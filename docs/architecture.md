# Architecture

[Project overview](../README.md) · [Development](development.md)

This is a test-only Maven framework. Reusable infrastructure and examples live
under `src/test`; there is no application artifact or `src/main` layer.

## Namespace

Maven group: `com.qaframework`. Base package: `com.qaframework.playwright`.
The neutral owner-approved convention is `com.qaframework.<project>` with a
short descriptive project segment and existing functional subpackages. The
artifact ID remains `qa-java-playwright`; source paths, TestNG suite/listener
names and the framework logger use the same base package.

## Project structure

```text
qa-java-playwright/
├── pom.xml                       # Dependencies, plugins and Java target
├── mvnw                          # Maven Wrapper for macOS/Linux
├── mvnw.cmd                      # Maven Wrapper for Windows
├── .mvn/wrapper/                 # Maven distribution URL and checksum
├── config/checkstyle.xml         # Java static analysis rules
├── src/test/
│   ├── java/com/qaframework/playwright/
│   │   ├── core/                 # Per-thread browser, per-method context
│   │   ├── config/               # Typed YAML model
│   │   ├── util/                 # Configuration loading and unit tests
│   │   ├── pages/                # Base page and documentation page object
│   │   ├── listeners/            # Suite settings, retries and screenshots
│   │   ├── testdata/             # Fixture loading and generated values
│   │   ├── examples/             # Public-site and data examples
│   │   └── smoke/                # Controlled browser checks
│   └── resources/
│       ├── config/               # dev, qa, prod and local environments
│       ├── data/                 # YAML, JSON and CSV fixtures
│       ├── suites/               # Default, example, unit and smoke suites
│       └── logback.xml           # Console and rotating file logs
├── scripts/                      # Staged Java style adapter
├── docs/                         # Guides, assets and historical plans
├── .github/                      # CI, reports and dependency updates
├── target/                       # Ignored build and test output
├── logs/                         # Ignored execution logs
└── .allure/                      # Ignored local report tooling and history
```

Generated directories appear after running the relevant build or reporting command.

## Core components

Paths below are relative to `src/test/java/com/qaframework/playwright/`.

| Component | Responsibility |
| --- | --- |
| `core/PlaywrightTest.java` | Reuse worker browser handles, create a context/page per method, expose the current page and run teardown |
| `util/ConfigReader.java` | Load the selected YAML environment once per JVM and apply typed overrides to existing paths |
| `listeners/TestConfigurationListener.java` | Apply suite parameters and method-level parallel settings |
| `listeners/RetryListener.java` | Attach `RetryAnalyzer` to test methods in suites that register this listener |
| `listeners/RetryAnalyzer.java` | Read retry enablement, attempt limit and delay from configuration |
| `listeners/ScreenshotListener.java` | Attempt failure screenshots for browser tests when enabled and a page is available |
| `testdata/TestDataManager.java` | Load and cache YAML/JSON/CSV fixtures with environment fallback and dot-path lookups |
| `testdata/TestDataGenerator.java` | Generate sample emails, passwords, numbers, dates and other test values |
| `pages/BasePage.java` | Wrap shared page operations such as title, URL and load-state access |

## Page object model

Page objects hold locators and reusable actions. Tests call those actions and
assert the resulting state. These snippets omit package declarations and imports;
they illustrate the pattern rather than reproduce whole source files.

### Base page

`BasePage` exposes shared operations. Its logging and Allure annotations are
omitted from this excerpt:

```java
public abstract class BasePage {
    protected final Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    public String getTitle() {
        return page.title();
    }

    public String getCurrentUrl() {
        return page.url();
    }

    public BasePage waitForPageLoad() {
        page.waitForLoadState();
        return this;
    }
}
```

### Creating a page object

