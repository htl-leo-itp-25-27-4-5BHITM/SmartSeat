# Spec Delta

## Purpose

Defines the stable seat and location catalog used by public availability, sensor mapping, administration, and historical reporting.

## ADDED Requirements

### Requirement: Stable seat identity
Each seat SHALL have a stable system identifier that remains unchanged when its display name or location metadata changes.

#### Scenario: Rename a seat
- **WHEN** an administrator changes a seat display name
- **THEN** the seat retains its identifier and existing history remains associated with it

### Requirement: Seat location metadata
Each seat SHALL reference a valid location containing at least a floor and wing value.

#### Scenario: Return seat location
- **WHEN** a client requests the seat catalog
- **THEN** each seat includes the floor and wing of its assigned location

#### Scenario: Invalid location assignment
- **WHEN** an administrative operation attempts to assign a seat to an unknown location
- **THEN** the system rejects the operation without changing the seat

### Requirement: Unique validated display names
The system SHALL require each seat display name to be non-blank and unique after trimming whitespace.

#### Scenario: Successful rename
- **WHEN** an authorized administrator submits a non-blank name that no other seat uses
- **THEN** the system updates the name and publishes the updated catalog

#### Scenario: Duplicate rename
- **WHEN** an authorized administrator submits a name already used by another seat
- **THEN** the system rejects the rename with a conflict response and preserves both existing names

### Requirement: Catalog-sized behavior
All availability, lifecycle, and analytics behavior SHALL derive the number and identifiers of seats from the configured catalog rather than assuming a fixed count or contiguous identifiers.

#### Scenario: More than five seats
- **WHEN** the catalog contains more than five seats
- **THEN** every configured seat can be displayed, updated, and included in analytics

#### Scenario: Non-contiguous identifiers
- **WHEN** configured seat identifiers contain a gap
- **THEN** the system processes the existing identifiers without creating placeholder seats or indexing failures

### Requirement: Unknown seat handling
Operations that reference a seat SHALL reject unknown identifiers without modifying another seat or creating implicit catalog entries.

#### Scenario: Unknown seat update
- **WHEN** a client or device submits an operation for an unknown seat identifier
- **THEN** the system leaves the catalog and all seat states unchanged and reports the rejected reference through the appropriate error channel

