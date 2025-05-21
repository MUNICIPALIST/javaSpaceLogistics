# Starship Logistics — Система управления космическими грузами 🛰️

---

## Описание проекта

**Starship Logistics** — это микросервисное приложение для управления логистикой грузов для космических станций, лунных/марсианских баз и спутников. Проект реализован на Java 21 с использованием Spring Boot 3 и лучших практик промышленной разработки.

---

## Архитектурный стиль и паттерны

- **Микросервисная архитектура** — каждый сервис независим, может масштабироваться и разворачиваться отдельно.
- **Event-driven architecture** — асинхронное взаимодействие через Apache Kafka.
- **RESTful API** — синхронное взаимодействие через HTTP/Feign.
- **Service Discovery (Eureka)** — автоматическое обнаружение сервисов.
- **Database per Service** — каждый сервис может иметь свою БД (в учебных целях допустимо использовать одну).
- **DTO, Repository, Publisher-Subscriber, Circuit Breaker** — для чистоты архитектуры и устойчивости.

---

## Технологии

- Java 21, Spring Boot 3
- PostgreSQL (основная БД)
- Apache Kafka (брокер событий)
- Docker Compose (оркестрация инфраструктуры)
- Eureka (Service Discovery)
- Gradle (сборка)

---

## Структура проекта

```
javaSpaceLogistics/
│
├── eureka-server/
│   └── src/...
│
├── cargo-service/
│   ├── src/main/java/com/space/cargo/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── model/
│   │   └── dto/
│   └── build.gradle
│
├── launch-service/
│   └── аналогичная структура
│
├── destination-service/
│   └── аналогичная структура
│
├── telemetry-service/
│   └── аналогичная структура
│
├── docker-compose.yml
├── .env
└── README.md
```

---

## Сервисы и их назначение

### 1. eureka-server
- **Назначение:** Сервис-реестр для обнаружения всех микросервисов.
- **Методы:** Нет бизнес-методов, только регистрация/обнаружение.

### 2. cargo-service 📦
- **Назначение:** Управление грузами (CRUD, поиск по месту назначения, обновление статуса).
- **Методы:**
  - `POST /cargo` — создать новый груз
  - `GET /cargo/{id}` — получить информацию о грузе
  - `GET /cargo` — получить список грузов
  - `PUT /cargo/{id}` — обновить информацию о грузе
  - `DELETE /cargo/{id}` — удалить груз
  - `GET /cargo/destination/{destinationId}` — получить грузы по месту назначения
- **Взаимодействие:**
  - Сохраняет данные в PostgreSQL
  - Отправляет события в Kafka при изменении статуса

### 3. launch-service 🚀
- **Назначение:** Управление запусками грузов (планирование, запуск, отслеживание).
- **Методы:**
  - `POST /launch` — создать запуск
  - `GET /launch/{id}` — получить запуск
  - `GET /launch` — список запусков
  - `PUT /launch/{id}/status` — изменить статус запуска
  - `GET /launch/cargo/{cargoId}` — получить запуск по грузу
- **Взаимодействие:**
  - Получает информацию о грузах через Feign-клиент к Cargo Service
  - Публикует события в Kafka (например, запуск начат/завершён)

### 4. destination-service 🌍
- **Назначение:** Управление пунктами назначения (космические станции, базы).
- **Методы:**
  - `POST /destination` — добавить новое место назначения
  - `GET /destination/{id}` — получить информацию о месте
  - `GET /destination` — список мест назначения
  - `PUT /destination/{id}` — обновить место назначения
  - `DELETE /destination/{id}` — удалить место назначения

### 5. telemetry-service 📊
- **Назначение:** Сбор и мониторинг телеметрии полёта (скорость, траектория, остаток топлива и т.д.).
- **Методы:**
  - `POST /telemetry` — добавить телеметрию
  - `GET /telemetry/{launchId}` — получить телеметрию по запуску
  - `GET /telemetry` — общая телеметрия

---

## Сценарии взаимодействия сервисов

1. Пользователь создаёт груз через Cargo Service.
2. Launch Service планирует запуск для этого груза, рассчитывает топливо и время.
3. Launch Service отправляет событие LaunchStarted в Kafka.
4. Telemetry Service подписан на этот топик и начинает мониторинг полёта.
5. По завершению полёта Telemetry Service отправляет событие LaunchCompleted.
6. Cargo Service обновляет статус груза на DELIVERED.

---

## Kafka — зачем и как используется

- **Назначение:** Асинхронная интеграция между сервисами, масштабируемость, отказоустойчивость.
- **Использование:**
  - Каждый сервис может быть продюсером или консьюмером событий.
  - Примеры событий: CargoCreated, LaunchStarted, LaunchCompleted, TelemetryUpdated.
  - Сервисы подписываются на нужные топики и реагируют на события.

---

## База данных: отдельная или общая?

- **Best practice:** Каждый сервис должен иметь свою БД (Database per Service).

---

## Docker Compose: зачем и как используется

- **Зачем:** Для локального запуска Kafka, Zookeeper, Eureka одной командой.
- **Что включает:** Контейнеры для Kafka, Zookeeper, Eureka. PostgreSQL не запускается, т.к. уже есть.
- **Пример:**
  - `docker-compose up` — запускает Kafka, Zookeeper, Eureka.

---

## .env файл (пример)

```
DB_NAME=javalogistics
DB_USER=postgresql
DB_PASSWORD=(9IeuwbxaDwckDue
DB_HOST=176.123.179.219
DB_PORT=5432
KAFKA_BOOTSTRAP_SERVERS=localhost:9092
EUREKA_SERVER=http://localhost:8761/eureka
```

---

## Описание данных (Entity/DTO)

### Cargo
```java
class CargoDTO {
    UUID id;
    String name;
    double weight;
    UUID destinationId;
    CargoStatus status; // CREATED, IN_TRANSIT, DELIVERED
    LocalDateTime createdAt;
}
```

### Launch
```java
class LaunchDTO {
    UUID id;
    UUID cargoId;
    LocalDateTime launchDate;
    LaunchStatus status; // PLANNED, LAUNCHED, COMPLETED, FAILED
    double fuelRequired;
    LocalDateTime estimatedArrival;
}
```

### Destination
```java
class DestinationDTO {
    UUID id;
    String name;
    DestinationType type; // ORBITAL_STATION, MOON_BASE, MARS_BASE, etc
    String coordinates;
}
```

### Telemetry
```java
class TelemetryDTO {
    UUID id;
    UUID launchId;
    LocalDateTime timestamp;
    double speed;
    double fuelLevel;
    String position;
}
```

---

## Пример docker-compose.yml

```yaml
version: '3.8'
services:
  zookeeper:
    image: wurstmeister/zookeeper:3.4.6
    ports:
      - "2181:2181"
  kafka:
    image: wurstmeister/kafka:2.12-2.2.1
    ports:
      - "9092:9092"
    environment:
      KAFKA_ADVERTISED_HOST_NAME: localhost
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    depends_on:
      - zookeeper
  eureka-server:
    image: eurekaserver:latest
    build: ./eureka-server
    ports:
      - "8761:8761"
```

---

## Лучшие практики

- Микросервисы = независимость, масштабируемость, отказоустойчивость.
- Kafka = асинхронность, надёжная доставка событий.
- Eureka = автоматическая маршрутизация и отказоустойчивость.
- Docker Compose = быстрая разработка и деплой.
- Чистая архитектура, DTO, разделение ответственности.

---

