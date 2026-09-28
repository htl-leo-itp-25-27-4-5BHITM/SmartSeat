# Proposal

## Why

SmartSeat has working software, device firmware, design material, and project documentation, but it has no durable OpenSpec requirements that define the intended product behavior. A functional baseline is needed before agentic implementation can safely extend or correct the system without treating implementation quirks, prototypes, or deprecated ideas as requirements.

## What Changes

- Establish a traceable functional baseline for public seat availability, seat and location data, occupancy state transitions, sensor ingestion, administration, analytics, device behavior, and runtime operation.
- Distinguish confirmed product behavior from current implementation details, prototype alternatives, and unresolved product decisions.
- Define normal, boundary, failure, recovery, security, and persistence scenarios for each capability.
- Record shared terminology, personas, system boundaries, state semantics, and cross-capability decisions in one design artifact.
- Provide specification-authoring and validation tasks that can be completed sequentially across separate threads without changing application code.
- **BREAKING (product scope)**: Deprecate and exclude direct communication, chat, messaging, announcements, and notifications between teachers and students. Historical proposal text and UI designs that depict these functions are evidence of removed scope only and do not define current or future requirements.
- Treat Tuya, ultrasonic, NFC, and other proof-of-concept hardware alternatives as non-baseline options unless a later product decision explicitly adopts them.

## Capabilities

### New Capabilities

- `seat-availability`: Public discovery of free and occupied seats through map, list, filtering, counts, current occupancy information, and live updates.
- `seat-catalog-and-locations`: Seat identity, naming, location metadata, catalog constraints, and administrative maintenance of that data.
- `seat-state-lifecycle`: Authoritative free and occupied state transitions, occupancy timing, inactivity release, manual correction, history creation, and update broadcasts.
- `sensor-ingestion`: MQTT message contract, device-to-seat mapping, validation, duplicate and stale signal handling, and ingestion failure behavior.
- `administration-and-access-control`: Administrator authentication, authorization, session behavior, protected operations, seat renaming, and timeout configuration.
- `history-and-analytics`: Occupancy interval history, per-seat averages, daily counts, hourly utilization, leaderboard behavior, and analytics edge cases.
- `device-operation`: Raspberry Pi Pico startup, connectivity, motion sensing, message publication, reconnection, diagnostics, wiring, and failure behavior.
- `runtime-and-deployment`: Application, database, MQTT, logging, configuration, persistence, startup, health, recovery, and deployment requirements.

### Modified Capabilities

None. The project has no existing durable OpenSpec capabilities.

## Impact

- Adds planning artifacts under `openspec/changes/establish-smartseat-functional-baseline/` and, after review and archival, new durable specifications under `openspec/specs/`.
- Establishes contracts for the Quarkus REST and WebSocket interfaces, PostgreSQL persistence, MQTT ingestion, browser interfaces, and Raspberry Pi Pico firmware.
- Identifies existing implementation behavior that requires later product decisions or implementation changes, including ambiguous status semantics, timeout-based release, hard-coded seat counts, client-only dashboard gating, destructive schema initialization, and analytics aggregation rules.
- Does not modify Java, JavaScript, HTML, CSS, C/C++, database, container, or device code. Implementation work requires a separate apply request after the planning artifacts are reviewed.
