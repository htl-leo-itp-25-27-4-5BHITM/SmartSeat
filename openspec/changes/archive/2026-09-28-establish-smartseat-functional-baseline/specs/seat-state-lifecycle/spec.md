# Spec Delta

## Purpose

Defines the authoritative occupancy state machine, timing rules, corrections, history completion, and observable state-change behavior for every seat.

## ADDED Requirements

### Requirement: Canonical seat states
The system SHALL represent a configured seat as exactly one of `FREE`, `OCCUPIED`, or `UNKNOWN` and SHALL not expose storage-specific boolean meanings as the functional contract.

#### Scenario: Known free state
- **WHEN** a seat has no active occupancy interval and its state is trustworthy
- **THEN** the authoritative state is `FREE`

#### Scenario: State cannot be trusted
- **WHEN** required state data is missing or inconsistent after recovery
- **THEN** the authoritative state is `UNKNOWN` until a valid transition establishes a known state

### Requirement: Start an occupancy interval
A valid occupancy observation for a free seat SHALL transition it to `OCCUPIED`, record the interval start time once, refresh its last-observed time, and publish the new state.

#### Scenario: First occupancy observation
- **WHEN** the system accepts an occupancy observation for a free seat
- **THEN** one active occupancy interval begins and connected clients receive the occupied state

#### Scenario: Repeated occupancy observation
- **WHEN** the system accepts another occupancy observation for an already occupied seat
- **THEN** it refreshes the last-observed time without replacing the original interval start or creating another interval

### Requirement: Inactivity release
An occupied seat SHALL transition to `FREE` when no valid occupancy observation has been accepted for the configured inactivity duration.

#### Scenario: Timeout expires
- **WHEN** the configured inactivity duration elapses after the last accepted occupancy observation
- **THEN** the system ends the active interval, stores its history, marks the seat free, and publishes the free state

#### Scenario: Observation before timeout
- **WHEN** a valid occupancy observation arrives before the inactivity duration elapses
- **THEN** the seat remains occupied and the timeout is measured again from the new observation

### Requirement: Administrative state correction
An authorized administrator SHALL be able to correct a seat state, and the correction SHALL follow the same interval and publication invariants as sensor-driven transitions.

#### Scenario: Correct occupied seat to free
- **WHEN** an administrator marks an occupied seat free
- **THEN** the system ends and records the active interval, clears active timing state, and publishes the correction

#### Scenario: Correct free seat to occupied
- **WHEN** an administrator marks a free seat occupied
- **THEN** the system starts one new interval using the correction time and publishes the correction

### Requirement: Atomic lifecycle completion
The system SHALL persist the completed history interval and authoritative free state as one logical operation before broadcasting completion.

#### Scenario: Persistence fails during release
- **WHEN** the system cannot persist a timeout or administrative release
- **THEN** it does not broadcast a successfully completed transition and records an operational error

### Requirement: Active interval recovery
The system SHALL preserve enough active-interval information to recover consistent seat states after an application restart.

#### Scenario: Restart with active interval
- **WHEN** the application restarts while a seat has a persisted active occupancy interval
- **THEN** it restores the occupied state and timing information or marks the seat `UNKNOWN` if consistency cannot be established

