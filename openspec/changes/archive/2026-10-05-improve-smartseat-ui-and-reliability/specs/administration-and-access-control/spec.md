# Spec Delta

## MODIFIED Requirements

### Requirement: Inactivity duration administration
An authenticated administrator SHALL be able to read and change the inactivity duration, the administrative interface SHALL identify the value as seconds, and the system SHALL accept only whole-second values greater than ten.

#### Scenario: Display duration value
- **WHEN** an authenticated administrator views the inactivity-duration control
- **THEN** the interface identifies the value and accepted input unit as seconds

#### Scenario: Valid duration update
- **WHEN** an administrator submits a whole-second duration greater than ten
- **THEN** the new duration is persisted and used for subsequent timeout evaluations

#### Scenario: Invalid duration update
- **WHEN** an administrator submits a duration of ten seconds or less or a non-integer value
- **THEN** the system rejects the request and preserves the existing duration

## ADDED Requirements

### Requirement: Responsive administrative workspace
The administrative workspace SHALL keep all authorized controls and results readable, reachable, and operable at supported desktop, laptop, and phone viewport sizes.

#### Scenario: Medium viewport
- **WHEN** the dashboard no longer fits its full wide-screen column layout
- **THEN** panels reflow without compressing inputs or buttons below a usable size

#### Scenario: Narrow viewport
- **WHEN** an administrator uses the dashboard on a phone-sized viewport
- **THEN** panels form a readable single-column flow and the page can scroll to every control

#### Scenario: Leaderboard exceeds available height
- **WHEN** leaderboard entries exceed the panel's available height
- **THEN** the entries can be scrolled without being clipped or covering other dashboard controls
