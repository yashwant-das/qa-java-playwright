<div align="center">

  <h1>Playwright Java Accelerator</h1>

  <p><strong>A robust and maintainable Java test automation framework built with Playwright and TestNG</strong></p>

  <p>
    <img src="https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk&logoColor=white" alt="Java"/>
    <img src="https://img.shields.io/badge/Playwright-1.48+-2EAD33?logo=playwright&logoColor=white" alt="Playwright"/>
    <img src="https://img.shields.io/badge/TestNG-7.9+-FF6A00?logo=testng&logoColor=white" alt="TestNG"/>
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License: MIT"/>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Chromium-Supported-4285F4?logo=google-chrome&logoColor=white" alt="Chromium"/>
    <img src="https://img.shields.io/badge/Firefox-Supported-FF7139?logo=firefox-browser&logoColor=white" alt="Firefox"/>
    <img src="https://img.shields.io/badge/WebKit-Supported-000000?logo=safari&logoColor=white" alt="WebKit"/>
    <img src="https://img.shields.io/badge/Allure-Reports-FF6A00?logo=allure&logoColor=white" alt="Allure"/>
    <img src="https://img.shields.io/badge/Parallel-Testing-00D9FF?logo=parallel&logoColor=white" alt="Parallel Testing"/>
    <img src="https://img.shields.io/badge/CI/CD-GitHub Actions-2088FF?logo=github-actions&logoColor=white" alt="CI/CD"/>
  </p>

  <p>
    <img src="https://github.com/yashwant-das/qa-java-playwright/actions/workflows/playwright.yml/badge.svg" alt="Playwright Tests"/>
  </p>

</div>