```java
public class PlaywrightDocsPage extends BasePage {
    private final Locator getStartedButton;
    private final Locator search;

    public PlaywrightDocsPage(Page page) {
        super(page);
        this.getStartedButton = page.getByRole(AriaRole.LINK,
            new Page.GetByRoleOptions().setName("Get started"));
        // The label includes the platform's shortcut key, so match the prefix only
        this.search = page.getByRole(AriaRole.BUTTON,
            new Page.GetByRoleOptions().setName(Pattern.compile("^Search")));
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

### Creating a test class

```java
@Epic("Web Playwright Java Framework")
@Feature("Playwright Documentation Navigation")
public class PlaywrightDocsNavigationTest extends PlaywrightTest {

    @Test(description = "Verify homepage navigation and Get Started button", groups = "smoke")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Homepage Navigation")
    public void testHomePageNavigation() {
        Page page = getCurrentPage().orElseThrow();
        PlaywrightDocsPage docsPage = new PlaywrightDocsPage(page);

        docsPage.navigate().clickGetStarted();

        assertThat(docsPage.getCurrentUrl()).contains("/java/docs/intro");
    }
}
```

The test example uses `orElseThrow()` so a missing page fails the test instead
of silently skipping its assertions. The `smoke` group here selects a public-site
example; the separate `smoke.xml` suite runs controlled browser checks.

## Execution and boundaries

```mermaid
sequenceDiagram
    participant Suite as TestNG suite
    participant Config as Suite / retry listeners
    participant Base as PlaywrightTest
    participant PW as Playwright and browser
    participant Test as Test method / page object
    participant Report as Allure and screenshots
    Suite->>Config: Load YAML, apply overrides, configure suite
    loop Each test method on its worker
        Suite->>Base: BeforeMethod
        Base->>PW: Reuse worker handles, create context and page
        Base->>PW: Set timeout and navigate to configured base URL
        Suite->>Test: Run assertions and page interactions
        Test-->>Report: TestNG results and AspectJ steps
        opt Method fails
            Suite->>Report: Listener attempts capture if enabled and page available
        end
        Suite->>Base: AfterMethod
        Base->>PW: Close page and context
    end
    Suite->>Base: AfterSuite cleanup on teardown thread
```

TestNG loads a suite, then `TestConfigurationListener` loads the selected YAML
configuration and applies parallel settings. `RetryListener` installs retry
behavior. Each `PlaywrightTest` method gets a new context and page; browser and
Playwright handles are held in thread locals. Suite teardown closes handles
visible to its executing thread; this is not a guarantee that another worker’s
thread-local handles have been closed.
Keep Playwright objects on the thread that created them, as described in
[Playwright Java multithreading](https://playwright.dev/java/docs/multithreading).
The thread-local handles support that usage; they do not make shared page objects
or test data safe for concurrent mutation. Setup catches runtime errors from
initial base-URL navigation and logs a warning, so tests must assert the page
state they depend on.

`ConfigReader` applies properties only to existing YAML paths. Its singleton is
initialized once per JVM, so select the environment before starting Maven.
`TestDataManager` loads environment-specific classpath fixtures, falls back to
the common data directory and caches parsed data. Fixture values are examples,
not credentials for real services.

The public examples contact playwright.dev, selenium.dev, example.com and
httpbin.org. Their availability and page structure are outside this repository’s control. The local
smoke suite instead serves HTML through Playwright routing and uses `about:blank`
for initial setup; it exercises a real browser without public-site dependencies.

Allure receives TestNG results and AspectJ `@Step` events. ScreenshotListener
attempts screenshots for failed `PlaywrightTest` methods when enabled and an
active page is available; capture errors are logged. Generated results, screenshots and logs are
ignored; report publication is a separate trusted-branch CI job.

## Build sources of truth

- [POM](../pom.xml): Java target, pinned dependencies, plugins and quality gates.
- [Wrapper properties](../.mvn/wrapper/maven-wrapper.properties): Maven distribution and checksum.
- [Suite files](../src/test/resources/suites/): offline, controlled and public test selections.
- [Workflow](../.github/workflows/playwright.yml): supported runner matrix and report publication.
