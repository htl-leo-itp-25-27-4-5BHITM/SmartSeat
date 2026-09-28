# Spec Delta

## Purpose

Defines how people discover current learning-seat availability without authentication and how the view remains accurate as occupancy changes.

## ADDED Requirements

### Requirement: Public availability access
The system SHALL allow a user to view seat availability without signing in.

#### Scenario: Open availability view
- **WHEN** a user opens the SmartSeat public entry point
- **THEN** the system displays the current seat-availability view without requesting credentials

#### Scenario: Administrative functions remain separate
- **WHEN** an unauthenticated user uses the public availability view
- **THEN** the system does not expose controls that modify seats, configuration, accounts, or history

### Requirement: Seat availability presentation
The system SHALL present each configured seat with its stable identifier, display name, floor, wing, and canonical state of `FREE`, `OCCUPIED`, or `UNKNOWN`.

#### Scenario: Display free and occupied seats
- **WHEN** the current catalog contains seats in free and occupied states
- **THEN** the map and list distinguish those states consistently and provide a textual state indicator in addition to color

#### Scenario: Display unknown state
- **WHEN** the system cannot establish a trustworthy current state for a configured seat
- **THEN** the public view identifies the seat as `UNKNOWN` rather than reporting it as free

### Requirement: Location filtering and counts
The system SHALL allow users to filter seats by configured location and SHALL calculate free-seat counts from the same filtered dataset shown to the user.

#### Scenario: Filter by floor
- **WHEN** a user selects a floor
- **THEN** only seats assigned to that floor are shown and the free-seat count reflects those seats

#### Scenario: Location has no seats
- **WHEN** a user selects a known location that has no configured seats
- **THEN** the system shows an empty result and a free-seat count of zero without reporting an error

### Requirement: Live availability updates
The system SHALL deliver a complete current seat snapshot when a realtime connection is established and SHALL publish subsequent state or catalog changes to connected clients.

#### Scenario: Realtime state change
- **WHEN** an occupied seat becomes free or a free seat becomes occupied
- **THEN** connected public views update that seat without requiring a page reload

#### Scenario: Reconnect after interruption
- **WHEN** a client restores a lost realtime connection
- **THEN** the system sends a fresh complete snapshot before applying later incremental changes

### Requirement: Current occupancy duration
The system SHALL show the elapsed duration of the current occupancy interval for an occupied seat and SHALL omit that duration for free or unknown seats.

#### Scenario: Occupied seat duration
- **WHEN** a user inspects a seat with an active occupancy interval
- **THEN** the displayed elapsed duration is derived from the recorded interval start and the current time

#### Scenario: Seat becomes free
- **WHEN** an occupied seat transitions to free
- **THEN** the public view stops displaying an active occupancy duration for that seat

### Requirement: Location-specific QR entry
The system SHALL support QR-code URLs that open the public availability view with a configured location already selected.

#### Scenario: Valid location QR code
- **WHEN** a user follows a QR-code URL for a known location
- **THEN** the public view opens with that location filter applied

#### Scenario: Invalid location QR code
- **WHEN** a QR-code URL references an unknown location
- **THEN** the system shows the unfiltered availability view and explains that the requested location was not found

