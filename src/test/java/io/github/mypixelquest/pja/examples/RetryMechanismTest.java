package io.github.mypixelquest.pja.examples;

import io.github.mypixelquest.pja.core.PlaywrightTest;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates test retry mechanism for handling flaky tests
 * Note: Retry configuration is set in YAML config file
 */
@Epic("Framework Capabilities")
@Feature("Retry Mechanism")
public class RetryMechanismTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(RetryMechanismTest.class);
    private static int attemptCount = 0;

    @Test(description = "Test that demonstrates retry on failure")
    @Description("This test may fail initially but will retry based on configuration")
    @Severity(SeverityLevel.NORMAL)
    @Story("Retry Mechanism")
    public void testWithRetryCapability() {
        attemptCount++;
        log.info("Test attempt number: {}", attemptCount);

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");

            // Simulate a potentially flaky condition
            // In real scenarios, this might be network timing, element loading, etc.
            var title = page.title();
            assertThat(title).isNotEmpty();
            assertThat(title).contains("Playwright");

            log.info("Test passed on attempt: {}", attemptCount);
        });
    }

    @Test(description = "Test that always passes - no retry needed")
    @Description("Demonstrates normal test execution without retries")
    @Severity(SeverityLevel.NORMAL)
    @Story("Retry Mechanism")
    public void testWithoutRetry() {
        log.info("Running test that should pass on first attempt");

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://example.com");
            assertThat(page.title()).contains("Example");
            log.info("Test passed without retry");
        });
    }
}
