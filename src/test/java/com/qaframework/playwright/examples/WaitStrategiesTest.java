package com.qaframework.playwright.examples;

import com.qaframework.playwright.core.PlaywrightTest;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates different wait strategies and Playwright's auto-waiting capabilities
 */
@Epic("Framework Capabilities")
@Feature("Wait Strategies")
public class WaitStrategiesTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(WaitStrategiesTest.class);

    @Test(description = "Demonstrate auto-waiting for element visibility")
    @Description("Shows Playwright's automatic waiting for elements")
    @Severity(SeverityLevel.NORMAL)
    @Story("Wait Strategies")
    public void testAutoWaitForVisibility() {
        log.info("Testing auto-wait for element visibility");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Playwright automatically waits for element to be visible
            var getStartedLink = page.locator("a.getStarted_Sjon");
            assertThat(getStartedLink.isVisible()).isTrue();

            log.info("Element found and visible after auto-wait");
        });
    }

    @Test(description = "Demonstrate explicit wait for navigation")
    @Description("Shows explicit wait for page navigation")
    @Severity(SeverityLevel.NORMAL)
    @Story("Wait Strategies")
    public void testExplicitWaitForNavigation() {
        log.info("Testing explicit wait for navigation");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Wait for navigation to complete
            page.waitForLoadState();

            assertThat(page.url()).contains("playwright.dev");
            log.info("Navigation completed");
        });
    }

    @Test(description = "Demonstrate wait for selector state")
    @Description("Shows waiting for specific selector states")
    @Severity(SeverityLevel.NORMAL)
    @Story("Wait Strategies")
    public void testWaitForSelectorState() {
        log.info("Testing wait for selector state");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Wait for element to be attached and visible
            var navbar = page.locator("nav.navbar");
            navbar.waitFor();

            assertThat(navbar.isVisible()).isTrue();
            log.info("Selector state verified");
        });
    }

    @Test(description = "Demonstrate wait for timeout")
    @Description("Shows custom timeout configuration")
    @Severity(SeverityLevel.NORMAL)
    @Story("Wait Strategies")
    public void testCustomTimeout() {
        log.info("Testing custom timeout configuration");

        getCurrentPage().ifPresent(page -> {
            // Set a custom timeout for this page
            page.setDefaultTimeout(10000);

            page.navigate("https://playwright.dev");

            // This will use the custom timeout
            var element = page.locator("nav.navbar");
            element.waitFor();

            assertThat(element.isVisible()).isTrue();
            log.info("Custom timeout applied successfully");
        });
    }
}