![banner](https://github.com/user-attachments/assets/dd8a014a-8002-4568-a512-b0f818905b8e)

---

A robust and maintainable **Java test automation framework** built with **Playwright** and **TestNG**. This framework follows the Page Object Model (POM) design pattern and provides a comprehensive set of utilities for web application testing with modern browser automation capabilities.

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Usage](#usage)
- [Examples](#examples)
- [Page Object Model](#page-object-model)
- [Configuration](#configuration)
- [Reports](#reports)
- [Logging](#logging)
- [Advanced Features](#advanced-features)
- [Troubleshooting](#troubleshooting)
- [Framework Architecture](#framework-architecture)
- [Contributing](#contributing)
- [License](#license)

## Features

- **Page Object Model**: Organized page classes for better maintainability
- **Browser Support**: Chromium by default; switch to Firefox or WebKit in the YAML config or with `-Dbrowser.type`
- **Multi-Environment Support**: Built-in support for multiple environments (dev, qa, prod) with easy switching
- **Headless Mode**: Run tests without opening browser windows
- **Parallel Execution**: Run tests in parallel with configurable thread count for faster execution
- **Rich Reporting**: Interactive Allure reports with test steps and screenshots of failed tests
- **Auto-Waiting**: Playwright's built-in auto-waiting eliminates flaky tests
- **Logging**: Comprehensive logging with SLF4J and Logback
- **Configuration Management**: YAML configuration per environment, with any setting overridable from the command line
- **Data-Driven Testing**: Support for YAML, JSON, and CSV test data
- **Screenshot Capture**: Automatic screenshots on test failures with Allure integration
- **Test Retry Mechanism**: Configurable automatic retry for failed tests
- **CI/CD Integration**: GitHub Actions workflow for automated testing and reporting

## Project Structure

```text
qa-java-playwright/
│
├── .github/
│   └── workflows/
│       └── playwright.yml              # GitHub Actions CI/CD workflow
│
├── src/
│   └── test/
│       ├── java/
│       │   └── io/
│       │       └── github/
│       │           └── mypixelquest/
│       │               └── pja/
│       │                   ├── core/
│       │                   │   └── PlaywrightTest.java
│       │                   ├── config/
│       │                   │   └── ConfigModel.java
│       │                   ├── testdata/
│       │                   │   ├── TestDataManager.java
│       │                   │   └── TestDataGenerator.java
│       │                   ├── listeners/
│       │                   │   ├── ScreenshotListener.java
│       │                   │   ├── RetryAnalyzer.java
│       │                   │   ├── RetryListener.java
│       │                   │   └── TestConfigurationListener.java
│       │                   ├── pages/
│       │                   │   ├── BasePage.java
│       │                   │   └── PlaywrightDocsPage.java
│       │                   ├── examples/
│       │                   │   ├── PlaywrightDocsNavigationTest.java
│       │                   │   ├── TestDataManagementTest.java
│       │                   │   ├── ParallelExecutionTest.java
│       │                   │   ├── DataDrivenTest.java
│       │                   │   ├── ScreenshotAndReportingTest.java
│       │                   │   ├── WaitStrategiesTest.java
│       │                   │   ├── MultiBrowserTest.java
│       │                   │   ├── RetryMechanismTest.java
│       │                   │   └── FormInteractionTest.java
│       │                   └── util/
│       │                       ├── ConfigReader.java
│       │                       └── ConfigReaderOverrideTest.java
│       │
│       └── resources/
│           ├── config/
│           │   ├── dev.yaml
│           │   ├── qa.yaml
│           │   └── prod.yaml
│           ├── data/
│           │   ├── test-data.json
│           │   ├── test-data.csv
│           │   └── playwright-test-data.yaml
│           ├── logback.xml
│           └── suites/
│               ├── example-suite.xml
│               └── testng.xml
│
├── .gitignore
├── LICENSE
├── pom.xml
└── README.md
```

## Prerequisites

Before installing the framework, ensure you have:

- **Java 17+** (check with `java -version`)
- **Maven 3.6+** (check with `mvn -version`)
- **Chromium browser** (installed automatically by Playwright when you run `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"`).
  - The same command also installs Firefox and WebKit. The default configuration uses Chromium; select another browser in the YAML config or with `-Dbrowser.type`.

### Quick Setup Validation

After installation, you can verify your setup:

```bash
# Check Java version
java -version  # Should be 17+

# Check Maven version
mvn -version   # Should be 3.6+

# Check that the Playwright CLI runs (prints the install options)
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --help"
```

## Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/yashwant-das/qa-java-playwright.git
   cd qa-java-playwright
   ```

2. Install Playwright browser binaries:

   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"
   ```

   This will download browser binaries for Chromium, Firefox, and WebKit.

3. Verify installation:

   ```bash
   mvn clean compile
   ```

## Quick Start

Get up and running in minutes:

```bash
# 1. Clone and setup
git clone https://github.com/yashwant-das/qa-java-playwright.git
cd qa-java-playwright
mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"

# 2. Run your first test
mvn test -Dtest=PlaywrightDocsNavigationTest#testHomePageNavigation

# 3. View the Allure report
mvn allure:serve
```

That's it! The framework will automatically handle browser setup, create necessary directories, and generate reports.

## Usage

### Running Tests

**Basic Test Execution:**

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=PlaywrightDocsNavigationTest

# Run specific test method
mvn test -Dtest=PlaywrightDocsNavigationTest#testHomePageNavigation
```

**Browser Selection:**

```bash
# Run tests in Chromium (default)
mvn test -Dbrowser.type=chromium

# Run tests in Firefox
mvn test -Dbrowser.type=firefox

# Run tests in WebKit
mvn test -Dbrowser.type=webkit
```

**Execution Modes:**

```bash
# Run tests in headless mode (faster, no UI)
mvn test -Dbrowser.headless=true

# Run tests in headed mode (see browser)
mvn test -Dbrowser.headless=false

# Run tests with specific environment (dev, qa or prod; qa is the default)
mvn test -Denvironment=prod
```

`-Dheadless=true|false` is accepted as a short form of `-Dbrowser.headless`.

**Test Suite Execution:**

```bash
# Run main test suite (all tests)
mvn test

# Run example suite (playwright.dev tests only)
mvn test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml
```

**Test Filtering:**

```bash
# Run only the tests in a TestNG group
mvn test -Dgroups=smoke

# Run specific test class
mvn test -Dtest=ParallelExecutionTest
```

A test joins a group through its annotation, e.g. `@Test(groups = "smoke")`. The only test in the `smoke` group out of the box is `PlaywrightDocsNavigationTest#testHomePageNavigation`.

> **Note:** `-Dtest=...` makes Maven run the named classes directly instead of a suite file. The retry and parallel settings are applied by listeners declared in the suite files, so they do not take effect in a `-Dtest` run. Screenshots on failure still work. To run a subset with retries and parallel execution, use a suite file (`-DsuiteXmlFile`) or a group (`-Dgroups`).

### Configuration Overrides

Any setting in the environment's YAML file can be overridden at runtime with a `-D` property named after its YAML path (`section.key`):

```bash
# Override browser settings
mvn test -Dbrowser.type=firefox -Dbrowser.headless=true

# Override parallel execution
mvn test -DtestExecution.parallel=false
mvn test -DtestExecution.threadCount=4

# Override retry settings
mvn test -Dretry.enabled=true
mvn test -Dretry.maxRetries=3
mvn test -Dretry.delayBetweenRetries=2000

# Override any other YAML setting the same way
mvn test -Dbrowser.slowMo=250 -Dbrowser.timeout=60000
mvn test -Denvironment.baseUrl=https://playwright.dev/

# Select the environment file
mvn test -Denvironment=qa
```

An override must be valid for the setting's type. A non-numeric value for a numeric setting (for example `-DtestExecution.threadCount=many`) stops the run with a configuration error.

## Examples

### Playwright Website Tests

The `PlaywrightDocsNavigationTest` class demonstrates various interactions with the Playwright documentation website:

- **Homepage Navigation Test**: Verifies basic navigation and "Get Started" functionality
- **Java Documentation Test**: Tests navigation to Java-specific documentation
- **Search Functionality Test**: Validates the search feature and modal dialog
- **Tools Navigation Test**: Tests navigation between different Playwright tools (Codegen, Trace Viewer)

**Run these tests:**

```bash
mvn test -Dtest=PlaywrightDocsNavigationTest
```

Or using the example suite:

```bash
mvn test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml
```

### Framework Capabilities Showcase Tests

The framework includes comprehensive showcase tests demonstrating various capabilities:

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
mvn test -Dtest='*Test'
```

**Run specific showcase test:**

```bash
mvn test -Dtest=ParallelExecutionTest
mvn test -Dtest=DataDrivenTest
mvn test -Dtest=ScreenshotAndReportingTest
mvn test -Dtest=WaitStrategiesTest
mvn test -Dtest=MultiBrowserTest
mvn test -Dtest=RetryMechanismTest
mvn test -Dtest=FormInteractionTest
mvn test -Dtest=TestDataManagementTest
```

## Page Object Model

The framework follows the Page Object Model (POM) design pattern for better maintainability and code reusability.

### Base Page

All page objects extend `BasePage`, which provides common functionality:

```java
public abstract class BasePage {
    protected final Page page;
    
    public BasePage(Page page) {
        this.page = page;
    }
    
    // Common methods available to all page objects
    public String getTitle() { /* ... */ }
    public String getCurrentUrl() { /* ... */ }
    public BasePage waitForPageLoad() { /* ... */ }
}
```

### Creating a Page Object

```java
public class PlaywrightDocsPage extends BasePage {
    private final Locator getStartedButton;
    private final Locator search;
    private final Locator javaLink;
    
    public PlaywrightDocsPage(Page page) {
        super(page);
        this.getStartedButton = page.getByRole(AriaRole.LINK, 
            new Page.GetByRoleOptions().setName("Get started"));
        // The label includes the platform's shortcut key, so match the prefix only
        this.search = page.getByRole(AriaRole.BUTTON, 
            new Page.GetByRoleOptions().setName(Pattern.compile("^Search")));
        this.javaLink = page.locator("a[href='/java/']");
    }
    
    @Step("Navigate to Playwright documentation")
    public PlaywrightDocsPage navigate() {
        page.navigate("https://playwright.dev/java/");
        return this;
    }
    
    @Step("Click Get Started button")
    public PlaywrightDocsPage clickGetStarted() {
        getStartedButton.click();
        page.waitForURL("**/docs/intro**");
        return this;
    }
    
    @Step("Open search dialog")
    public PlaywrightDocsPage openSearch() {
        search.click();
        return this;
    }
}
```

### Creating a Test Class

```java
@Epic("Web Playwright Java Framework")
@Feature("Playwright Documentation Navigation")
public class PlaywrightDocsNavigationTest extends PlaywrightTest {
    
    @Test(description = "Verify homepage navigation and Get Started button", groups = "smoke")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Homepage Navigation")
    public void testHomePageNavigation() {
        getCurrentPage().ifPresent(page -> {
            PlaywrightDocsPage docsPage = new PlaywrightDocsPage(page);
            
            docsPage.navigate()
                    .clickGetStarted();
            
            assertThat(docsPage.getCurrentUrl())
                    .contains("/docs/intro");
        });
    }
}
```

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
mvn test -Dretry.enabled=true -Dretry.maxRetries=3 -Dretry.delayBetweenRetries=2000
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
mvn test -DtestExecution.parallel=false
mvn test -DtestExecution.threadCount=4
```

## Reports

The framework generates comprehensive test reports using Allure:

### Allure Reports

Each test run writes Allure result data to `target/allure-results/`:

**Generate and View Allure Reports:**

```bash
# Generate Allure report
mvn allure:report

# Serve interactive report
mvn allure:serve
```

**What the report shows:**

- Test result dashboard with pass/fail statistics
- Tests grouped by suite, package and by the `@Epic`, `@Feature` and `@Story` annotations
- Step-by-step execution details from `@Step` methods
- A screenshot attached to each failed test
- Timeline view of test execution
- Severity, description and duration of each test

The framework does not write an Allure environment file, so the report's Environment widget is empty. Trend history is not carried over between runs.

**CI/CD Test Results:**

The test results are automatically published to GitHub Pages after each CI/CD run. You can view them at:
[https://yashwant-das.github.io/qa-java-playwright/](https://yashwant-das.github.io/qa-java-playwright/)

The published report is from the latest run on macOS, with a link to the Windows run. Each deployment replaces the previous one.

### Screenshots

Screenshots are automatically captured and attached to reports:

- **On Failure**: All failed tests automatically capture screenshots
- **Integration**: Screenshots are embedded in Allure reports
- **Storage**: `target/screenshots/`, named after the test with a timestamp
- **Scope**: Full page or viewport only, set by `screenshot.fullPage`

## Logging

The framework provides comprehensive logging with automatic timestamping:

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

- Framework code (io.github.mypixelquest.pja): DEBUG
- TestNG: INFO
- Playwright: INFO
- Root logger: INFO

### Customizing Logging

You can modify logging behavior by editing `src/test/resources/logback.xml`:

- Change log levels
- Modify rotation policies
- Add new appenders
- Customize log patterns

## Advanced Features

### Parallel Test Execution

The framework supports parallel test execution through TestNG:

```yaml
# Configure parallel execution in YAML
testExecution:
  parallel: true
  threadCount: 3
```

```bash
# Or override at runtime
mvn test -DtestExecution.parallel=true -DtestExecution.threadCount=4
```

**Benefits:**

- Faster execution with multiple threads
- Independent browser sessions for each thread
- Automatic test distribution across threads
- Thread-safe browser handling

### Test Data Management

The framework provides comprehensive test data management capabilities:

#### Supported Data Formats

- **YAML**: `playwright-test-data.yaml`
- **JSON**: `test-data.json`
- **CSV**: `test-data.csv`

#### Example: YAML Data

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

#### Using Test Data in Tests

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

#### Dynamic Data Generation

```java
@Test
public void testWithDynamicData() {
    String email = TestDataGenerator.generateEmail();
    String password = TestDataGenerator.generatePassword(12, true);
    String phone = TestDataGenerator.generatePhoneNumber();
    
    // Use generated data in test
}
```

### Test Suite Configuration

The framework includes two TestNG suite files:

#### Main Test Suite (testng.xml)

- Runs all example tests in the framework
- Also runs the framework's own unit tests (`ConfigReaderOverrideTest`), which need no browser
- Uses package scanning to include all example test classes
- Configured with all framework listeners

#### Example Suite (example-suite.xml)

- Focused suite for playwright.dev documentation testing
- Includes only `PlaywrightDocsNavigationTest` class
- Demonstrates how to create targeted test suites
- Useful for running specific test scenarios

**Usage:**

```bash
# Run main suite (all tests)
mvn test

# Run example suite (playwright.dev tests only)
mvn test -DsuiteXmlFile=src/test/resources/suites/example-suite.xml
```

`mvn test` runs `testng.xml` by default. `-DsuiteXmlFile` replaces it with any other suite file.

## Troubleshooting

### Common Issues

**Browser Not Installed:**

- Ensure the browser is installed before running tests
- Install Playwright browsers: `mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install"`

**Java Version Issues:**

- Verify Java version: `java -version` (should be 17+)
- Maven may use a different JDK than `java` on your PATH. Check the "Java version" line of `mvn -version`, and set `JAVA_HOME` if it is not the JDK you expect
- `Fatal error compiling: java.lang.ExceptionInInitializerError: com.sun.tools.javac.code.TypeTag :: UNKNOWN` means Maven is running on a JDK that is too new for the bundled Lombok version. It occurs on JDK 27; JDK 21 compiles cleanly. Point `JAVA_HOME` at a JDK 17 or 21 installation

**Maven Dependency Issues:**

- Clean and rebuild: `mvn clean install`
- Verify Maven version: `mvn -version` (should be 3.6+)

**Configuration Errors:**

- Verify `src/test/resources/config/*.yaml` files exist and are valid YAML
- Check `src/test/resources/data/*` files are valid

**Import Errors:**

- Verify all dependencies are installed: `mvn dependency:resolve`
- Check Java version compatibility

### Getting Help

If tests fail, check:

1. Log files in `logs/` directory for detailed error messages
2. Screenshots in `target/screenshots/` for visual debugging
3. Allure reports for test execution details
4. GitHub Actions logs for CI/CD issues

The framework provides clear error messages to help diagnose issues quickly.

## Framework Architecture

### Core Components

**PlaywrightTest** (`core/PlaywrightTest.java`):

- Base test class with browser setup/teardown
- Provides `getCurrentPage()` method for test access
- Handles browser lifecycle management

**ConfigReader** (`util/ConfigReader.java`):

- Singleton pattern for configuration loading
- Loads YAML configuration files
- Applies `-D` system property overrides to the loaded values
- Provides typed access to configuration settings

**TestDataManager** (`testdata/TestDataManager.java`):

- Centralized test data loading from YAML, JSON, CSV
- Path-based value access using dot notation
- Environment-specific data support

**ScreenshotListener** (`listeners/ScreenshotListener.java`):

- Automatic screenshot capture on test failure
- Allure report integration
- Full-page screenshot support

**RetryAnalyzer** (`listeners/RetryAnalyzer.java`):

- Configurable test retry mechanism
- Tracks retry attempts per test
- Configurable delay between retries

**BasePage** (`pages/BasePage.java`):

- Common page object methods (title, URL, wait for load)
- Playwright page wrapper

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

MIT. See [LICENSE](LICENSE).

---

Built with ❤️ using Java, Playwright, TestNG, and Allure
