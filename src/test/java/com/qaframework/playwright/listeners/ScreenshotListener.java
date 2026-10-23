package com.qaframework.playwright.listeners;

import com.microsoft.playwright.Page;
import com.qaframework.playwright.core.PlaywrightTest;
import com.qaframework.playwright.util.ConfigReader;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * TestNG listener that automatically captures screenshots on test failure.
 * <p>
 * Screenshot behavior is controlled by the ScreenshotConfig in the YAML configuration:
 * - takeOnFailure: Whether to capture screenshots on failure
 * - fullPage: Whether to capture full page or viewport only
 * </p>
 */
@Slf4j
public class ScreenshotListener implements ITestListener {
    private final ConfigReader configReader = ConfigReader.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        // No action needed
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.debug("Test failed: {}", result.getName());

        // Check if screenshot on failure is enabled in configuration
        var screenshotConfig = configReader.getConfig().getScreenshot();
        if (screenshotConfig != null && screenshotConfig.isTakeOnFailure()) {
            takeScreenshot(result);
        } else {
            log.debug("Screenshot on failure is disabled in configuration");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.debug("Test skipped: {}", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // No action needed
    }

    private void takeScreenshot(ITestResult result) {
        try {
            Object instance = result.getInstance();
            if (!(instance instanceof PlaywrightTest test)) {
                log.warn("Test instance is not a PlaywrightTest, cannot take screenshot");
                return;
            }

            test.getCurrentPage().ifPresentOrElse(
                    page -> captureAndAttachScreenshot(page, result),
                    () -> log.warn("No active page found to capture screenshot")
            );
        } catch (Exception e) {
            log.error("Failed to take screenshot", e);
        }
    }

    private void captureAndAttachScreenshot(Page page, ITestResult result) {
        try {
            String testName = result.getName();
            log.info("Taking screenshot for test: {}", testName);

            // Get screenshot configuration
            var screenshotConfig = configReader.getConfig().getScreenshot();
            boolean fullPage = screenshotConfig != null && screenshotConfig.isFullPage();

            // Get build directory from system property (Maven sets project.build.directory)
            // Fallback to "target" if not set
            String buildDir = System.getProperty("project.build.directory", "target");
            Path screenshotsDir = Paths.get(buildDir, "screenshots");
            Files.createDirectories(screenshotsDir);

            // Take screenshot with configuration-based settings
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions()
                    .setPath(screenshotsDir.resolve(testName + "_" + System.currentTimeMillis() + ".png"))
                    .setFullPage(fullPage));

            // Attach to Allure report
            Allure.addAttachment(
                    testName + "_failure",
                    "image/png",
                    new ByteArrayInputStream(screenshot),
                    "png"
            );

            log.debug("Screenshot captured (fullPage: {})", fullPage);
        } catch (Exception e) {
            log.error("Failed to capture or attach screenshot for test: {}", result.getName(), e);
        }
    }
}
