# Tasks

## 1. Prepare Source Content and Evidence

- [x] 1.1 Convert the ten Markdown findings into a German content inventory with three high-, two medium-, and five low-risk entries, and verify every source list item is mapped to exactly one presentation finding.
- [x] 1.2 Hash and classify the six recording filenames, retain only the five unique payloads, treat the fourth recording as WebM, and verify the duplicate hash is not included twice.
- [x] 1.3 Visually audit all screenshots and recordings for account identifiers, email addresses, verification URLs or tokens, secrets, and personal data, and verify the audit records a sanitization decision for every media item.
- [x] 1.4 Create temporary irreversible sanitized derivatives for every media item that needs redaction, including `doppelt.png`, and verify sensitive source pixels or frames cannot be recovered from each derivative.
- [x] 1.5 Verify every sanitized recording plays in current Chromium and Firefox, and transcode only incompatible recordings to a supported format while confirming the demonstrated issue remains visible.

## 2. Build the Self-Contained Presentation

- [x] 2.1 Create `documentation/FehlersuchePartyfinder/PartyHub-Issues-Presentation.html` with an inlined, pinned Reveal.js JavaScript and CSS bundle plus its license notice, and verify the HTML contains no runtime CDN or package dependency.
- [x] 2.2 Implement the fourteen-slide German narrative defined in `design.md`, and verify the rendered deck contains the executive summary, all ten findings, three risk sections, and the closing remediation priorities.
- [x] 2.3 Apply the PartyHub-inspired visual theme, consistent risk badges, readable typography, evidence layouts, visible focus styles, and reduced-motion rules, and verify slides remain legible in 16:9 and mobile-width viewports without horizontal page scrolling.
- [x] 2.4 Add meaningful headings, screenshot alternative text, media labels, and semantic controls, and verify keyboard focus and accessible names are visible and understandable.

## 3. Embed and Control Evidence

- [x] 3.1 Embed the three sanitized screenshots as data URLs beside their matching findings, and verify each image renders after the HTML file is copied away from the source asset directories.
- [x] 3.2 Embed each of the five unique sanitized recordings once as a base64 media data block, and verify the final HTML contains five video payloads with no duplicate fourth-recording payload.
- [x] 3.3 Implement lazy conversion of video data blocks to Blob URLs with metadata-only preload and unload cleanup, and verify videos are materialized on first slide activation and their Blob URLs are revoked when the page unloads.
- [x] 3.4 Provide native video controls, disable autoplay, and pause media when its slide is left, and verify no video starts or continues playing across a slide change without presenter action.

## 4. Presentation Interaction

- [x] 4.1 Initialize Reveal.js with keyboard, touch, on-screen controls, progress, URL hashes, 16:9 scaling, and restrained transitions, and verify next and previous actions move exactly one slide by keyboard and by visible controls.
- [x] 4.2 Add a labelled fullscreen control using the browser Fullscreen API with a graceful unsupported-state fallback, and verify the deck enters and exits fullscreen when the browser permits it.
- [x] 4.3 Add slide lifecycle handling for media and presentation state, and verify revisiting a finding restores usable controls without restarting or duplicating the embedded payload.

## 5. Validate the Deliverable

- [x] 5.1 Run structural checks that the deliverable is one HTML file, all required code and media are embedded, no external `src`, `href`, font, or network dependency remains, and verify the checks pass.
- [x] 5.2 Decode representative embedded image and video payloads and compare their hashes with the intended sanitized inputs, and verify base64 generation did not corrupt the evidence.
- [x] 5.3 Open a copied version of the HTML with network access disabled and verify all fourteen slides, styles, controls, screenshots, and five recordings work without adjacent files.
- [x] 5.4 Review the rendered deck in current Chromium and Firefox at desktop projector and mobile widths, and verify navigation, fullscreen behavior where supported, responsiveness, contrast, and every media control meet the specification scenarios.
- [x] 5.5 Inspect the final HTML source and all rendered evidence for sensitive account information, and verify no email address, verification URL or token, secret, or identifying account data is present in the distributed payload.
