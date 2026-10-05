# Proposal

## Why

Usability testing found that SmartSeat can show a newly occupied seat with an incorrect elapsed time, obscures smaller analytics values, and becomes difficult to use on smaller screens. The public floor navigation and administrative feedback also create avoidable confusion, so the affected views and timing contract should be corrected together.

## What Changes

- Replace the public map's floor-selector cards with two simultaneously visible, clearly labeled floor maps: `1.OG` on the left and `2.OG` on the right on wide screens, stacked on narrow screens.
- Keep the Map, List, and Chart navigation and visibly identify the active view with a programmatic current-state indicator.
- Derive floor maps, seat markers, counts, list entries, and chart series from catalog data instead of fixed seat IDs or a fixed five-seat layout.
- Transmit occupancy interval start times as unambiguous absolute timestamps and calculate elapsed occupancy without a hard-coded client timezone offset.
- Make hourly utilization calculations additive across intervals and capped at 100 percent per seat-hour, and make chart units, values, empty states, and interaction understandable without precision hovering.
- Make the administrative dashboard responsive, allow page and leaderboard scrolling where needed, and keep controls usable at laptop and phone widths.
- Label inactivity duration values in seconds.
- Treat saving a seat's unchanged normalized name as a successful no-op while continuing to reject a name owned by another seat.
- Add automated backend coverage for timing, aggregation, and rename behavior, plus repeatable browser checks for catalog-derived rendering and important responsive/accessibility states.

## Capabilities

### New Capabilities

None.

### Modified Capabilities

- `seat-availability`: Replace single-floor selection with a responsive overview of all configured floors, add active-view navigation semantics, and require unambiguous elapsed-time and non-hover-only presentation.
- `seat-state-lifecycle`: Require absolute, timezone-unambiguous active-interval timestamps across the external contract.
- `history-and-analytics`: Clarify user-facing utilization semantics and require accessible, catalog-derived analytics presentation while preserving additive capped aggregation.
- `seat-catalog-and-locations`: Define unchanged-name renames as successful no-ops and preserve conflicts only for names owned by another seat.
- `administration-and-access-control`: Require explicit duration units and a responsive, reachable administrative dashboard.

## Impact

- Public frontend: `index.html`, `script.js`, `style.css`, floor-map markup, navigation state, chart configuration, and accessible value presentation.
- Administrative frontend: `dashboard.html`, `dashboard.js`, and `dashboard.css` responsive behavior and feedback.
- Backend/API: seat timestamp serialization, hourly utilization aggregation, dynamic catalog projection, and rename conflict handling.
- Tests: repository/resource timing and aggregation tests plus frontend behavior and responsive-layout checks.
- No new runtime dependency or intentional breaking API endpoint removal is required; timestamp values become explicitly offset-aware.
