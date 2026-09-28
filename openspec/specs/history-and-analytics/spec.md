# History and Analytics Specification

## Purpose

Defines the completed occupancy history and the deterministic calculations used for per-seat summaries, daily counts, and hourly utilization.

## Requirements

### Requirement: Completed occupancy interval history
Each completed occupancy interval SHALL produce one history record containing the stable seat identifier, interval start, interval end, and non-negative duration.

#### Scenario: Timeout completes interval
- **WHEN** an occupied seat is released by inactivity timeout
- **THEN** exactly one history record is stored for the completed interval

#### Scenario: Rename after history exists
- **WHEN** a seat is renamed after history has been recorded
- **THEN** the existing history remains associated with the same stable seat identifier

### Requirement: Per-seat average duration
The system SHALL calculate a seat's average from completed occupancy durations and SHALL identify the measure as occupancy duration rather than waiting time.

#### Scenario: Multiple completed intervals
- **WHEN** a seat has multiple completed intervals
- **THEN** its average equals the sum of their durations divided by their count

#### Scenario: No completed intervals
- **WHEN** a seat has no completed intervals in scope
- **THEN** the system returns an explicit no-data result rather than an internal error or invented zero-duration observation

### Requirement: Daily history counts
The system SHALL return the number of completed occupancy intervals for each configured seat within a requested local calendar date.

#### Scenario: Valid date with no history
- **WHEN** a valid date contains no completed intervals
- **THEN** the result contains zero counts for the configured seats or an explicitly documented empty result, consistently across clients

#### Scenario: Invalid date
- **WHEN** a client supplies a date outside the accepted ISO `yyyy-MM-dd` format
- **THEN** the system rejects it as a client error without running the aggregation

### Requirement: Hourly utilization
The system SHALL calculate each seat's hourly utilization as the sum of overlap between its completed occupancy intervals and each local clock hour, capped at one hour of utilization per seat-hour.

#### Scenario: Interval crosses an hour boundary
- **WHEN** an interval overlaps two clock hours
- **THEN** its duration is divided between the two corresponding hourly buckets

#### Scenario: Multiple intervals in one hour
- **WHEN** multiple non-overlapping intervals occur for one seat in the same hour
- **THEN** their overlap durations are summed rather than replacing one another

### Requirement: Catalog-derived analytics
Analytics SHALL derive seats from the catalog and SHALL not assume five seats, contiguous identifiers, or generated display names.

#### Scenario: Additional configured seat
- **WHEN** a new catalog seat has history
- **THEN** the seat is included in relevant analytics without a code change

### Requirement: Controlled synthetic history
Synthetic history generation SHALL be available only in an explicitly enabled development or test environment and SHALL require administrative authorization.

#### Scenario: Production request
- **WHEN** a client requests synthetic history generation in a production environment
- **THEN** the system rejects the request without creating history

#### Scenario: Invalid synthetic range
- **WHEN** an authorized development request has a minimum duration greater than or equal to its maximum
- **THEN** the system rejects the request without creating history

### Requirement: Consistent time zone
All date-based history queries and hourly calculations SHALL use one configured application time zone and SHALL document that zone in the external contract.

#### Scenario: Day boundary
- **WHEN** an interval ends near midnight
- **THEN** daily and hourly analytics assign it according to the configured application time zone

