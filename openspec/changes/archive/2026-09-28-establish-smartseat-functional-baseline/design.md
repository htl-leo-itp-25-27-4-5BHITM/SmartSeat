# Design

## Context

See `proposal.md` for motivation and scope. SmartSeat currently combines a Quarkus application, static browser clients, PostgreSQL, MQTT ingestion, a WebSocket update channel, and Raspberry Pi Pico firmware. Functional intent is distributed across source code, generated framework documentation, project documents, hand-drawn UI sketches, Figma frames, proof-of-concept research, and hardware models.

The current implementation demonstrates the main end-to-end path but does not yet provide a stable contract. Notable constraints include inverted boolean state naming, timeout-driven release, client-only dashboard gating, fixed five-seat analytics, destructive schema initialization, and alternative hardware concepts that were investigated but not selected. The project has no pre-existing durable OpenSpec capabilities, so this change creates the initial baseline.

## Goals / Non-Goals

**Goals:**

- Establish one canonical vocabulary and state model across browser, API, persistence, MQTT, analytics, and firmware behavior.
- Make each externally observable behavior traceable to current source, project intent, or an explicit baseline decision.
- Keep capability boundaries small enough for later implementation threads while preserving cross-capability invariants.
- Allow the planning work to be reviewed and refined across sequential threads without modifying runtime source.
- Separate production requirements from development conveniences and proof-of-concept alternatives.

**Non-Goals:**

- Implementing or refactoring application, database, frontend, container, or firmware code in this change.
- Preserving implementation defects as required behavior merely because they exist today.
- Requiring Kubernetes because the documentation folder contains presentations about persistent volumes.
- Selecting NFC, Tuya, ultrasonic, button, or infrared-barrier hardware for the baseline.
- Supporting direct communication, chat, messaging, announcements, or notifications between teachers and students. Those concepts are deprecated even where historical proposal text or UI designs show them.

## Decisions

### Decision: Use one baseline change with eight capability specifications

The specification is partitioned by externally observable responsibility: availability, catalog, lifecycle, ingestion, administration, analytics, device operation, and runtime deployment. The shared proposal and design hold scope and cross-cutting decisions.

This is preferred over one change per capability because the initial baseline depends on shared definitions for seat identity, state, time, authentication, and persistence. Separate changes would allow those definitions to drift before any durable specification exists.

### Decision: Apply a fixed evidence precedence

Conflicts are resolved in this order:

1. Explicit user decisions, especially the communication exclusion.
2. Approved decisions in this proposal and design.
3. Source code, tests, configuration, and seed data as evidence of current behavior.
4. Project proposal, operating documentation, use cases, and current UI designs as evidence of product intent.
5. Proof-of-concept alternatives and older sketches.
6. Generic or unrelated supporting material.

An observed implementation behavior can justify a compatibility scenario but cannot silently override an explicit product decision. An aspirational document can motivate a requirement only when it fits the approved baseline.

### Decision: Use a canonical three-state model

The functional contract uses `FREE`, `OCCUPIED`, and `UNKNOWN` rather than exposing boolean storage fields.

```text
                    accepted occupancy observation
        +----------------------------------------------+
        |                                              v
     +------+                                      +----------+
     | FREE |                                      | OCCUPIED |
     +------+                                      +----------+
        ^                                              |
        | timeout or authorized correction             |
        +----------------------------------------------+

     inconsistent or unrecoverable state --> UNKNOWN
     valid transition from known evidence  --> FREE or OCCUPIED
```

`UNKNOWN` prevents missing or inconsistent data from being reported as a free seat. Persistence adapters may retain the existing boolean column temporarily, but API and domain boundaries must map it explicitly.

The alternative was to define the current `status=true` value as the public contract. That was rejected because the name is ambiguous, the database column means `unoccupied`, and the firmware sends the inverse of detected motion.

### Decision: Define motion as occupancy evidence and release by timeout

For compatibility with the active firmware, MQTT `status=false` means motion or occupancy detected. `status=true` means no active motion, but it does not immediately mark a seat free. Accepted occupied observations refresh `lastObservedAt`; the original `occupiedSince` remains unchanged. The configured inactivity duration ends the interval.

Immediate release on a no-motion edge was rejected because a PIR sensor can stop reporting motion while a person remains seated. Timeout release is retained, but its state and history update must be atomic.

Retained or stale messages must not create phantom occupancy. The ingestion adapter must use broker metadata or a freshness policy when establishing whether a retained observation is current.

### Decision: Use the configured catalog as the source of seat cardinality

All views, updates, and analytics iterate configured seat identifiers. Fixed arrays of five elements and `id - 1` indexing are implementation gaps. Stable identifiers preserve history through renames and do not require identifiers to be contiguous.

The baseline does not require a full create/delete-seat administration interface. It requires the runtime behavior to work for any valid configured catalog and covers the currently demonstrated rename operation.

### Decision: Require server-side administrative sessions

Successful login must create a server-recognized session, and every modifying endpoint must enforce it. Browser `sessionStorage` can remain a presentation convenience but cannot be the authorization boundary. Passwords use an adaptive one-way hash, sessions expire, logout invalidates the server session, and sensitive values never appear in responses or ordinary logs.

Keeping the current client-only gate was rejected because a caller can bypass it and invoke dashboard endpoints directly.

