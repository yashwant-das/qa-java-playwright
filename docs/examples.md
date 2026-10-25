# Examples

[Development](development.md) explains runtime overrides, suites and reports.
These examples exercise public websites and may need adjustment when those sites
change. This separate catalog preserves the framework usage examples without
overloading the entry point.

## Playwright website tests

The `PlaywrightDocsNavigationTest` class demonstrates various interactions with the Playwright documentation website:

- **Homepage Navigation Test**: Verifies basic navigation and "Get Started" functionality
- **Java Documentation Test**: Tests navigation to Java-specific documentation
- **Search Functionality Test**: Validates the search feature and modal dialog
- **Tools Navigation Test**: Tests navigation between different Playwright tools (Codegen, Trace Viewer)

**Run these tests:**

```bash
./mvnw test -Dtest=PlaywrightDocsNavigationTest
```

Or using the example suite:

```bash
./mvnw test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml
```

## Framework capabilities showcase tests

The bundled examples cover these framework mechanisms:

- **ParallelExecutionTest**: Demonstrates parallel test execution with thread-safe browser handling
- **DataDrivenTest**: Shows data-driven testing with YAML, JSON, and CSV data sources
- **ScreenshotAndReportingTest**: Demonstrates screenshot capture and Allure reporting integration
- **WaitStrategiesTest**: Shows different wait strategies and Playwright's auto-waiting capabilities
- **MultiBrowserTest**: Demonstrates cross-browser testing (Chromium, Firefox, WebKit)
- **RetryMechanismTest**: Shows test retry mechanism for handling flaky tests
- **FormInteractionTest**: Demonstrates form filling, validation, and submission
- **TestDataManagementTest**: Demonstrates loading and using test data from multiple sources

**Run all showcase tests:**

```bash
./mvnw test -Dtest='*Test'
```

**Run specific showcase test:**

```bash
./mvnw test -Dtest=ParallelExecutionTest
./mvnw test -Dtest=DataDrivenTest
./mvnw test -Dtest=ScreenshotAndReportingTest
./mvnw test -Dtest=WaitStrategiesTest
./mvnw test -Dtest=MultiBrowserTest
./mvnw test -Dtest=RetryMechanismTest
./mvnw test -Dtest=FormInteractionTest
./mvnw test -Dtest=TestDataManagementTest
```
