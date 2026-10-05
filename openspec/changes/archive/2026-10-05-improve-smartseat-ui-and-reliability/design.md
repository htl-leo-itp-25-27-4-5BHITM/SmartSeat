# Design

## Context

See `proposal.md` for motivation. The public UI is a static HTML/CSS/JavaScript application backed by Quarkus REST resources and a WebSocket seat snapshot. It currently renders two floors through one reused map, indexes five fixed DOM markers, builds five fixed chart series, serializes active timing as timezone-free `LocalDateTime`, and compensates in the browser with a hard-coded `+02:00` offset. The administration page uses a fixed-height, three-column grid with overflow hidden.

The existing OpenSpec baseline already requires catalog-sized behavior, additive hourly utilization, consistent time handling, and correct interval lifecycle semantics. This change aligns the implementation with that baseline and adds the tested presentation behavior.

## Goals / Non-Goals

**Goals:**

- Establish one timezone-safe representation for active interval instants.
- Render floor and analytics views from catalog data rather than seat-number assumptions.
- Keep public and administrative controls usable with pointer, keyboard, and narrow screens.
- Make analytics understandable and inspectable independently of relative visual size.
- Preserve the existing public endpoints and Map/List/Chart information architecture.

**Non-Goals:**

- Redesign the physical floor-plan artwork.
- Replace Chart.js or introduce a frontend framework.
- Add new floors, seats, authentication architecture, or analytics metrics.
- Change inactivity-release policy from timeout-based release.
- Treat the administration dashboard as a public view.

## Decisions

### 1. Use absolute instants at the lifecycle and API boundary

Represent `occupiedSince` and in-memory last-observed values with an absolute time type such as `Instant`; serialize the DTO value in ISO 8601 UTC form. The browser parses the supplied value directly and clamps a transient negative difference to zero while awaiting a corrected snapshot.

This removes dependence on the JVM timezone, browser timezone, and daylight-saving offset. Keeping `LocalDateTime` and documenting an assumed server zone was rejected because it would preserve ambiguity and the current two-hour failure mode. Appending a computed browser offset was rejected because the server value still would not identify an instant.

Use an injectable clock for lifecycle calculations where practical so transition and timezone tests can be deterministic. Preserve the original interval start on repeated occupied observations and update only last-observed time.

### 2. Render a catalog-driven floor-map collection

Build the map view by grouping the WebSocket snapshot by floor, ordering the known floors naturally, and generating one labeled map panel per group. At the current two-floor deployment, the responsive grid places `1.OG` left and `2.OG` right; below the minimum readable panel width it changes to one column.

Each seat receives normalized map coordinates in catalog data. Marker elements are generated from stable seat IDs, carry readable labels and state text, and support focus/click as well as hover. A seat without valid coordinates is omitted only from graphical placement and remains present in the list/text representation with a visible data-quality indication.

Reusing the existing floor-selector cards was rejected because it preserves hidden context and unnecessary interaction. Duplicating fixed markup for two floors was rejected because it conflicts with catalog-sized behavior.

### 3. Preserve Map/List/Chart navigation with explicit state

Use buttons with stable view identifiers. The selected control receives an active class and `aria-pressed="true"` (or an equivalent single-selection pattern); inactive controls receive the corresponding false state. View activation updates both visual state and content visibility without changing the existing default Map view.

Removing Map/List/Chart was rejected because the views answer different user questions. Only the obsolete floor-selection controls are removed.

### 4. Keep hourly utilization as the chart metric

The durable specification already defines hourly utilization, so the chart remains a line chart of occupied time per seat-hour rather than switching to completed-visit counts. The backend sums every interval overlap per seat-hour and clamps the total to 3600 seconds before converting it to a percentage.

The response and datasets are built from catalog seats and explicit hour keys instead of assuming that seat 1 supplies all labels or that identifiers are contiguous. The canvas labels utilization as percent, uses 0-100 bounds, and configures index-based, non-intersecting interaction with usable point hit areas. A synchronized textual table exposes the same values and serves as the non-canvas representation. An explicit empty state replaces a zero-only chart when there is no data.

A logarithmic scale was rejected because utilization has a bounded, meaningful linear percentage scale. Persistent labels on every point were rejected because 24 hours across multiple seats would become unreadable.

### 5. Reflow the dashboard instead of scaling it down

Replace the fixed viewport/hidden-overflow contract with a page that can scroll. Use a responsive grid with three columns on wide screens, two columns at laptop widths, and one column on narrow screens. Panels use content-driven heights; the leaderboard receives a bounded internal scroll region only when its entries exceed the allocated panel height. Form rows wrap and controls retain usable widths.

Shrinking the existing three columns was rejected because the presentation demonstrates that it makes labels and controls unreadable. Making every panel independently scrollable was rejected because nested scrolling would harm phone usability.

### 6. Make unchanged renames idempotent

Normalize the submitted name once. If it equals the target seat's normalized current name, return the current catalog as success without issuing an update or broadcast. Otherwise, test uniqueness against other seat IDs and return conflict only for another owner. Server-side handling remains authoritative even if the client also disables a no-change save.

### 7. Verify behavior at service and viewport boundaries

Add backend tests for transition start time, repeated observations, differing timezone environments, overlap summation/capping, dynamic seat identifiers, unchanged rename, and true duplicate rename. Use repeatable browser checks at representative wide, laptop, and phone viewport widths for dual-map order, active navigation, keyboard-accessible details, chart textual values, dashboard reflow, and leaderboard reachability.

## Risks / Trade-offs

- [Changing persisted timestamp types can require database conversion outside the current drop-and-create development setup] -> Define the intended SQL conversion before production deployment and keep rollback instructions for the previous column representation.
- [Existing seats do not yet have per-seat map coordinates] -> Seed coordinates for all current seats and keep unplaceable seats available in the list rather than guessing a position.
- [A data table duplicates chart content and can become large] -> Keep it semantically available, allow users to reveal/collapse it visually, and scope it to the selected date.
- [WebSocket updates currently trigger repeated analytics requests] -> Load analytics when the Chart view/date changes or debounce invalidation so seat heartbeat traffic does not recreate the chart unnecessarily.
- [Two internal scroll regions can be awkward on touch devices] -> Prefer page scrolling on narrow screens and reserve leaderboard-only scrolling for layouts with a bounded panel height.

## Migration Plan

1. Add map coordinates for the current catalog and introduce the absolute timestamp representation with a compatible database conversion where persistent data is retained.
2. Update backend DTOs, lifecycle calculations, rename behavior, and analytics aggregation; verify endpoint contracts before changing the clients.
3. Deploy the catalog-driven public views and responsive administration styles together with their backend contract changes.
4. Run the automated backend suite and the viewport/accessibility check matrix against current seat and history data.
5. Roll back application and schema changes together if timestamp conversion or client compatibility fails; the existing endpoints and navigation names remain stable.
