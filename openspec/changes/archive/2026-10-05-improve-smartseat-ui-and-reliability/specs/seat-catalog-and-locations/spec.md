# Spec Delta

## MODIFIED Requirements

### Requirement: Unique validated display names
The system SHALL require each seat display name to be non-blank and unique after trimming whitespace, while treating submission of the seat's own unchanged normalized name as a successful no-op.

#### Scenario: Successful rename
- **WHEN** an authorized administrator submits a non-blank name that no other seat uses
- **THEN** the system updates the name and publishes the updated catalog

#### Scenario: Save unchanged name
- **WHEN** an authorized administrator submits the target seat's existing name with no normalized change
- **THEN** the system reports success, preserves the catalog, and does not publish a false error

#### Scenario: Duplicate rename
- **WHEN** an authorized administrator submits a name already used by another seat
- **THEN** the system rejects the rename with a conflict response and preserves both existing names

## ADDED Requirements

### Requirement: Seat map placement metadata
Each configured seat SHALL have map placement metadata sufficient to position its marker on the map for its assigned floor without relying on contiguous identifiers or fixed seat counts.

#### Scenario: Render catalog seat on its floor
- **WHEN** a configured seat includes valid floor and placement metadata
- **THEN** the public map displays its marker at the configured position on the matching floor map

#### Scenario: Missing or invalid placement
- **WHEN** a configured seat lacks valid map placement metadata
- **THEN** the seat remains available in a textual representation and the system does not place it at another seat's position
