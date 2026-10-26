# Development

[Project overview](../README.md) · [Architecture](architecture.md)

## Setup

Use JDK 21 for the supported build toolchain and set `JAVA_HOME` accordingly.
The compiler API/bytecode target is also Java 21. Java 17 is no longer a supported
target following the owner-approved baseline alignment. On macOS with Homebrew OpenJDK 21:

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

Run commands from the repository root. Install the selected browser before
running a browser suite. The default environment is `qa`, with headed Chromium.

### Suite selection

| Selection | Command | Scope |
| --- | --- | --- |
| Default | `./mvnw test` | Configuration units and all bundled examples, including public websites |
| Public documentation examples | `./mvnw test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml` | Four Playwright documentation tests, with suite listeners |
| Configuration units | `./mvnw verify -DsuiteXmlFile=src/test/resources/suites/unit.xml` | Browser-free configuration tests and Checkstyle |
| Controlled smoke | `./mvnw test -DsuiteXmlFile=src/test/resources/suites/smoke.xml -Denvironment=local` | Real-browser form and context-isolation checks, without public websites |
| Public smoke group | `./mvnw test -Dgroups=smoke` | The default suite's `PlaywrightDocsNavigationTest#testHomePageNavigation` |

The `smoke` group and `smoke.xml` suite select different tests. The local smoke
configuration disables retries and parallel execution. The other browser commands
above retain the selected environment's settings; add `-Dbrowser.headless=true`
to hide browser windows.

### Selecting classes and methods

```bash
# Run a specific test class
./mvnw test -Dtest=PlaywrightDocsNavigationTest

# Run a specific test method
./mvnw test -Dtest=PlaywrightDocsNavigationTest#testHomePageNavigation
```

`-Dtest=...` bypasses the suite file. Retry and parallel settings are applied by
listeners registered in the suite files, so those settings do not take effect in
a direct class/method run. The inherited screenshot listener still applies to
classes extending `PlaywrightTest`. Use a suite file or a group when you need the
suite's retry and parallel behavior.

### Browser and environment selection

```bash
# Select one browser per run (install it first)
./mvnw test -Dbrowser.type=chromium
./mvnw test -Dbrowser.type=firefox
./mvnw test -Dbrowser.type=webkit

# Select headed or headless mode
./mvnw test -Dbrowser.headless=false
./mvnw test -Dbrowser.headless=true

# Select the production configuration file
./mvnw test -Denvironment=prod
```

`-Dheadless=true|false` is accepted as a short form of `-Dbrowser.headless`.
The shipped `dev`, `qa` and `prod` base URLs all point to Playwright's website;
the environment names do not represent deployed application environments.

### Configuration overrides

Existing settings in the selected YAML file can be overridden at runtime with a
`-D` property named after the YAML path (`section.key`). Unknown paths are ignored:

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

YAML files in `src/test/resources/config/` supply the defaults. Command-line
properties override existing paths before the result is bound to `ConfigModel`.

### Environment files

| File | Initial URL | Browser mode | Parallel methods | Retries |
| --- | --- | --- | --- | --- |
| `dev.yaml` | `https://playwright.dev/` | Headed Chromium, 100 ms slow motion | Disabled | Disabled |
| `qa.yaml` (default) | `https://playwright.dev/` | Headed Chromium | 3 threads | Up to 2, 1000 ms delay |
| `prod.yaml` | `https://playwright.dev/` | Headless Chromium | 2 threads | Up to 1, 2000 ms delay |
| `local.yaml` | `about:blank` | Headless Chromium | Disabled | Disabled |

### Example configuration (`qa.yaml`)

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

### Configuration management

The framework uses a configuration management system that:

- Centralizes all settings in YAML configuration files
- Supports environment-specific configurations
- Allows runtime overrides of any setting via `-D` system properties
- Uses TestNG listeners to apply the parallel and retry settings to the suite
- Provides automatic test retry capabilities

### Retry configuration

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

### Parallel execution

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

### Allure reports

Each test run writes Allure result data to `target/allure-results/`.
Generate or serve the report from the repository root:

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
- Failure screenshots when enabled, an active browser page is available and capture succeeds
- Timeline view of test execution
- Severity, description and duration of each test

The framework does not write an Allure environment file, so the report’s Environment widget is empty. CI starts with a clean checkout, so deployments do not carry history from earlier runs. The report plugin may retain history between local runs; remove `.allure/` to reset it.

### CI reports