### Decision: Model history as completed occupancy intervals

Each completed interval records seat identity, start, end, and duration. Existing storage may derive start from `endedAt - duration`, but the contract treats both boundaries as part of the interval semantics.

Per-seat averages measure occupancy duration, not waiting time. Hourly utilization sums interval overlap in each clock hour and caps the result at one hour per seat-hour. Taking only the maximum overlap was rejected because it undercounts multiple valid intervals within the same hour.

All date calculations use one externally documented application time zone. The default deployment should use `Europe/Vienna` unless deployment configuration explicitly selects another supported zone.

### Decision: Adopt Pico 2 W plus PIR as the baseline device

The active device path is a Raspberry Pi Pico 2 W with a motion sensor on GPIO 18, powered by USB and publishing to MQTT. Continuous occupancy needs periodic refresh messages in addition to edge messages so the inactivity timeout does not release a genuinely occupied seat.

NFC, Tuya, ultrasonic, button, and infrared-barrier concepts remain research inputs rather than required runtime dependencies. Adopting one later requires a separate specification change that defines its observation semantics.

### Decision: Separate production durability from development reset behavior

Production uses durable PostgreSQL storage and non-destructive schema evolution. Schema recreation, seeded accounts, and synthetic history are permitted only in explicit development or test profiles. Configuration and secrets come from the deployment environment.

The current unconditional `drop-and-create` behavior is treated as a development default that must not define production behavior.

### Decision: Use full snapshots as the realtime recovery boundary

The WebSocket channel sends a complete authoritative snapshot at connection and reconnection. Subsequent changes may be broadcast as complete snapshots or incremental messages, provided clients can order them and converge on the same state.

This avoids relying on a client to reconstruct changes missed during a network or application interruption.

### Decision: Maintain traceability in the change artifacts

Requirements use stable capability paths and descriptive requirement names. During later implementation, each task must reference the capability and requirement it satisfies. When a source behavior differs from a requirement, the implementation task records the gap rather than changing the requirement implicitly.

Primary evidence locations are:

| Area | Evidence |
| --- | --- |
| Public view and realtime behavior | `project/quarkus-java/src/main/resources/META-INF/resources/`, `SeatResource`, `SeatWebSocket` |
| State lifecycle and analytics | `SeatRepository`, `HistoryRepository`, model and DTO classes |
| Administration and login | `DashboardResource`, `UserResource`, dashboard client code |
| Sensor ingestion | `SensorService`, `application.properties`, MQTT documentation |
| Device behavior | `project/C_Pi_Movement/C_PI/main.cpp`, wiring documentation |
| Deployment | `pom.xml`, `Dockerfile`, `docker-compose.yaml`, `application.properties` |
| Product intent | project proposal, use-case diagram, UI sketches, Figma design |
| Alternative hardware | `documentation/ProofofConcept/ProofOfConcept.md` |

### Decision: Use sequential artifact ownership across threads

Each continuation thread must read `proposal.md`, this design, all completed specs, and `tasks.md` before editing. Threads work on the same change sequentially, keep requirement names stable, run strict validation, and leave a handoff stating artifacts changed, decisions made, remaining tasks, and validation status.

If work must run in parallel, each author uses an isolated worktree and an integration thread reconciles the artifacts before validation.

## Risks / Trade-offs

- **The baseline intentionally exceeds current implementation security and durability** -> Keep source-code work in later apply changes and trace every gap through tasks and tests.
- **The PIR timeout can report a stationary occupied seat as free** -> Require periodic occupied refresh and allow later calibration of the inactivity duration.
- **Retained MQTT messages can create stale occupancy** -> Require an explicit freshness policy and tests covering reconnect and retained delivery.
- **Adding `UNKNOWN` requires UI and API changes** -> Preserve a compatibility mapping during migration and never map unknown to free.
- **QR entry and server-side sessions are documented intent rather than complete current behavior** -> Treat them as target baseline requirements and identify their implementation gaps in the task plan.
- **Historical data currently lacks an explicit start column** -> Derive it during migration, validate non-negative durations, and add explicit storage when implementation begins.
- **A large baseline creates substantial implementation work** -> Execute tasks by capability and validate cross-capability journeys after each group.
- **Historical artifacts contain deprecated communication concepts** -> Allow those terms only in the proposal non-goal and this exclusion record; exclude them from specifications and implementation tasks.

## Migration Plan

1. Review and approve the proposal, design decisions, and eight capability deltas.
2. Archive the documentation-only change to establish the durable main specifications.
3. Create smaller implementation changes per capability or tightly coupled capability group.
4. Implement compatibility adapters for boolean state and existing MQTT payloads before changing external representations.
5. Migrate persistence non-destructively, including active-interval and history data validation.
6. Introduce server-side authorization before exposing administrative behavior beyond a controlled environment.
7. Remove fixed-seat assumptions and reconcile analytics against catalog-driven test fixtures.
8. Roll out device refresh and retained-message handling together so lifecycle timeouts remain reliable.
9. Validate the end-to-end public, sensor, administrative, analytics, restart, and recovery journeys before production use.

Rollback for a later implementation change must restore the prior application version without rolling back already committed durable data. Database migrations therefore need backward-compatible deployment steps or an explicit restore procedure. This planning-only change itself is rolled back by reverting its OpenSpec artifacts before archival.
