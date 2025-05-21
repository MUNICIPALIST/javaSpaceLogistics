#!/bin/sh
if [ ! -f app.jar ]; then
  echo "cargo-service is starting in demo mode..."
  # Create a simple JAR file that just prints a message
  echo "echo 'cargo-service service is running in demo mode'" > /app/demo.sh
  chmod +x /app/demo.sh
  exec /app/demo.sh
else
  exec java -jar app.jar
fi
