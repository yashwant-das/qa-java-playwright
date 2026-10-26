package com.qaframework.playwright.examples;

import com.qaframework.playwright.core.PlaywrightTest;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates screenshot capture and Allure reporting integration
 */
@Epic("Framework Capabilities")
@Feature("Screenshot and Reporting")
public class ScreenshotAndReportingTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(ScreenshotAndReportingTest.class);

    @Test(description = "Successful test with screenshot attachment")
    @Description("Demonstrates screenshot capture on successful test")
    @Severity(SeverityLevel.NORMAL)
    @Story("Screenshot Capture")
    @Attachment(value = "Page Screenshot", type = "image/png")
    public void testSuccessfulScreenshot() {
        log.info("Running test with screenshot capture");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Take screenshot manually
            byte[] screenshot = page.screenshot();
            Allure.addAttachment("Success Screenshot", "image/png",
                    new java.io.ByteArrayInputStream(screenshot), "png");

            assertThat(page.title()).contains("Playwright");
            log.info("Screenshot captured successfully");
        });
    }

    @Test(description = "Test with step annotations for reporting")
    @Description("Demonstrates Allure step annotations for detailed reporting")
    @Severity(SeverityLevel.NORMAL)
    @Story("Allure Reporting")
    public void testWithSteps() {
        log.info("Running test with step annotations");

        getCurrentPage().ifPresent(page -> {
            navigateToPage(page, "https://playwright.dev");
            verifyPageTitle(page, "Playwright");
            verifyPageContent(page);
        });
    }

    @Step("Navigate to {url}")
    private void navigateToPage(com.microsoft.playwright.Page page, String url) {
        log.info("Navigating to: {}", url);
        page.navigate(url);
    }

    @Step("Verify page title contains {expectedTitle}")
    private void verifyPageTitle(com.microsoft.playwright.Page page, String expectedTitle) {
        log.info("Verifying page title");
        assertThat(page.title()).contains(expectedTitle);
    }

    @Step("Verify page content is loaded")
    private void verifyPageContent(com.microsoft.playwright.Page page) {
        log.info("Verifying page content");
        assertThat(page.content()).isNotEmpty();
    }
}
