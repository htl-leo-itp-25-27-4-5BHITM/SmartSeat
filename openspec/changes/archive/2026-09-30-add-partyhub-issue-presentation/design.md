# Design

## Context

See `proposal.md` for motivation and `specs/partyhub-issue-presentation/spec.md` for the behavioral contract.

The current PartyHub report is a German Markdown file that references three images and six filenames for five unique recordings. The two fourth-recording files have identical hashes; despite its `.mp4` extension, `fehler4.mp4` contains WebM data. The unique video payload is about 44.2 MiB before base64 encoding, so a fully embedded presentation will be approximately 60 MiB before browser decoding overhead. The duplicate-email screenshot contains an email address and a verification URL or token that must not be distributed unchanged.

## Goals / Non-Goals

**Goals:**

- Produce one durable HTML file that can be copied, opened offline, and presented without installation.
- Give the findings a coherent German, risk-oriented narrative rather than reproducing the Markdown layout verbatim.
- Keep each recording or screenshot directly associated with the finding it demonstrates.
- Make the presentation practical at a projector resolution while retaining usable narrow-screen behavior.
- Ensure that the actual embedded media payload, not merely its visual presentation, is free of sensitive account data.

**Non-Goals:**

- Change or fix PartyHub itself.
- Convert the presentation to `.pptx` or require PowerPoint.
- Add a permanent build system, package manifest, server, or runtime dependency to the repository.
- Preserve the misspellings and formatting defects of the source report when clearer German wording can express the same finding.

## Decisions

### Use an inlined Reveal.js browser bundle

Use Reveal.js for slide layout, keyboard and touch navigation, controls, progress, scaling, transitions, and presentation lifecycle events. Inline the required minified JavaScript and CSS in the final document and retain the applicable Reveal.js license notice. Configure the deck for 16:9 content, visible controls and progress, keyboard and touch navigation, URL hashes, and restrained transitions.

Reveal.js is preferred over a custom slide engine because it supplies proven presentation behavior and responsive scaling. Impress.js was considered, but its spatial canvas effects do not improve this evidence-led deck and would require more custom accessibility and navigation work. A hand-written engine was rejected because the user explicitly requested a JavaScript presentation library and because it would recreate mature behavior unnecessarily.

### Deliver exactly one repository artifact

The final repository artifact is `documentation/FehlersuchePartyfinder/PartyHub-Issues-Presentation.html`. All CSS, JavaScript, images, fonts used by the deck, and media payloads are embedded. No runtime URL points to a CDN, local sibling file, or remote font.

Temporary tooling or sanitized media derivatives may be used during generation, but they are not retained as required presentation dependencies. This keeps the requested distribution model while allowing irreversible media redaction before encoding.

An HTML file plus adjacent assets was rejected because it would be easier to break when copied. Runtime CDN loading was rejected because it violates offline operation.

### Embed media as base64 and materialize videos lazily

Images use base64 data URLs. Each unique video is stored once as base64 inside a non-executable data block in the HTML. Presentation JavaScript converts a video's data to a Blob URL when its slide is first activated and attaches that URL to the `<video>` element. Videos expose native controls, use `preload="metadata"`, and never autoplay. Blob URLs are revoked when the page unloads.

Lazy materialization limits the amount of decoded video data retained before it is needed. Direct data URLs on every `<video>` element were considered but would make initial media parsing and memory use less predictable. External media files were rejected because the output must remain self-contained.

The identical `fehler4.mp4` and `fehler4.webm` payload is included once with the WebM MIME type. The other four recordings retain their MP4 MIME type unless implementation-time browser validation proves a source codec incompatible, in which case a compatible sanitized transcode is embedded.

### Use a fourteen-slide risk narrative

The deck uses this structure:

1. Title and purpose
2. Executive summary with the 3/2/5 risk distribution
3. High-risk section introduction
4. Party navigation authentication bypass with `fehler1.mp4`
5. Account navigation authentication bypass with `fehler2.mp4`
6. Notification navigation authentication bypass with `fehler3.mp4`
7. Medium-risk section introduction
8. Mobile login scaling with `Skalierungsfehler.jpeg`
9. Second-tab login failure with `fehler4.webm`
10. Low-risk section introduction and expired-party finding
11. Email-verification contrast with `blackFont.png`
12. Duplicate registration email with a sanitized `doppelt.png` derivative
13. Party-page spacing and inconsistent profile navigation with `Fehler5.mp4`
14. Recommended remediation order and conclusion

Each detailed finding slide uses a consistent pattern: risk badge, affected interaction, observed behavior, impact, and evidence. The high-risk label from the source is preserved even though implementation cannot independently prove exploitability from the report alone.

### Redact before embedding rather than hiding in CSS

Audit every screenshot and recording for email addresses, verification URLs or tokens, authentication secrets, and identifying account data. Create sanitized raster or video derivatives before base64 encoding. Redaction must replace or remove the underlying pixels or frames; CSS blurs, masks, clipping, or overlays alone are insufficient because the original bytes would remain extractable from the HTML.

The duplicate-email screenshot is known to require sanitization. Other media is treated as unverified until visually reviewed during implementation. If a recording contains sensitive data, crop or transcode it with irreversible masking while retaining the behavior needed as evidence.

### Add presentation-specific controls and accessibility cues

Use Reveal.js navigation plus a small custom fullscreen control backed by the browser Fullscreen API. Provide meaningful German headings, media labels, alternative text for screenshots, visible focus states, sufficient color contrast, and reduced-motion behavior via `prefers-reduced-motion`. Pause any playing video when its slide is left so audio does not continue across findings.

## Risks / Trade-offs

- **Large output and memory use** -> Expect roughly 60 MiB of HTML; lazy video materialization, deduplication, and metadata-only preload reduce startup and memory pressure without dropping evidence.
- **Browser codec differences** -> Validate every recording in at least current Chromium and Firefox; transcode only incompatible media to a broadly supported format while preserving evidence.
- **Sensitive details could survive superficial masking** -> Sanitize media before encoding and inspect the final HTML payload and rendered output rather than relying on CSS concealment.
- **Base64 generation is error-prone** -> Compare embedded payload counts and decoded hashes against the intended sanitized sources, and test every media control offline.
- **A single generated file is difficult to review in a text diff** -> Keep all human-readable slide markup and presentation styles organized before the large media blocks, add clear comments, and validate behavior through scripted structural checks plus browser review.
- **The source wording is sometimes ambiguous or misspelled** -> Improve language without inventing reproduction steps, and visibly distinguish observed behavior from inferred impact.

## Migration Plan

1. Audit and sanitize the source media in temporary working files.
2. Build the German slide markup and inlined presentation runtime.
3. Embed and deduplicate the sanitized media payloads.
4. Run structural, privacy, offline, responsive, navigation, and playback verification.
5. Add the single completed HTML file at the proposed path.

Rollback consists of removing the new HTML file; no existing application or documentation behavior is migrated.
