# Tasks

## 1. Catalog and Time Contracts

- [x] 1.1 Add normalized per-seat map coordinates to the persisted catalog and seat DTO, seed valid coordinates for the five current seats, and verify repository/resource tests return each seat's floor and placement without assuming contiguous IDs.
- [x] 1.2 Replace timezone-free active interval and last-observed values with absolute instants using an injectable clock, preserve the original interval start on repeated occupied observations, and verify deterministic lifecycle tests cover first occupancy, repeated occupancy, inactivity release, and a daylight-saving boundary.
- [x] 1.3 Serialize active interval starts with `Z` or an explicit offset in REST/WebSocket snapshots and verify a contract test parses the same instant under different JVM timezone settings.

## 2. Backend Behavior Corrections

- [x] 2.1 Rework hourly utilization to enumerate catalog seats, sum every interval overlap per seat-hour, clamp each total to 3600 seconds, and verify tests cover multiple intervals, an hour boundary, a capped overlap, a non-contiguous seat ID, and a no-data date.
- [x] 2.2 Normalize rename input, return success without an update for the target seat's unchanged name, and conflict only when another seat owns the name; verify resource tests cover unchanged, unique, duplicate, blank, and unknown-seat requests.
- [x] 2.3 Ensure public seat snapshots and analytics responses expose catalog display names and all configured identifiers, and verify adding a test seat requires no frontend-specific or aggregation code change.

## 3. Public Map, List, and Navigation

- [x] 3.1 Replace the single map and floor-selector markup with a catalog-driven floor-map container while preserving Map, List, and Chart controls; verify the rendered default order places `1.OG` before `2.OG` and contains no obsolete floor-selector controls.
- [x] 3.2 Generate floor panels, free counts, seat markers, and list entries from snapshot data and normalized coordinates, with a textual fallback for invalid placement; verify fixtures with extra and non-contiguous seat IDs render without missing or placeholder seats.
- [x] 3.3 Add visible and programmatic active state to Map/List/Chart controls and keyboard-operable seat details, and verify focus, activation, current-view state, seat name, canonical state, and duration are available without pointer hover.
- [x] 3.4 Parse the server-provided occupancy instant without adding a client offset, update elapsed time from the original interval start, and verify a newly occupied fixture starts near zero in browsers configured for different timezones.
- [x] 3.5 Implement the responsive two-map layout with side-by-side wide-screen panels and vertically stacked narrow-screen panels, and verify floor labels, counts, markers, and details remain readable without horizontal page scrolling.

## 4. Analytics Presentation

- [x] 4.1 Build hourly chart labels and series from explicit response keys and catalog names, convert utilization to 0-100 percent, label both axes and the metric meaning, and verify dynamic/non-contiguous seat fixtures produce correctly named series.
- [x] 4.2 Configure index-based non-intersecting chart interaction with usable hit areas and add a synchronized textual value table, then verify all seat values for an hour are obtainable by pointer and keyboard even when their sizes differ substantially.
- [x] 4.3 Show an explicit no-data state and avoid rebuilding the chart on every unrelated WebSocket snapshot, and verify an empty date hides the misleading chart while relevant date/data changes refresh it once.

## 5. Administrative Dashboard

- [x] 5.1 Label the inactivity duration control and success feedback in seconds, keep no-change rename saves from showing an error, and verify the visible wording and unchanged-name flow match the API result.
- [x] 5.2 Replace fixed viewport heights and hidden page overflow with a responsive three-, two-, and one-column dashboard grid, allow form rows to wrap, and verify every panel and control is reachable at wide, laptop, and phone widths.
- [x] 5.3 Add bounded leaderboard scrolling for constrained desktop/laptop panels while preferring page scrolling on narrow screens, and verify a leaderboard longer than the viewport does not clip entries or cover neighboring controls.

## 6. Integrated Verification

- [x] 6.1 Run the Quarkus test suite from `project/quarkus-java` and verify all existing and new lifecycle, API, analytics, and rename tests pass.
- [x] 6.2 Run the repeatable browser check matrix at approximately 1440x900, 1024x768, and 390x844 with keyboard-only navigation, and verify the dual-map order, active navigation, timer, chart/table values, dashboard reflow, and leaderboard reachability satisfy the delta specs.
- [x] 6.3 Validate the completed OpenSpec change with strict validation and verify no proposal capability lacks its matching delta spec.
