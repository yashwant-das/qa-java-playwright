# Development

[Project overview](../README.md) · [Architecture](architecture.md)

## Setup

Use JDK 21 for the supported build toolchain and set `JAVA_HOME` accordingly.
The bytecode/API target remains Java 17, preserving the existing compatibility
contract; raising that target requires owner approval. A Java 17 runtime run is
tracked separately below. On macOS with Homebrew OpenJDK 21:

```bash
export JAVA_HOME="$(brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
./mvnw --version
./mvnw test-compile
./mvnw exec:java -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install chromium"
```

Maven Wrapper downloads its checksum-pinned distribution on first use. Maven
resolves dependencies from Central; browser installation needs Playwright’s CDN.
Surefire disables automatic browser downloads: install the chosen browser explicitly
before tests. A missing browser is a setup failure rather than a network side
effect during a test. No Node application or global Maven installation is required. On Windows use
`mvnw.cmd` in place of `./mvnw` and quote each `-Dkey=value` argument in PowerShell.

## Running tests

**Basic Test Execution:**

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=PlaywrightDocsNavigationTest

# Run specific test method
./mvnw test -Dtest=PlaywrightDocsNavigationTest#testHomePageNavigation
```

**Browser Selection:**

```bash
# Run tests in Chromium (default)
./mvnw test -Dbrowser.type=chromium

# Run tests in Firefox
./mvnw test -Dbrowser.type=firefox

# Run tests in WebKit
./mvnw test -Dbrowser.type=webkit
```

**Execution Modes:**

```bash
# Run tests in headless mode (faster, no UI)
./mvnw test -Dbrowser.headless=true

# Run tests in headed mode (see browser)
./mvnw test -Dbrowser.headless=false

# Run tests with specific environment (dev, qa or prod; qa is the default)
./mvnw test -Denvironment=prod
```

`-Dheadless=true|false` is accepted as a short form of `-Dbrowser.headless`.

**Test Suite Execution:**

```bash
# Run main test suite (all tests)
./mvnw test

# Run example suite (playwright.dev tests only)
./mvnw test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml
```

**Test Filtering:**

```bash
# Run only the tests in a TestNG group
./mvnw test -Dgroups=smoke

# Run specific test class
./mvnw test -Dtest=ParallelExecutionTest
```

A test joins a group through its annotation, e.g. `@Test(groups = "smoke")`. The only test in the `smoke` group out of the box is `PlaywrightDocsNavigationTest#testHomePageNavigation`.

> **Note:** `-Dtest=...` makes Maven run the named classes directly instead of a suite file. The retry and parallel settings are applied by listeners declared in the suite files, so they do not take effect in a `-Dtest` run. Screenshots on failure still work. To run a subset with retries and parallel execution, use a suite file (`-DsuiteXmlFile`) or a group (`-Dgroups`).

### Configuration Overrides

Any setting in the environment's YAML file can be overridden at runtime with a `-D` property named after its YAML path (`section.key`):

```bash
# Override browser settings
./mvnw test -Dbrowser.type=firefox -Dbrowser.headless=true

# Override parallel execution
./mvnw test -DtestExecution.parallel=false
./mvnw test -DtestExecution.threadCount=4

# Override retry settings
./mvnw test -Dretry.enabled=true
./mvnw test -Dretry.maxRetries=3
./mvnw test -Dretry.delayBetweenRetries=2000

# Override any other YAML setting the same way
./mvnw test -Dbrowser.slowMo=250 -Dbrowser.timeout=60000
./mvnw test -Denvironment.baseUrl=https://playwright.dev/

# Select the environment file
./mvnw test -Denvironment=qa
```

An override must be valid for the setting's type. A non-numeric value for a numeric setting (for example `-DtestExecution.threadCount=many`) stops the run with a configuration error.

## Configuration

The framework uses YAML configuration files located in `src/test/resources/config/` as the single source of truth for all configuration settings.

### Configuration Files

- `dev.yaml` - Development environment configuration
- `qa.yaml` - QA environment configuration
- `prod.yaml` - Production environment configuration

### Example Configuration (qa.yaml)

```yaml
environment:
  name: qa
  baseUrl: https://playwright.dev/

browser:
  type: chromium  # chromium, firefox, or webkit
  headless: false
  slowMo: 0       # milliseconds to wait between actions
  timeout: 30000  # default timeout in milliseconds

screenshot:
  takeOnFailure: true
  fullPage: true

testExecution:
  parallel: true     # enable/disable parallel execution
  threadCount: 3     # number of parallel threads to use

retry:
  enabled: true      # enable/disable test retries
  maxRetries: 2      # maximum number of retry attempts
  delayBetweenRetries: 1000  # delay in milliseconds between retries
```

### Configuration Management

The framework uses a configuration management system that:

