package io.github.mypixelquest.pja.core;

import com.microsoft.playwright.*;
import io.github.mypixelquest.pja.listeners.ScreenshotListener;
import io.github.mypixelquest.pja.util.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.Optional;

/**
 * Base test class for all Playwright tests
 * Handles thread-safe browser initialization and cleanup
 */
@Listeners({ScreenshotListener.class})
public class PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(PlaywrightTest.class);
    private static final ConfigReader configReader = ConfigReader.getInstance();

    // ThreadLocal variables for parallel execution safety
    private static final ThreadLocal<Playwright> playwrightThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Browser> browserThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> contextThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<Page> pageThreadLocal = new ThreadLocal<>();

    /**
     * Setup for the test suite - called once before any tests run
     */
    @BeforeSuite(alwaysRun = true)
    public void setupPlaywright() {
        log.info("Starting Playwright setup for test suite");
    }

    /**
     * Setup for each test method - creates context, page and navigates to base URL
     */
    @BeforeMethod(alwaysRun = true)
    public void setupBrowserContext() {
        log.info("Setting up browser for test");

        if (playwrightThreadLocal.get() == null) {
            log.debug("Initializing Playwright for thread: {}", Thread.currentThread().getId());
            Playwright playwright = Playwright.create();
            playwrightThreadLocal.set(playwright);
        }

        if (browserThreadLocal.get() == null) {
            log.debug("Creating {} browser for thread: {}",
                    configReader.getBrowserType(), Thread.currentThread().getId());

            Browser browser;
            BrowserType browserType = getBrowserType(configReader.getBrowserType());
            BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                    .setHeadless(configReader.isHeadless())
                    .setSlowMo(configReader.getConfig().getBrowser().getSlowMo());

            browser = browserType.launch(launchOptions);
            browserThreadLocal.set(browser);
        }

        // Create a new browser context for each test (for isolation)
        BrowserContext context = browserThreadLocal.get().newContext();
        contextThreadLocal.set(context);

        // Create a new page and set default timeout
        Page page = context.newPage();
        page.setDefaultTimeout(configReader.getTimeout());
        pageThreadLocal.set(page);

        // Navigate to the base URL if configured
        try {
            String baseUrl = configReader.getBaseUrl();
            if (baseUrl != null && !baseUrl.isEmpty()) {
                log.info("Navigating to base URL: {}", baseUrl);
                page.navigate(baseUrl);
            }
        } catch (RuntimeException e) {
            log.warn("Base URL not configured, skipping initial navigation");
        }
    }

    /**
     * Cleanup after each test method - close page and context
     */
    @AfterMethod(alwaysRun = true)
    public void cleanupBrowserContext() {
        log.info("Cleaning up browser context after test");

        if (pageThreadLocal.get() != null) {
            pageThreadLocal.get().close();
            pageThreadLocal.remove();
        }

        if (contextThreadLocal.get() != null) {
            contextThreadLocal.get().close();
            contextThreadLocal.remove();
        }
    }

    /**
     * Cleanup after the test suite - close all browsers and playwright
     */
    @AfterSuite(alwaysRun = true)
    public void cleanupPlaywright() {
        log.info("Cleaning up Playwright resources");

        if (browserThreadLocal.get() != null) {
            browserThreadLocal.get().close();
            browserThreadLocal.remove();
        }

        if (playwrightThreadLocal.get() != null) {
            playwrightThreadLocal.get().close();
            playwrightThreadLocal.remove();
        }
    }

    /**
     * Get the current thread's Page object
     * Used by tests and listeners (e.g., for screenshots)
     *
     * @return Optional containing the current page or empty if none exists
     */
    public Optional<Page> getCurrentPage() {
        return Optional.ofNullable(pageThreadLocal.get());
    }

    /**
     * Get the current thread's BrowserContext object
     *
     * @return Optional containing the current context or empty if none exists
     */
    public Optional<BrowserContext> getCurrentContext() {
        return Optional.ofNullable(contextThreadLocal.get());
    }

    /**
     * Get browser type based on configuration
     *
     * @param browserType String name of browser type
     * @return BrowserType instance
     */
    private BrowserType getBrowserType(String browserType) {
        Playwright playwright = playwrightThreadLocal.get();
        return switch (browserType.toLowerCase()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            default -> playwright.chromium();
        };
    }
}
