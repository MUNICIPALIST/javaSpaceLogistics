#!/bin/bash

# Function to create Dockerfile if it doesn't exist
create_dockerfile() {
  local service_dir=$1
  local service_name="${service_dir//-service/}"
  local port=$2
  
  # Create service directory and resources if they don't exist
  if [ ! -d "${service_dir}" ]; then
    echo "Creating directory structure for ${service_dir}..."
    mkdir -p "${service_dir}/src/main/java/com/spacelogistics/${service_name}"
    mkdir -p "${service_dir}/src/main/resources"
    
    # Create application.yml for service
    if [ "$service_dir" = "eureka-server" ]; then
      cat > "${service_dir}/src/main/resources/application.yml" << EOF
server:
  port: 8761

spring:
  application:
    name: eureka-server
  # Exclude DB config for Eureka
  autoconfigure:
    exclude: 
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
      - org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    wait-time-in-ms-when-sync-empty: 0
EOF
    else
      # For other services
      cat > "${service_dir}/src/main/resources/application.yml" << EOF
server:
  port: ${port}

spring:
  application:
    name: ${service_dir}

eureka:
  client:
    service-url:
      defaultZone: http://eureka-server:8761/eureka/
  instance:
    prefer-ip-address: true
EOF
    fi
  fi
  
  # Create a standard Dockerfile
  if [ ! -f "${service_dir}/Dockerfile" ]; then
    echo "Creating Dockerfile for ${service_dir}..."
    cat > "${service_dir}/Dockerfile" << EOF
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app
# Copy the JAR from the build output directory relative to the context
COPY build/libs/*.jar app.jar
EXPOSE ${port}
ENTRYPOINT ["java", "-jar", "app.jar"]
EOF
    echo "Dockerfile created for ${service_dir}"
  fi
}

# Create a simple Spring Boot application class
create_sample_app() {
  local service_dir=$1
  local service_name="${service_dir//-service/}"
  # Create a Java-compatible name by removing hyphens
  local service_name_java="${service_name//-/}" 
  local class_name="$(tr '[:lower:]' '[:upper:]' <<< ${service_name_java:0:1})${service_name_java:1}ServiceApplication"
  local package_dir="${service_dir}/src/main/java/com/spacelogistics/${service_name_java}" # Use Java-compatible name
  
  mkdir -p "$package_dir"
  
  # Create main application class
  cat > "${package_dir}/${class_name}.java" << EOF
package com.spacelogistics.${service_name_java};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
public class ${class_name} {
    public static void main(String[] args) {
        SpringApplication.run(${class_name}.class, args);
    }
    
    @RestController
    static class HelloController {
        @GetMapping("/")
        public String hello() {
            return "${service_dir} is running!";
        }
    }
}
EOF

  # Special case for eureka-server
  if [ "$service_dir" = "eureka-server" ]; then
    # Overwrite with Eureka Server specific annotations and NO HelloController
    cat > "${package_dir}/${class_name}.java" << EOF
package com.spacelogistics.${service_name_java};

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
// Removed GetMapping and RestController imports

@SpringBootApplication
@EnableEurekaServer
public class ${class_name} {
    public static void main(String[] args) {
        SpringApplication.run(${class_name}.class, args);
    }
    
    // Removed conflicting HelloController
}
EOF
  fi

  # Create a dummy jar file if needed
  if [ ! -f "${service_dir}/build/libs/${service_dir}.jar" ]; then
    echo "Creating dummy JAR file for ${service_dir}..."
    mkdir -p "${service_dir}/build/libs"
    touch "${service_dir}/build/libs/${service_dir}.jar"
  fi
}

# Update docker-compose.yml to fix Kafka configuration
update_docker_compose() {
  # Check if KAFKA_ZOOKEEPER_CONNECT exists in docker-compose.yml
  if ! grep -q "KAFKA_ZOOKEEPER_CONNECT" docker-compose.yml; then
    echo "Updating docker-compose.yml for Kafka..."
    # Use a temporary file for sed compatibility on macOS
    sed -i '.bak' 's/KAFKA_BROKER_ID: 1/KAFKA_BROKER_ID: 1\\n      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181/g' docker-compose.yml && rm docker-compose.yml.bak
  fi
}

# Create Dockerfiles and directories for all services
# Ensure sample apps are created *before* building
create_sample_app "eureka-server"
create_sample_app "cargo-service"
create_sample_app "launch-service"
create_sample_app "destination-service"
create_sample_app "telemetry-service"

create_dockerfile "eureka-server" "8761"
create_dockerfile "cargo-service" "8081"
create_dockerfile "launch-service" "8082"
create_dockerfile "destination-service" "8083"
create_dockerfile "telemetry-service" "8084"

# Update docker-compose.yml
update_docker_compose

# Build all services using Gradle
echo "Building all services with Gradle..."
./gradlew clean bootJar
if [ $? -ne 0 ]; then
  echo "Gradle build failed!"
  exit 1
fi

# Run docker-compose
echo "Starting Docker Compose..."
docker compose up --build # Add --build to ensure images are rebuilt
