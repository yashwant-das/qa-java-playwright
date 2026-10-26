# Examples

[Project overview](../README.md) · [Development](development.md) · [Architecture](architecture.md)

The bundled examples show how to use the framework's page objects, data loaders,
reporting and execution settings. Most contact public websites, whose content
and availability can change. `TestDataManagementTest` only exercises local data
utilities. For the first browser run, use the controlled `smoke.xml` suite in
[Quickstart](../README.md#quickstart).

## Playwright website tests

`PlaywrightDocsNavigationTest` covers these interactions with Playwright's public
documentation site:

| Method | Interaction |
| --- | --- |
| `testHomePageNavigation` | Open the Java documentation and follow Get Started |
| `testJavaDocumentation` | Navigate to Java-specific documentation |
| `testSearch` | Open the search dialog and verify its input is visible |
| `testToolsNavigation` | Visit Codegen and Trace Viewer documentation |

Run the focused suite from the repository root to retain retry and parallel
listeners:

```bash
./mvnw test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml -Dbrowser.headless=true
```

## Framework examples

| Class | What it exercises | Execution boundary |
| --- | --- | --- |
| `ParallelExecutionTest` | Independent navigation methods with per-worker browser handles | Parallel execution requires the suite listener and enabled configuration |
| `DataDrivenTest` | Page interactions using YAML, JSON and CSV data | Uses local fixtures and public websites |
| `ScreenshotAndReportingTest` | An explicit screenshot attachment and annotated report steps | Passing examples; they do not deliberately test the failure listener |
| `WaitStrategiesTest` | Element, navigation and page-load waits | Uses public Playwright pages |
| `MultiBrowserTest` | Navigation and element interaction using the selected browser | One browser per run; it does not launch a three-browser matrix itself |
| `RetryMechanismTest` | Tests that can be retried when suite retry behavior is enabled | Does not deliberately fail the first attempt or prove a retry occurred |
| `FormInteractionTest` | Form fields and submission | Uses Selenium's public web form |
| `TestDataManagementTest` | YAML/JSON/CSV loading and generated data | Browser-free; does not extend `PlaywrightTest` |

Run the default suite for configuration units and all bundled examples:

```bash
./mvnw test -Dbrowser.headless=true
```

To explore one class directly:

```bash
./mvnw test -Dtest=FormInteractionTest -Dbrowser.headless=true
```

Direct `-Dtest` selection bypasses the suite's retry and parallel listeners.
To retain those behaviors for a subset, copy the focused suite, change its class
and method entries, and select it with `-DsuiteXmlFile`. See
[suite selection](development.md#suite-selection).

## Browser selection

Install matching binaries before selecting Firefox or WebKit. Each command runs
the selected tests in one browser:

```bash
./mvnw test -Dtest=MultiBrowserTest -Dbrowser.type=chromium -Dbrowser.headless=true
./mvnw test -Dtest=MultiBrowserTest -Dbrowser.type=firefox -Dbrowser.headless=true
./mvnw test -Dtest=MultiBrowserTest -Dbrowser.type=webkit -Dbrowser.headless=true
```

CI separately runs the controlled smoke suite in all three browsers. Its public
examples use Chromium. Configured coverage is separate from the
[recorded native-host results](development.md#verification-status).
