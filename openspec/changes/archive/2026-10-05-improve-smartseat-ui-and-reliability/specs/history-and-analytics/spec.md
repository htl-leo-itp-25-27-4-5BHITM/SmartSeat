# Spec Delta

## ADDED Requirements

### Requirement: Understandable analytics presentation
The system SHALL present hourly utilization as a percentage from 0 through 100, identify the time and utilization units, and explain that the value represents occupied time within each seat-hour.

#### Scenario: Display hourly utilization
- **WHEN** analytics for a date are available
- **THEN** the chart labels its time axis, identifies utilization as a percentage, and associates every series with the catalog seat display name

#### Scenario: No analytics for date
- **WHEN** the selected date has no completed occupancy data
- **THEN** the analytics view shows an explicit no-data message rather than a misleading zero-valued chart or an internal error

### Requirement: Accessible analytics values
Every value represented graphically SHALL also be obtainable without precise pointer hovering, and the presentation SHALL remain usable when series values differ substantially.

#### Scenario: Inspect an hour with pointer or keyboard
- **WHEN** a user focuses, activates, or points near an hourly position
- **THEN** the system exposes the hour and values for all applicable seat series at that position

#### Scenario: Request textual values
- **WHEN** a user accesses the analytics data without using the graphical canvas
- **THEN** the same seat, hour, and percentage values are available in a readable textual representation

#### Scenario: Large differences between values
- **WHEN** one displayed value is substantially larger than another
- **THEN** both values remain discoverable and are not made unreachable by the larger visual mark
