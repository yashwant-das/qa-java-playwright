package com.qaframework.playwright.examples;

import com.qaframework.playwright.core.PlaywrightTest;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates cross-browser testing capabilities
 * Note: Browser type is configured via YAML config file
 */
@Epic("Framework Capabilities")
@Feature("Multi-Browser Testing")
public class MultiBrowserTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(MultiBrowserTest.class);

    @Test(description = "Test navigation across different browsers")
    @Description("Demonstrates that the same test can run on Chromium, Firefox, or WebKit")
    @Severity(SeverityLevel.NORMAL)
    @Story("Multi-Browser Testing")
    public void testCrossBrowserNavigation() {
        log.info("Running cross-browser test");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Verify page loaded correctly regardless of browser
            assertThat(page.title()).contains("Playwright");
            assertThat(page.url()).contains("playwright.dev");

            log.info("Test passed on configured browser");
        });
    }

    @Test(description = "Test element interaction across browsers")
    @Description("Demonstrates element interaction works consistently across browsers")
    @Severity(SeverityLevel.NORMAL)
    @Story("Multi-Browser Testing")
    public void testCrossBrowserElementInteraction() {
        log.info("Running cross-browser element interaction test");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Interact with elements - should work on all browsers
            var getStartedLink = page.locator("a.getStarted_Sjon");
            assertThat(getStartedLink.isVisible()).isTrue();

            // Click and verify navigation
            getStartedLink.click();
            page.waitForLoadState();

            assertThat(page.url()).contains("docs");
            log.info("Element interaction successful on configured browser");
        });
    }
}
