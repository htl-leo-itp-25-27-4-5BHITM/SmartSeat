# Spec Delta

## Purpose

Provides a portable, privacy-conscious presentation of the documented PartyHub findings and their visual evidence for offline review and prioritization.

## ADDED Requirements

### Requirement: Complete issue coverage
The presentation SHALL describe all ten findings recorded in `documentation/FehlersuchePartyfinder/FehlersuchePartyfinder.md` and SHALL retain their documented high-, medium-, or low-risk classification.

#### Scenario: Reviewer checks the issue inventory
- **WHEN** a reviewer moves through the complete presentation
- **THEN** the reviewer can identify three high-risk findings, two medium-risk findings, and five low-risk findings
- **AND** no finding from the source report is omitted

### Requirement: German presentation narrative
The presentation SHALL provide its titles, explanations, summaries, and recommended priorities in clear German while preserving the meaning of the source findings.

#### Scenario: German-speaking audience reviews a finding
- **WHEN** a finding slide is displayed
- **THEN** its risk, affected interaction, observed behavior, and security or usability impact are understandable in German

### Requirement: Evidence mapped to findings
The presentation SHALL include the five unique issue recordings referenced by the source report and the relevant screenshots, with each media item shown alongside the finding it supports.

#### Scenario: Reviewer opens a finding with recorded evidence
- **WHEN** the reviewer reaches a finding backed by a video
- **THEN** the corresponding embedded recording is available with playback controls
- **AND** the recording does not begin playing automatically

#### Scenario: Reviewer opens a finding with screenshot evidence
- **WHEN** the reviewer reaches a finding backed by a screenshot
- **THEN** the corresponding embedded screenshot is visible and labelled with its evidentiary purpose

#### Scenario: Duplicate fourth recording is packaged
- **WHEN** the identical `fehler4.mp4` and `fehler4.webm` source files are processed
- **THEN** the correctly identified WebM recording is embedded once rather than duplicating the same media payload

### Requirement: Self-contained offline delivery
The presentation SHALL be distributed as `documentation/FehlersuchePartyfinder/PartyHub-Issues-Presentation.html` and SHALL contain all runtime code, styling, images, and recordings needed for presentation playback.

#### Scenario: Presentation is opened without network access
- **WHEN** the HTML file is copied away from the source directories and opened in a supported modern browser while offline
- **THEN** all slides, navigation controls, styles, screenshots, and recordings remain available
- **AND** the browser makes no required network request for presentation content or dependencies

### Requirement: PowerPoint-like navigation
The presentation SHALL support sequential slide navigation through the keyboard and visible on-screen controls, SHALL display presentation progress, and SHALL offer fullscreen viewing.

#### Scenario: Presenter uses keyboard navigation
- **WHEN** the presenter presses a supported next or previous navigation key
- **THEN** the presentation moves exactly one slide in the requested direction

#### Scenario: Presenter uses on-screen navigation
- **WHEN** the presenter activates a visible next or previous control
- **THEN** the presentation moves exactly one slide in the requested direction

#### Scenario: Presenter enters fullscreen
- **WHEN** the presenter activates the fullscreen control and the browser permits fullscreen
- **THEN** the slide deck occupies the available fullscreen presentation area

### Requirement: Risk-oriented narrative
The presentation SHALL include an executive summary, dedicated high-, medium-, and low-risk sections, and a closing prioritization of recommended remediation work.

#### Scenario: Stakeholder reviews priorities
- **WHEN** the stakeholder views the opening summary and closing recommendation slides
- **THEN** the high-risk authentication or access-control findings are visibly prioritized ahead of medium- and low-risk findings

### Requirement: Sensitive evidence protection
The presentation SHALL NOT expose email addresses, verification URLs or tokens, authentication secrets, or other personally identifying account data in embedded screenshots or recordings.

#### Scenario: Evidence contains sensitive account data
- **WHEN** source media contains an email address, verification URL or token, authentication secret, or personally identifying account data
- **THEN** that information is irreversibly redacted from the media payload before the media is embedded
- **AND** the original sensitive pixels or frames cannot be recovered from the distributed HTML file

### Requirement: Responsive and readable slide rendering
The presentation SHALL maintain readable text, controls, and evidence at common desktop projector sizes and mobile viewport sizes without horizontal page scrolling.

#### Scenario: Presentation is viewed on a desktop projector
- **WHEN** the presentation is opened in a 16:9 desktop browser viewport
- **THEN** slide content remains within the visible presentation area and evidence is legible

#### Scenario: Presentation is viewed on a narrow viewport
- **WHEN** the presentation is opened on a mobile-width browser viewport
- **THEN** navigation remains operable and slide content is scaled or rearranged without horizontal page scrolling
