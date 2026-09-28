# Device Operation Specification

## Purpose

Defines how the supported Raspberry Pi Pico 2 W motion-sensor unit starts, observes a seat, publishes data, reconnects, and exposes diagnostics.

## Requirements

### Requirement: Supported baseline device
The baseline occupancy device SHALL be a Raspberry Pi Pico 2 W connected to one motion sensor whose signal output is wired to GPIO 18, with appropriate power and ground connections.

#### Scenario: Correctly wired startup
- **WHEN** the device receives stable USB power and the sensor is wired to the documented pins
- **THEN** the firmware initializes the GPIO input without requiring an interactive start command

### Requirement: Network startup
The device SHALL use externally supplied Wi-Fi and MQTT configuration, connect to the configured network, and establish an MQTT session before publishing observations.

#### Scenario: Network available
- **WHEN** configured Wi-Fi and MQTT services are reachable
- **THEN** the device connects and becomes ready to publish observations

#### Scenario: Network unavailable at boot
- **WHEN** Wi-Fi or MQTT is unavailable during startup
- **THEN** the device reports the failure through diagnostics and continues bounded reconnection attempts without crashing

### Requirement: Seat-specific observation publication
Each device SHALL be configured with one seat identifier and SHALL publish observations using the sensor-ingestion contract when motion state changes.

#### Scenario: Motion begins
- **WHEN** the motion sensor changes from no motion to motion detected
- **THEN** the device publishes the configured seat identifier with `status=false`

#### Scenario: Motion ends
- **WHEN** the motion sensor changes from motion detected to no motion
- **THEN** the device publishes the configured seat identifier with `status=true`

### Requirement: Continued occupancy refresh
While motion remains detected, the device SHALL publish observations often enough to prevent a genuinely occupied seat from expiring solely because no edge transition occurred.

#### Scenario: Continuous motion state
- **WHEN** a seat remains occupied longer than the configured inactivity duration
- **THEN** periodic valid observations refresh the backend last-observed time without creating another interval

### Requirement: Connection recovery
The device SHALL recover from temporary Wi-Fi or MQTT loss and resume publication without requiring a power cycle under normal recoverable conditions.

#### Scenario: MQTT connection drops
- **WHEN** an established MQTT connection is lost
- **THEN** the device retries with bounded backoff and publishes the current observation after reconnecting

### Requirement: Local diagnostics
The device SHALL make startup, connection, publication, and unrecoverable error status observable through its serial diagnostic output without printing reusable secrets.

#### Scenario: Inspect serial output
- **WHEN** an operator connects a serial monitor to the device
- **THEN** the output identifies operational state and failures without revealing Wi-Fi or MQTT passwords

### Requirement: Prototype alternatives remain excluded
NFC, Tuya, ultrasonic, button, and infrared-barrier devices SHALL NOT be required for baseline operation unless adopted by a later approved specification change.

#### Scenario: Baseline acceptance test
- **WHEN** SmartSeat is tested against this baseline
- **THEN** the test does not require any excluded proof-of-concept sensor technology