The test results are published to GitHub Pages only after successful trusted-branch CI checks. Pull requests upload reports as artifacts without publishing them. You can view them at:
[Published Allure reports](https://yashwant-das.github.io/qa-java-playwright/)

The published report is from the latest run on macOS, with a link to the Windows run. Each deployment replaces the previous one.

### Screenshots

`ScreenshotListener` attempts capture for failed tests extending `PlaywrightTest`
when `screenshot.takeOnFailure=true` and a page is still available. Capture or
attachment errors are logged; setup failures and browser-free tests may have no
page to capture.

| Setting or output | Behavior |
| --- | --- |
| `screenshot.fullPage` | Selects full-page or viewport capture |
| `target/screenshots/` | Stores PNGs named with the test and a timestamp |
| Allure attachments | Embeds successfully captured failure screenshots |

## Logging

| Output | Location |
| --- | --- |
| Current execution log | `logs/playwright-tests.log` |
| Archived execution logs | `logs/archived/playwright-tests.YYYY-MM-DD.i.log` |
| Console | Execution events with color-coded levels |

### Log rotation

Logs rotate daily or when they reach 10 MB. Archives are retained for up to
30 days, with a 100 MB total size cap.

### Log levels

| Logger | Default level |
| --- | --- |
| `com.qaframework.playwright` | DEBUG |
| TestNG | INFO |
| Playwright | INFO |
| Root | INFO |

### Customizing logging

You can modify logging behavior by editing `src/test/resources/logback.xml`:

- Change log levels
- Modify rotation policies
- Add new appenders
- Customize log patterns

## Test data

`TestDataManager` loads and caches classpath fixtures. It looks in
`data/<environment>/` first, then falls back to `data/`. YAML and JSON return
maps; CSV rows are returned under the `data` key.

### Supported data formats

- **YAML**: `playwright-test-data.yaml`
- **JSON**: `test-data.json`
- **CSV**: `test-data.csv`

### YAML fixture example

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

### Using test data in tests

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

### Dynamic data generation

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

### Browser not installed

- Ensure the browser is installed before running tests
- Install Playwright browsers: `./mvnw exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"`

### Java version issues

- Verify Java version: `java -version` (use JDK 21)
- Maven may use a different JDK than `java` on your PATH. Check the "Java version" line of `./mvnw -version`, and set `JAVA_HOME` if it is not the JDK you expect
- Use the supported JDK 21 build toolchain; other JDK majors need separate compatibility evidence. Check `JAVA_HOME` if Maven selects another installation.

### Maven dependency issues

- Clean and rebuild: `./mvnw clean install`
- Verify the wrapper version: `./mvnw --version`

### Configuration errors

- Verify `src/test/resources/config/*.yaml` files exist and are valid YAML
- Check `src/test/resources/data/*` files are valid

### Import errors

- Verify all dependencies are installed: `./mvnw dependency:resolve`
- Check Java version compatibility

### Runtime method mismatch

`NoSuchMethodError` can mean the compiled test classes and runtime library have
different API signatures. Rebuild with `./mvnw clean test-compile` and check the
IDE's resolved dependencies if it also writes to `target/test-classes/`. Verify
in a fresh checkout if the IDE and Maven keep replacing each other's output.

### Getting help

If tests fail, check:

1. Log files in `logs/` directory for detailed error messages
2. Screenshots in `target/screenshots/` for visual debugging
3. Allure reports for test execution details
4. GitHub Actions logs for CI/CD issues

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

Required contexts enforced by the active `main-rule` ruleset:

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
protection and deployment-environment changes require owner approval. Following
the owner's standards-alignment request on 2026-10-25, the active
[`main-rule` ruleset](https://github.com/yashwant-das/qa-java-playwright/rules/25824925)
requires the four checks above from GitHub Actions (app 15368), with the branch up
to date. The existing deletion and force-push protections remain, with no bypass
actors. `Deploy Report` is not required because PRs intentionally skip publication.
The CodeQL job compiles the test-source framework for analysis and also gates
report publication; local verification does not replace its hosted result.

## Compatibility decisions

The repository uses the `complete` profile from
[repo-standards at 073f51ff68ec050f6b3372c46bc3bd4e721a5342](https://github.com/yashwant-das/repo-standards/tree/073f51ff68ec050f6b3372c46bc3bd4e721a5342),
pinned in [AGENTS.md](../AGENTS.md). Exact dependency versions live in the
[POM](../pom.xml). The decisions below preserve the existing test engine and
report integration; they do not waive failed checks.

| Area | Decision and reason | Owner / review trigger |
| --- | --- | --- |
| Java | Build and target Java 21 (`release=21`), aligned with the shared baseline and existing CI toolchain. The owner approved replacing the former Java 17 target. | Yashwant Das; review when dependencies or the selected JDK vendor's support require a baseline change. |
| Allure | Retain adapter 2.35.5: candidate 3.0.0 failed compilation against the attachment API used here. Report 2.46.1 with Maven plugin 3.1.0 preserves the existing report format. | Yashwant Das; review when updating attachment integration or intentionally migrating report formats. |
| Surefire | Retain 3.5.6: candidate 3.6.0 did not honor the TestNG XML selection in the unit-only check and started a browser example. | Yashwant Das; review when changing the test engine or reevaluating native TestNG suite support. |
| Browser setup | Surefire sets `PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1`. Install package-matched binaries explicitly so test startup does not download browsers. | Yashwant Das; review when changing Playwright startup or browser installation. |
| README title | Use the owner-approved display title `Playwright Java Accelerator`; the repository identifier remains `qa-java-playwright`. | Yashwant Das; review when renaming the project or changing its identity. |

## Verification status

Recorded evidence as of 2026-10-26. Local runs used macOS 27.0.1 on Apple Silicon,
Homebrew JDK 21.0.13 and Maven 3.10.0. Local Java 21 results cover the working tree
based on `424b193`, with `release=21`. The recorded hosted results cover the
older revision `424b193`, before the target alignment. Assess each new PR head's
checks independently; old hosted results do not validate later changes.

| Check | Recorded result | Date and scope |
| --- | --- | --- |
| Configuration units and Checkstyle | 10/10 passed; zero violations | 2026-10-26; fresh temporary copy with Java 21 target |
| Controlled Chromium smoke | 2/2 passed; zero failures, errors or skips | 2026-10-26; fresh temporary copy with Java 21 target |
| Compiler target | All 27 generated class files use major version 65 (Java 21) | 2026-10-26; clean build with JDK 21 and `release=21` |
| Controlled WebKit smoke | 2/2 passed locally | 2026-10-23; namespace-migration verification, before `424b193`; not rerun in the documentation pass |
| Default/public suite | 36/36 passed; zero failures, errors or skips | 2026-10-26; fresh temporary copy with Java 21 target, default retries/parallelism and headless Chromium |
| Hosted quality, Windows browser tests and CodeQL | Passed | Rechecked at `424b193`, before the Java 21 target alignment |
| Hosted macOS browser job | Cancelled after about 25 minutes; not a pass | Rechecked at `424b193`; earlier `0274192` timed out launching WebKit |
| Local Firefox smoke | Native startup failed; both tests skipped after setup failure | 2026-10-23; Firefox 157.0, Playwright build 1555 on macOS 27.0.1 |
| Documentation and hooks | Pinned all-file hooks passed; three architecture Java snippets compiled; both Mermaid diagrams rendered; local links and anchors resolved | 2026-10-26 hooks, local links and image rendering; Java snippets and Mermaid diagrams verified 2026-10-25 |

The owner-supplied Allure preview records an earlier local run with 10 passing
configuration tests and two broken controlled browser checks (83.33% passing).
It illustrates a failure report; it does not replace the separately recorded
successful clean-copy tests or establish hosted browser success.

## Known limitations and outstanding checks

- **Native browsers:** Local Firefox failed during startup with “Could not find
  profile folder.” Symptoms match the upstream
  [macOS 27 Firefox issue](https://github.com/microsoft/playwright/issues/42768)
  and [Mozilla diagnostic](https://bugzilla.mozilla.org/show_bug.cgi?id=2062988),
  but the host-specific cause was not proven. Hosted macOS 14 WebKit timed out
  with a frozen-build warning. Keep both checks enabled. Owner: Yashwant Das;
  review after relevant host/browser changes or upstream fixes.
- **Compiled output:** The primary checkout's smoke class expected `Page.route`
  to return `void`; the selected library and freshly compiled temporary copy used
  `AutoCloseable`. The writer of the incompatible class was not identified.
  Follow [runtime method mismatch](#runtime-method-mismatch) if this recurs.
- **Browser lifecycle:** Suite teardown closes handles visible to its executing
  thread; cleanup of every worker's thread-local browser handles is not guaranteed.
  See [execution boundaries](architecture.md#execution-and-boundaries).
- **Validation scope:** Hosted checks must pass on the revision being merged.
  Local success does not establish hosted success. Earlier Java 17
  compilation evidence is historical and does not describe the current target.
- **Merge readiness:** The macOS check remains required even while its recorded
  run is cancelled. Resolve the native-browser failure and obtain passing checks
  for the revision being merged; do not bypass it based on local Chromium success.

## Repository metadata

The GitHub description is:

> Tests browser flows with Java, Playwright and TestNG, with Allure reports (starter template)

The owner-authorized update was read back on 2026-10-25. It is 92 characters,
identifies the core tools and includes the starter-template qualifier. Repository
and artifact identifiers remain `qa-java-playwright`; the display title remains
`Playwright Java Accelerator`.

The [historical adoption record](superpowers/history/2026-10-25-standards-adoption.md)
preserves the original audit, rejected candidates, hook probes, namespace migration
and documentation work log. Current setup instructions remain in this guide.