- Centralizes all settings in YAML configuration files
- Supports environment-specific configurations
- Allows runtime overrides of any setting via `-D` system properties
- Uses TestNG listeners to apply the parallel and retry settings to the suite
- Provides automatic test retry capabilities

### Test Retry Configuration

Configure retry behavior through YAML:

```yaml
retry:
  enabled: true      # Enable/disable retry mechanism
  maxRetries: 2      # Maximum retry attempts (0-N)
  delayBetweenRetries: 1000  # Milliseconds to wait between retries
```

Or override at runtime:

```bash
./mvnw test -Dretry.enabled=true -Dretry.maxRetries=3 -Dretry.delayBetweenRetries=2000
```

### Parallel Execution

Configure parallel execution through YAML:

```yaml
testExecution:
  parallel: true
  threadCount: 3
```

Or override at runtime:

```bash
./mvnw test -DtestExecution.parallel=false
./mvnw test -DtestExecution.threadCount=4
```

## Reports

Allure reports include:

### Allure Reports

Each test run writes Allure result data to `target/allure-results/`:

**Generate and View Allure Reports:**

```bash
# Generate Allure report
./mvnw allure:report

# Serve interactive report
./mvnw allure:serve
```

**What the report shows:**

- Test result dashboard with pass/fail statistics
- Tests grouped by suite, package and by the `@Epic`, `@Feature` and `@Story` annotations
- Step-by-step execution details from `@Step` methods
- A screenshot attached to each failed test
- Timeline view of test execution
- Severity, description and duration of each test

The framework does not write an Allure environment file, so the report’s Environment widget is empty. CI starts with a clean checkout, so deployments do not carry history from earlier runs. The report plugin may retain history between local runs; remove `.allure/` to reset it.

**CI/CD Test Results:**

