# Spec Delta

## ADDED Requirements

### Requirement: Unambiguous active interval timestamps
The system SHALL expose active occupancy interval start times as ISO 8601 timestamps containing `Z` or an explicit UTC offset and SHALL use a consistent clock basis for lifecycle calculations.

#### Scenario: Publish occupied state
- **WHEN** the system publishes a seat with an active occupancy interval
- **THEN** the interval start timestamp identifies an unambiguous instant independent of server and client timezone settings

#### Scenario: Daylight-saving transition
- **WHEN** an occupancy interval spans a daylight-saving offset change
- **THEN** elapsed duration is calculated from absolute instants and remains continuous

#### Scenario: Server and client use different timezones
- **WHEN** the server and public client use different local timezone settings
- **THEN** both calculate the same elapsed occupancy duration for the published interval
