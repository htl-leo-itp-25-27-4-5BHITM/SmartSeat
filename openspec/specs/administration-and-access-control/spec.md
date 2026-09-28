# Administration and Access Control Specification

## Purpose

Defines authenticated administrative access and protects every operation that changes SmartSeat catalog, state, configuration, or development data.

## Requirements

### Requirement: Administrator authentication
The system SHALL verify an administrator's submitted username and password and SHALL establish a server-recognized authenticated session only after successful verification.

#### Scenario: Valid credentials
- **WHEN** an administrator submits valid credentials
- **THEN** the system establishes an authenticated session without returning the stored password representation

#### Scenario: Invalid credentials
- **WHEN** a submitted username is unknown or its password is invalid
- **THEN** the system returns an indistinguishable authentication failure and does not establish a session

### Requirement: Server-side authorization
The system SHALL enforce authorization on the server for every operation that changes seats, authoritative state, inactivity duration, accounts, or synthetic history.

#### Scenario: Unauthenticated modification
- **WHEN** an unauthenticated client calls a protected modification endpoint directly
- **THEN** the system rejects the request without changing data

#### Scenario: Authenticated modification
- **WHEN** an authenticated administrator submits a valid protected operation
- **THEN** the system performs the operation and returns its observable result

### Requirement: Session termination
Administrative sessions SHALL expire after the configured session lifetime and SHALL be explicitly terminable through logout.

#### Scenario: Logout
- **WHEN** an administrator logs out
- **THEN** the server invalidates the session and later protected requests using it are rejected

#### Scenario: Session expiry
- **WHEN** the configured session lifetime elapses
- **THEN** the server requires the administrator to authenticate again before performing protected operations

### Requirement: Credential protection
The system SHALL store passwords using an adaptive one-way password hash and SHALL not include passwords, password hashes, or broker credentials in API responses or ordinary logs.

#### Scenario: Inspect account response
- **WHEN** an account-related response or log entry is produced
- **THEN** it contains no plaintext password, password hash, or reusable secret

### Requirement: Inactivity duration administration
An authenticated administrator SHALL be able to read and change the inactivity duration, and the system SHALL accept only whole-second values greater than ten.

#### Scenario: Valid duration update
- **WHEN** an administrator submits a whole-second duration greater than ten
- **THEN** the new duration is persisted and used for subsequent timeout evaluations

#### Scenario: Invalid duration update
- **WHEN** an administrator submits a duration of ten seconds or less or a non-integer value
- **THEN** the system rejects the request and preserves the existing duration

### Requirement: Administrative audit information
The system SHALL record the time, authenticated administrator identity, target, and result of each state correction or configuration change.

#### Scenario: Configuration change succeeds
- **WHEN** an administrator changes a seat name or inactivity duration
- **THEN** an audit record identifies who changed which value and when

