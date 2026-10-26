# Standards adoption history: October 2026

Historical record of the 2026-10-22 through 2026-10-25 adoption and documentation work.
Statements about current versions, pending checks, branch publication and working-tree
status describe their original point in time. This record is not maintained as setup
instructions or a current status report. See [Development](../../development.md) for
compatibility decisions, verification evidence and known limitations.

## Original adoption record


Target: [repo-standards 073f51ff68ec050f6b3372c46bc3bd4e721a5342](https://github.com/yashwant-das/repo-standards/tree/073f51ff68ec050f6b3372c46bc3bd4e721a5342),
`complete` documentation profile. Audit: 2026-10-22, default branch `f355b28`.
Documentation: verified locally. Structure/dependencies and hooks/CI: implemented;
local checks and outstanding integration evidence are detailed below.
Publication: pending PR review and merge; local verification is not published
adoption or enforced merge protection.

The original POM and CI targeted Java 17. Retain `release=17`; JDK 21 is the build
toolchain. Owner: Yashwant Das. Review the target when Java 17 support or a
dependency’s minimum runtime changes. The shared target of Java 21 is pending
owner approval rather than silently changing the existing target.

### Dependency compatibility review

On 2026-10-22, resolved stable candidates from
[Maven Central](https://repo.maven.apache.org/maven2/). Exact selected versions
live in the POM; each Jackson format binding and the Java Playwright binding
were checked separately. Maven is pinned to 3.10.0 with its official distribution
checksum; wrapper scripts are upstream 3.3.4.

Allure TestNG 3.0.0 failed test compilation because it removed the
`Allure.addAttachment(String, String, InputStream, String)` overload used by the
failure listener and reporting example. Select the latest compatible 2.x adapter,
2.35.5. Keep Allure Report on its current 2.x rendering family (2.46.1) through
the updated Maven plugin 3.1.0, which explicitly supports 2.x reports. This preserves
the deployed report format and attachment behavior. Owner: Yashwant Das. Review
when upgrading the attachment integration or moving report formats deliberately.
This is a compatibility constraint, not authorization to skip failed checks.

Use JDK 21 for Checkstyle and the build. `--release 17` checks Java 17 API use and
bytecode; it does not prove every dependency on every JDK. The selected logging,
TestNG and Allure adapter base classes were inspected for Java 17-compatible
bytecode. A separate Java 17 execution remains outstanding; no runtime upgrade
or claim of full Java 17 validation is implied.

Surefire 3.6.0 switched to JUnit Platform and did not honor the TestNG suite XML
selection: the supposed unit-only run started a browser example. That candidate
was stopped and rejected. Keep the latest compatible 3.5.x plugin, 3.5.6, with
native TestNG suite/listener behavior. Owner: Yashwant Das. Review when deliberately
migrating the test engine or when native TestNG support changes. This protects
unit/smoke/public separation, retry listeners and existing default-suite behavior.

The first controlled run exposed Playwright Java’s implicit all-browser download
inside `Playwright.create()`, even after installing Chromium. The JVM stack and
child `cli.js install` process confirmed that startup side effect. Surefire now
sets `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1`; setup installs browser binaries
explicitly from the pinned Java package. This keeps the controlled run independent
of download services after setup and makes missing-browser prerequisites visible.

### Verification record

Environment: macOS 27.0.1, Apple Silicon, Homebrew JDK 21.0.13; Maven 3.10.0.
Baseline `f355b28`: 10/10 configuration units passed. The existing public/default
suite ran 36 tests with retries and parallelism disabled and a 10-second browser
timeout: 35 passed, `PlaywrightDocsNavigationTest.testSearch` timed out. That run
is evidence of a baseline failure, not a waived gate.

After compatible upgrades: the documented default `test -Dbrowser.headless=true`
run, preserving the YAML’s retry and parallel settings, passed all 36 tests with
zero failures, errors or skips. The explicit unit-only `clean verify` passed
10/10 units and Checkstyle with zero violations. Controlled Chromium smoke passed
2/2 tests with retries disabled, checking form submission, screenshots and
per-method context storage isolation. No public page selectors were changed.

The hooks passed on all maintained files. The initial hook run corrected existing
trailing whitespace and missing/extra final newlines; source changes outside the
new smoke package are only those formatting repairs. All relative documentation
file targets resolve; Markdown and workflow syntax checks passed. Further native
browser, clean-checkout and hosted results are recorded below as they complete.

### Final review and resumed verification (2026-10-23)

The independent branch review found three issues, all corrected in one pass:
conflict markers outside merge state, incorrect root-relative suite commands in
moved guides, and untracked Java drafts affecting staged commits. The marker and
untracked-draft failures were reproduced before correction. The staged Java
adapter now selects index paths; full CI analysis remains independent. CodeQL
also explicitly gates report deployment in the same workflow.

Sixteen real Git commit probes passed in a disposable checkout. They cover valid
Markdown hard breaks, invalid Markdown, whitespace/newlines, conflict markers,
strict JSON including rejected comments, YAML/XML syntax, invalid staged Java,
config-only JSONC parsing, valid JSONC comments, Java/POM commits with an unrelated
invalid untracked Java draft, and partially staged Markdown/Java that preserve
unstaged edits. No hooks were bypassed. Shell syntax, documentation file targets
and root-relative suite paths were checked.

Fresh JDK 21 verification passed the 10 offline units and Checkstyle, controlled
Chromium (2/2), and controlled WebKit (2/2). Firefox 157.0 / Playwright build 1555
fails during native startup on this macOS 27.0.1 host with “Could not find profile
folder”; both browser tests are skipped after setup failure. A second run using
canonical `/private/tmp` reproduced it. This is a failed local run, not a pass or
an approved exception. Its symptoms match the upstream
[macOS 27 Firefox app-data issue](https://github.com/microsoft/playwright/issues/42768)
and [Mozilla startup diagnostic](https://bugzilla.mozilla.org/show_bug.cgi?id=2062988);
the host-specific cause has not been independently proven here. No host privacy
settings were changed. Keep the Firefox CI run enabled on macOS 14 and Windows;
those hosted results and a future working local Firefox run remain outstanding.
Owner: Yashwant Das. Review when the upstream issue or host/browser changes.

The earlier clean-checkout verification passed unit/static checks and Chromium
smoke, then generated an Allure report with 12 passing results and valid result
JSON. An interrupted exploratory run had left an empty container in the primary
checkout; the clean checkout had no such parse error. Hosted CI, CodeQL, Windows,
macOS 14, Java 17 runtime execution and merge protection are separate outstanding
evidence. No adoption stage is represented as published on `main`.

### Approved namespace migration (2026-10-23)

The owner selected the neutral `com.qaframework.<project>` convention. This
repository now uses Maven group `com.qaframework` and Java base package
`com.qaframework.playwright`; the artifact ID stays `qa-java-playwright`.
All 22 Java source paths/declarations/imports, TestNG listener/class/package
references, the logger prefix and current documentation were migrated together.
The earlier source-change description above applies to the initial adoption;
this follow-up additionally changes package identity without changing test logic.

After a clean compile, all 10 offline unit tests and Checkstyle passed. Controlled
Chromium and WebKit smoke each passed 2/2; the default/public suite passed 36/36
with no failures, errors or skips. All-file hooks passed. Every Java package
matched its source path, all suite class/package references resolved, and active
sources/configuration/docs contained no obsolete namespace. The prior Firefox
host limitation and separate Java 17 runtime evidence remain outstanding.

The first namespace commit was correctly blocked by the hook: pre-commit split
the many Java/config paths into concurrent Maven batches, which collided on
`target/checkstyle-result.xml`. Mark the Java hook `require_serial: true` so its
batches share Maven’s output sequentially. This fixes the observed report parse
failure without bypassing checks or changing style rules; the full staged rename
is the regression case. No application tests or runtime settings were changed.

### Showcase standards rework (2026-10-25)

Adopted the merged standards revision `073f51ff68ec050f6b3372c46bc3bd4e721a5342`
for the `complete` profile. Restore landing-page depth: Shields.io badges,
concrete capabilities, an inline execution diagram, structure/stack summaries,
template adaptation steps and a local Allure preview. Keep detailed setup and
rollout history here rather than in the root README. Add the TestNG/context
lifecycle sequence to the architecture guide and remove unsupported promotional
wording from maintained guides. Runtime, dependency, hook and workflow files
were unchanged by this documentation rework.

The preview was generated from a clean build followed by `unit.xml` (10/10) and
controlled Chromium `smoke.xml` with `-Denvironment=local` (2/2), then
`./mvnw -o -B -ntp allure:report -Dallure.history.enabled=false`. It is a scoped
local example, not a screenshot of the full public-site suite or hosted CI.
The editable Mermaid source stays inline in each Markdown document.

At prior head `0274192`, hosted quality/unit checks, Windows browser tests and
CodeQL succeeded. macOS 14 timed out after 25 minutes while launching WebKit in
the controlled smoke suite; its log warns that this host receives a frozen WebKit
build. The public-site step on that job was not reached. Report deployment was
skipped as expected for a PR. This is outstanding macOS evidence, not a passing
matrix or an authorized exception. Keep that check enabled for the new revision.
The separately documented local Firefox/macOS 27 failure remains outstanding.

Metadata audit: the current description is 97 characters without a trailing
period, but omits the starter-template qualifier. Proposed description for owner
review: `Tests browser flows with Java, Playwright and TestNG, with Allure reports (starter template)`.
Proposed topics, grouped by category/language/tools/technique:
`test-automation`, `java`, `playwright`, `testng`, `allure-report`, `web-testing`.
The topic set already matches; no repository metadata or account settings were
changed. Native GitHub topic display order is not evidence of policy ordering.

Validation for this rework: both inline diagrams rendered successfully with
Mermaid 12.2.0 in Chromium and were visually inspected; a sequence-label semicolon
that initially caused a parse error was corrected. All maintained local file
links and Markdown anchors resolve. The report PNG was inspected after capture
and shows 12/12 passing cases without unrelated cached history. Markdown lint and
whole-file hooks use the existing pinned configuration. No new validator or test
suite was added for the documentation layout.

The pinned standards tree and published report URLs returned HTTP 200 during
link review. Representative Shields.io badge requests returned HTTP 403 from
this host, so live badge rendering was not verified here; the README uses the
standard Shields.io endpoint syntax. New-head GitHub checks must be assessed
separately from the prior-head results above.

Independent documentation review found no material requirement or source-accuracy
issues. Its minor public-service-list omission was corrected in the README and
architecture guide: a bundled parallel example also calls httpbin.org. Review
scope excluded changes to existing browser teardown, public-site test behavior,
hosted native execution and account settings; those limits remain explicit above.

### README motivation and documentation review (2026-10-25)

Restored the Java-team setup rationale in the README, connected capabilities to
their practical benefits and moved the scoped report preview into Verification.
Replaced grouped path blocks with directory trees and normalized guide headings,
component tables, suite selection and example descriptions. Checked the maintained
documentation, including `AGENTS.md`, against the sources and pinned standards.
No runtime, dependency, hook or workflow implementation changed.

The accuracy pass distinguishes one selected browser per run from CI's browser
loop, suite-based retry/parallel configuration from direct class selection, and
conditional failure screenshots from guaranteed attachments. It also records
that the retry example does not deliberately fail, search checks visibility,
CSV returns rows under `data`, and shipped environment URLs are demo defaults.
The architecture example uses `orElseThrow()` to avoid silently skipped assertions.

Fresh JDK 21 verification passed 10/10 configuration units and Checkstyle with
zero violations. A temporary copy of the same runtime sources also passed those
checks and controlled Chromium smoke (2/2, zero failures, errors or skips).
The primary checkout's smoke attempt failed with `NoSuchMethodError` for
`Page.route`: its class file expected a `void` return, while the selected library
and temporary copy used `AutoCloseable`. This establishes incompatible compiled
output in the primary checkout; the process that wrote it was not identified.
No runtime workaround or IDE-setting change was made. Public-site and hosted
matrix checks were not rerun for this documentation change.

All three architecture Java snippets compiled with `--release 17` against the
framework dependencies. Pinned all-file hooks passed. Markdown was rendered in
Chromium with Mermaid 12.2.0; both diagrams rendered, local file links and anchors
resolved, and directory trees were visually inspected. Independent review found
one minor search-description overstatement, corrected. Changes remain uncommitted
for owner review.
