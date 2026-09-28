# Runtime and Deployment Specification

## Purpose

Defines the operational environment that keeps SmartSeat data durable, configuration externalized, dependencies observable, and service recovery predictable.

## Requirements

### Requirement: Durable production data
Production seat catalog, configuration, active occupancy information, accounts, and history SHALL survive application and database container restarts.

#### Scenario: Application restart
- **WHEN** the application restarts normally
- **THEN** previously committed production data remains available and consistent

#### Scenario: Production startup
- **WHEN** the production service starts
- **THEN** it uses non-destructive schema migration or validation rather than dropping and recreating the schema

### Requirement: Externalized configuration and secrets
Database, MQTT, network, logging, and authentication settings SHALL be supplied through deployment configuration, and reusable secrets SHALL not be committed as production defaults.

#### Scenario: Missing required secret
- **WHEN** a required production credential is absent
- **THEN** the affected component fails readiness with a diagnostic that does not reveal another secret

### Requirement: Dependency health
The runtime SHALL expose health information that distinguishes application liveness from readiness of required dependencies such as the database and MQTT connection.

#### Scenario: Database unavailable
- **WHEN** the application process is running but cannot use its database
- **THEN** liveness may remain healthy while readiness reports the database failure

#### Scenario: MQTT unavailable
- **WHEN** the broker is unavailable
- **THEN** health information reports degraded ingestion while read-only behavior backed by available persisted data remains observable

### Requirement: Operational logging
The runtime SHALL log application startup, dependency changes, rejected sensor messages, lifecycle failures, realtime connection failures, and administrative audit events with timestamps and useful identifiers.

#### Scenario: Sensor message rejected
- **WHEN** ingestion rejects a message
- **THEN** the logs identify the rejection category and affected identifier when safely available without recording credentials

### Requirement: Realtime recovery
The runtime SHALL allow clients to reconnect to the seat-update channel after application or network interruption and obtain a fresh authoritative snapshot.

#### Scenario: Application is redeployed
- **WHEN** the service becomes available after redeployment
- **THEN** reconnecting clients receive the current catalog and states before later updates

### Requirement: Environment-specific development data
Schema recreation, seeded credentials, and synthetic history SHALL be limited to explicitly selected development or test profiles.

#### Scenario: Development profile
- **WHEN** an operator explicitly starts a development profile
- **THEN** documented seed or schema-reset behavior may run without changing production policy

#### Scenario: Production profile
- **WHEN** the production profile starts
- **THEN** the runtime neither recreates the schema nor installs documented development credentials

