package io.github.mypixelquest.pja.examples;

import io.github.mypixelquest.pja.core.PlaywrightTest;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates parallel test execution capabilities
 * These tests can run in parallel to showcase thread-safe browser handling
 */
@Epic("Framework Capabilities")
@Feature("Parallel Execution")
public class ParallelExecutionTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(ParallelExecutionTest.class);

    @Test(description = "Parallel test 1 - Navigate to Playwright docs")
    @Description("First parallel test demonstrating thread-safe execution")
    @Severity(SeverityLevel.NORMAL)
    @Story("Parallel Execution")
    public void parallelTest1() {
        log.info("Running parallel test 1 on thread: {}", Thread.currentThread().getId());

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");
            assertThat(page.title()).contains("Playwright");
            log.info("Test 1 completed on thread: {}", Thread.currentThread().getId());
        });
    }

    @Test(description = "Parallel test 2 - Navigate to different page")
    @Description("Second parallel test demonstrating thread-safe execution")
    @Severity(SeverityLevel.NORMAL)
    @Story("Parallel Execution")
    public void parallelTest2() {
        log.info("Running parallel test 2 on thread: {}", Thread.currentThread().getId());

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://example.com");
            assertThat(page.title()).contains("Example");
            log.info("Test 2 completed on thread: {}", Thread.currentThread().getId());
        });
    }

    @Test(description = "Parallel test 3 - Verify URL navigation")
    @Description("Third parallel test demonstrating thread-safe execution")
    @Severity(SeverityLevel.NORMAL)
    @Story("Parallel Execution")
    public void parallelTest3() {
        log.info("Running parallel test 3 on thread: {}", Thread.currentThread().getId());

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://httpbin.org/html");
            assertThat(page.url()).contains("httpbin.org");
            log.info("Test 3 completed on thread: {}", Thread.currentThread().getId());
        });
    }
}
