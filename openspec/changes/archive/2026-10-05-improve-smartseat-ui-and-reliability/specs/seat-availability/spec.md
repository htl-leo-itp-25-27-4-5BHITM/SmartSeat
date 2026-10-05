# Spec Delta

## MODIFIED Requirements

### Requirement: Location filtering and counts
The system SHALL present every configured floor in a separately labeled map in the default public map view, SHALL allow a location-specific entry point to focus the requested location, and SHALL calculate each displayed free-seat count from the same seat dataset shown for that location.

#### Scenario: Default multi-floor overview
- **WHEN** a user opens the default public map view and the catalog contains seats on `1.OG` and `2.OG`
- **THEN** the system shows labeled maps for `1.OG` and `2.OG` simultaneously with the seats and free-seat count belonging to each floor

#### Scenario: Filter by floor
- **WHEN** a user or location-specific entry point focuses a configured floor
- **THEN** the interface emphasizes that floor and its count while preserving access to the complete multi-floor overview

#### Scenario: Location-specific focus
- **WHEN** a user opens the availability view through a valid location-specific entry point
- **THEN** the requested location is focused without mislabeling or removing the other configured location data

#### Scenario: Location has no seats
- **WHEN** a configured floor has no seats
- **THEN** its labeled map shows an empty state and a free-seat count of zero without reporting an error

### Requirement: Current occupancy duration
The system SHALL show the non-negative elapsed duration of the current occupancy interval for an occupied seat, calculated from an unambiguous recorded interval start and the current time, and SHALL omit that duration for free or unknown seats.

#### Scenario: Newly occupied seat
- **WHEN** a free seat becomes occupied and a user inspects it immediately
- **THEN** the displayed duration begins near zero and does not include a client/server timezone offset

#### Scenario: Occupied seat duration
- **WHEN** a user inspects a seat with an active occupancy interval
- **THEN** the displayed non-negative elapsed duration is derived from the recorded absolute interval start and the current instant

#### Scenario: Repeated occupancy update
- **WHEN** another occupancy observation is accepted for an already occupied seat
- **THEN** the displayed duration continues from the original interval start instead of restarting

#### Scenario: Seat becomes free
- **WHEN** an occupied seat transitions to free
- **THEN** the public view stops displaying an active occupancy duration for that seat

## ADDED Requirements

### Requirement: Public view navigation state
The system SHALL keep the Map, List, and Chart views available and SHALL visibly and programmatically identify which view is active.

#### Scenario: Switch public view
- **WHEN** a user activates Map, List, or Chart
- **THEN** only the selected view is presented as current and its navigation control is visually distinct from the other controls

#### Scenario: Keyboard navigation
- **WHEN** a keyboard user moves through and activates the public navigation controls
- **THEN** focus remains visible and the active view can be determined without relying only on color

### Requirement: Responsive multi-floor presentation
The public availability interface SHALL keep floor labels, seat states, counts, navigation, and seat details readable and reachable across supported viewport sizes.

#### Scenario: Wide viewport
- **WHEN** the viewport has sufficient horizontal space
- **THEN** the `1.OG` map is presented to the left of the `2.OG` map

#### Scenario: Narrow viewport
- **WHEN** the two maps cannot retain their minimum readable size side by side
- **THEN** the maps stack vertically without clipping content or requiring horizontal page scrolling

#### Scenario: Seat details without hover
- **WHEN** a user cannot or does not use pointer hover
- **THEN** the seat name, state, and active duration remain available through focus, activation, or equivalent visible text
