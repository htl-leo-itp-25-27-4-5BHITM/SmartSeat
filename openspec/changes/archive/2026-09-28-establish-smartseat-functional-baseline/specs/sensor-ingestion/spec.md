# Spec Delta

## Purpose

Defines the MQTT observation contract and the validation and reliability rules that convert sensor messages into trustworthy seat-lifecycle input.

## ADDED Requirements

### Requirement: MQTT observation contract
The ingestion service SHALL consume JSON observations from the configured `pico-data` channel containing a seat `id` and boolean `status`. For compatibility, a numeric identifier or a string containing only a positive integer SHALL be accepted. `status=false` SHALL mean occupancy detected; `status=true` SHALL mean no active motion and SHALL not by itself release the seat.

#### Scenario: Occupancy observation
- **WHEN** the service receives `{"id":"1","status":false}` for a configured seat
- **THEN** it submits a valid occupancy observation to that seat's lifecycle

#### Scenario: No-motion observation
- **WHEN** the service receives a valid observation with `status=true`
- **THEN** it does not immediately release an occupied seat and allows inactivity timeout rules to determine release

### Requirement: Payload validation
The ingestion service SHALL reject malformed JSON, missing fields, non-boolean status values, non-positive identifiers, and identifiers that do not map to a configured seat.

#### Scenario: Malformed message
- **WHEN** a message cannot be parsed according to the observation contract
- **THEN** no seat state changes and the rejection is logged without exposing credentials or secrets

#### Scenario: Unknown device mapping
- **WHEN** a syntactically valid observation references an unknown seat identifier
- **THEN** no catalog entry or seat state is created implicitly and the unknown mapping is recorded

### Requirement: Duplicate delivery tolerance
The ingestion service SHALL tolerate duplicate delivery without creating duplicate occupancy intervals or history records.

#### Scenario: Duplicate occupied message
- **WHEN** the same occupied observation is delivered more than once while the seat is occupied
- **THEN** the active interval remains singular and only the last-observed time may be refreshed

### Requirement: Retained and stale message safety
Retained or otherwise stale observations SHALL NOT keep a seat occupied indefinitely or start a new interval when their freshness cannot be established.

#### Scenario: Stale retained occupancy
- **WHEN** the service subscribes and receives an occupied retained message that cannot be established as current
- **THEN** it does not start or extend an occupancy interval solely from that message

### Requirement: Ingestion continuity
Temporary MQTT disconnection SHALL not corrupt existing seat state, and the service SHALL resume validation and processing after reconnection.

#### Scenario: Broker unavailable
- **WHEN** the MQTT broker becomes unavailable
- **THEN** the service records the connectivity failure and leaves existing seat states to their lifecycle timeout rules

#### Scenario: Broker reconnects
- **WHEN** the MQTT connection is restored
- **THEN** the service resumes consuming observations without creating duplicate intervals from redelivery

