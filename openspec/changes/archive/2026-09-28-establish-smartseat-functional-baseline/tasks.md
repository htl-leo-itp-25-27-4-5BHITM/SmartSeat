# Tasks

## 1. Scope and Evidence Baseline

- [x] 1.1 Inventory the OpenSpec root, README, documentation folder, UI artifacts, relevant source, tests, configuration, firmware, and hardware models; verify the evidence areas are summarized in `design.md`
- [x] 1.2 Compare implemented behavior with documented intent and verify `design.md` records the evidence-precedence rule and the major implementation gaps
- [x] 1.3 Lock the approved product boundary and verify the proposal, design, and capability list contain no unapproved capability

## 2. Shared Functional Model

- [x] 2.1 Define stable seat identity, catalog-derived cardinality, and location semantics; verify the catalog spec covers rename, duplicate, unknown-id, non-contiguous-id, and more-than-five-seat scenarios
- [x] 2.2 Define the `FREE`, `OCCUPIED`, and `UNKNOWN` state model; verify lifecycle scenarios cover first observation, repeated observation, timeout, correction, atomic completion, and restart recovery
- [x] 2.3 Define MQTT boolean compatibility and timeout semantics; verify ingestion scenarios cover malformed, unknown, duplicate, retained, disconnected, and reconnected messages
- [x] 2.4 Define interval and time semantics; verify analytics scenarios cover averages, empty data, invalid dates, multiple intervals per hour, catalog-derived seats, and day boundaries

## 3. Capability Specifications

- [x] 3.1 Create `seat-availability` requirements and verify public access, presentation, filtering, counts, realtime recovery, duration display, and QR entry each have scenarios
- [x] 3.2 Create `seat-catalog-and-locations` and `seat-state-lifecycle` requirements and verify every requirement uses normative language and at least one scenario
- [x] 3.3 Create `sensor-ingestion` and `device-operation` requirements and verify the wire contract and Pico behavior agree on identifier and status meanings
- [x] 3.4 Create `administration-and-access-control` requirements and verify authentication, server-side authorization, expiry, logout, credential protection, configuration validation, and audit scenarios are present
- [x] 3.5 Create `history-and-analytics` requirements and verify calculations do not assume five seats or use maximum overlap in place of summed utilization
- [x] 3.6 Create `runtime-and-deployment` requirements and verify production durability, external configuration, health, logging, recovery, and environment separation are covered

## 4. Cross-Capability Review

- [x] 4.1 Review all eight specs for shared terminology and verify state names, seat identity, time zone, authorization, and error semantics are consistent
- [x] 4.2 Walk through public discovery, sensor occupancy, inactivity release, administrative correction, analytics, broker outage, application restart, and realtime reconnection; verify every step maps to at least one requirement and scenario
- [x] 4.3 Compare the proposal capability list with the eight spec paths and verify there are no missing, duplicate, or undeclared capability deltas
- [x] 4.4 Run the deprecated-scope audit and verify excluded historical concepts appear only in the proposal non-goal and design exclusion record, never in capability requirements or implementation work

## 5. Decision Review

- [x] 5.1 Review the baseline choices for three-state availability, `status=false` occupancy evidence, timeout release, and retained-message freshness; record any approved revision in design and affected specs, then rerun strict validation
- [x] 5.2 Review the choices for location-specific QR entry, Pico plus PIR hardware, the greater-than-ten-second timeout rule, and the `Europe/Vienna` default time zone; record any approved revision and verify affected scenarios remain testable
- [x] 5.3 Review server-side administrative sessions, audit records, production schema migration, and development-only synthetic data; verify the resulting contracts clearly distinguish target behavior from current gaps

## 6. Validation and Publication

- [x] 6.1 Run `openspec validate establish-smartseat-functional-baseline --type change --strict --json` and resolve every reported error or warning; verify the final result is valid
- [x] 6.2 Inspect `git diff -- openspec/changes/establish-smartseat-functional-baseline` and verify the change contains planning artifacts only and no runtime source modifications
- [x] 6.3 Obtain stakeholder approval for the proposal, design decisions, and capability requirements; verify all review changes are captured and tasks 4.1 through 6.2 are complete
- [x] 6.4 Archive the completed documentation-only change with the OpenSpec archive workflow and verify the eight durable specifications exist under `openspec/specs/`
