# Proposal

## Why

The PartyHub issue findings currently exist as a Markdown report with separate screenshots and recordings, which is inconvenient to present and distribute. A self-contained, presentation-style HTML document will make all findings easy to review offline while preserving the supporting evidence and risk classification.

## What Changes

- Add a German presentation covering all ten PartyHub findings from `documentation/FehlersuchePartyfinder/FehlersuchePartyfinder.md`.
- Organize the findings into high-, medium-, and low-risk sections with an executive summary and remediation priorities.
- Include the five unique issue recordings and the relevant screenshots as embedded presentation evidence.
- Deliver the presentation as one offline HTML file with no required CDN, network connection, or adjacent asset files.
- Provide PowerPoint-like keyboard, on-screen, progress, and fullscreen presentation controls.
- Prevent sensitive account details, email addresses, and verification tokens from appearing in distributed evidence.

## Capabilities

### New Capabilities

- `partyhub-issue-presentation`: Defines the content, evidence handling, offline packaging, privacy, and presentation behavior of the PartyHub issue presentation.

### Modified Capabilities

None.

## Impact

- Adds `documentation/FehlersuchePartyfinder/PartyHub-Issues-Presentation.html` as the distributable presentation.
- Uses the existing Markdown report, three screenshots, and five unique recordings as source material.
- Introduces an embedded Reveal.js runtime and presentation styling inside the HTML deliverable.
- Does not change the SmartSeat application, PartyHub application, APIs, or existing OpenSpec capabilities.