The test results are published to GitHub Pages only after successful trusted-branch CI checks. Pull requests upload reports as artifacts without publishing them. You can view them at:
[Published Allure reports](https://yashwant-das.github.io/qa-java-playwright/)

The published report is from the latest run on macOS, with a link to the Windows run. Each deployment replaces the previous one.

### Screenshots

Screenshots are automatically captured and attached to reports:

- **On Failure**: All failed tests automatically capture screenshots
- **Integration**: Screenshots are embedded in Allure reports
- **Storage**: `target/screenshots/`, named after the test with a timestamp
- **Scope**: Full page or viewport only, set by `screenshot.fullPage`

## Logging

Logging writes to:

- **Test Execution Logs**: `logs/playwright-tests.log`
- **Archived Logs**: `logs/archived/playwright-tests.YYYY-MM-DD.i.log`
- **Console Logging**: Real-time test execution information
- **Log Levels**: DEBUG, INFO, WARN, ERROR

### Log Rotation Policy

- Log files are automatically rotated when:
  - Size reaches 10MB
  - A new day starts
- Retention policy:
  - Keeps logs for 30 days
  - Total size cap of 100MB for all archived logs

### Log Levels

Default log levels are configured as follows:

- Framework code (com.qaframework.playwright): DEBUG
- TestNG: INFO
- Playwright: INFO
- Root logger: INFO

### Customizing Logging

You can modify logging behavior by editing `src/test/resources/logback.xml`:

- Change log levels
- Modify rotation policies
- Add new appenders
- Customize log patterns

## Test data

TestDataManager supports the fixture formats and examples below:

### Supported Data Formats

- **YAML**: `playwright-test-data.yaml`
- **JSON**: `test-data.json`
- **CSV**: `test-data.csv`

### Example: YAML Data

```yaml
users:
  admin:
    username: admin@playwright-test.com
    password: Test@1234
    role: ADMIN
    permissions:
      - read
      - write
      - delete

browsers:
  chromium:
    name: "Chromium"
    type: "chromium"
    headless: true
    viewport:
      width: 1920
      height: 1080
```

### Using Test Data in Tests

```java
@Test
public void testWithYamlData() {
    TestDataManager dataManager = new TestDataManager("qa");
    Map<String, Object> data = dataManager.loadYamlData("playwright-test-data.yaml");

    String username = (String) dataManager.getValue(data, "users.admin.username");
    String password = (String) dataManager.getValue(data, "users.admin.password");

    // Use data in test
}
```

### Dynamic Data Generation

```java
@Test
public void testWithDynamicData() {
    String email = TestDataGenerator.generateEmail();
    String password = TestDataGenerator.generatePassword(12, true);
    String phone = TestDataGenerator.generatePhoneNumber();

    // Use generated data in test
}
```

## Adding tests

Place page objects under `pages/` and browser examples under `examples/` in the
existing package. Extend `PlaywrightTest` for context setup and teardown; obtain
the page with `getCurrentPage().orElseThrow()` so absent setup cannot silently
skip assertions. Add assertions after actions and prefer role/label locators over
CSS class names. [Architecture](architecture.md#page-object-model) includes a
page object and test example. Add suite entries when a new package is outside the
existing examples scan.

The default `testng.xml` runs 10 configuration units plus the public examples.
`example-suite.xml` selects four Playwright documentation examples. `unit.xml`
is browser-free and `smoke.xml` selects the controlled browser tests. The local
smoke config disables retries and parallel execution. Set `-Denvironment=local`
when selecting the smoke suite; public examples navigate to hard-coded URLs
and are not redirected merely by changing `environment.baseUrl`.

## Troubleshooting

### Common Issues

**Browser Not Installed:**

- Ensure the browser is installed before running tests
- Install Playwright browsers: `./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"`

**Java Version Issues:**

- Verify Java version: `java -version` (use JDK 21)
- Maven may use a different JDK than `java` on your PATH. Check the "Java version" line of `./mvnw -version`, and set `JAVA_HOME` if it is not the JDK you expect
- Use the supported JDK 21 build toolchain; other JDK majors need separate compatibility evidence. Check `JAVA_HOME` if Maven selects another installation.

**Maven Dependency Issues:**

- Clean and rebuild: `./mvnw clean install`
- Verify the wrapper version: `./mvnw --version`

**Configuration Errors:**

- Verify `src/test/resources/config/*.yaml` files exist and are valid YAML
- Check `src/test/resources/data/*` files are valid

**Import Errors:**

- Verify all dependencies are installed: `./mvnw dependency:resolve`
- Check Java version compatibility

### Getting Help

If tests fail, check:

1. Log files in `logs/` directory for detailed error messages
2. Screenshots in `target/screenshots/` for visual debugging
3. Allure reports for test execution details
4. GitHub Actions logs for CI/CD issues

The framework provides clear error messages to help diagnose issues quickly.

## Quality gates and hooks

Install [pre-commit](https://pre-commit.com/) 4.7.0, then run from the root:

```bash
# With uv available; pipx install pre-commit==4.7.0 is an alternative.
uvx --python 3.13 --from pre-commit==4.7.0 pre-commit install
./mvnw -B -ntp checkstyle:check
uvx --python 3.13 --from pre-commit==4.7.0 pre-commit run --all-files
./mvnw -B -ntp clean verify -DsuiteXmlFile=src/test/resources/suites/unit.xml
```

`pre-commit install` creates the Git hook. On Windows run Maven and hooks from
Git Bash, with JDK 21 on PATH and `JAVA_HOME` set. Hook installation downloads
pinned Python/Node tool environments. The Java hook uses cached Maven in offline
mode; warm it with the command above. No browser, credentials or remote service
is used after setup. If dependencies or plugins change, rerun the online warmup.

Hooks check staged text and config files for trailing whitespace, final newlines,
merge conflicts and YAML/strict JSON/XML syntax. Two-space Markdown hard breaks
are preserved. markdownlint-cli2 0.24.0 uses defaults with only MD013 disabled.
A staged Markdown lint configuration change also parses that JSONC configuration
while checking the README, so config-only commits cannot skip validation.
Generated output and dependencies are excluded by path. Wrapper scripts remain
upstream-generated files, with their provenance in the properties file.

For a Java-only commit, the staged-path adapter passes those source-relative
files to Checkstyle. A staged POM, Checkstyle config or adapter change checks all
tracked Java sources from `git ls-files`; unfinished untracked drafts are excluded.
Pre-commit hides unstaged tracked edits while checking the index snapshot. The
rules check tabs, final newlines, modifier order, empty statements, equals/hashCode
pairs, fall-through, top-level classes and catches of Error/Throwable. This is
neither full compilation nor a security analyzer. CI `verify` checks full source
roots independently in its clean checkout. Fix and restage any hook edits;
never bypass a failed gate. CI runs the same hook configuration on all tracked
files, plus compilation and unit tests on Linux.

Proposed required contexts for owner review:

- `Quality and unit tests`
- `Run Tests (macos-14)`
- `Run Tests (windows-latest)`
- `Analyze (java-kotlin)`

The browser jobs run controlled Chromium, Firefox and WebKit smoke, plus the complete public-site suite,
then upload Allure reports. Public-site failures remain failures; no job ignores
them. Each browser is exercised by the smoke suite; the public examples use Chromium. `Deploy Report` runs only on successful `main`
quality, browser and CodeQL checks from a non-PR event, writes only the existing `gh-pages` branch and retains
the macOS/Windows report links. CI artifacts expire after three days.

Weekly Maven and action update PRs are configured in
[Dependabot](../.github/dependabot.yml). Major upgrades need review. Actions are
SHA-pinned; test jobs have read-only permissions and bounded runtimes. Account
protection and deployment-environment changes require owner approval; workflow
presence alone does not enforce these checks for merging. The public repository
had no rulesets and an unprotected `main` at audit; no account settings were
changed. The CodeQL job compiles the test-source framework for analysis and also gates
report publication; its hosted result is separate evidence, not a locally verified claim.

## Standards adoption

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
