package io.github.mypixelquest.pja.examples;

import io.github.mypixelquest.pja.core.PlaywrightTest;
import io.github.mypixelquest.pja.pages.PlaywrightDocsPage;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for Playwright.dev website using PlaywrightDocsPage
 * Demonstrates basic navigation and interaction patterns
 */
@Epic("Web Playwright Java Framework")
@Feature("Playwright Documentation Navigation")
public class PlaywrightDocsNavigationTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(PlaywrightDocsNavigationTest.class);

    /**
     * Test 1: Verify basic navigation to the homepage and Get Started functionality
     */
    @Test(description = "Verify homepage navigation and Get Started button", groups = "smoke")
    @Description("Navigates to the homepage and clicks the Get Started button")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Homepage Navigation")
    public void testHomePageNavigation() {
        log.info("Running test: Homepage navigation");

        getCurrentPage().ifPresent(page -> {
            PlaywrightDocsPage docsPage = new PlaywrightDocsPage(page);

            // Navigate to homepage
            docsPage.navigate();

            // Verify page loaded successfully
            assertThat(docsPage.isLoaded())
                    .as("Homepage should be loaded")
                    .isTrue();

            // Verify we're on the Java documentation page
            assertThat(docsPage.getCurrentUrl())
                    .as("Should be on Java documentation homepage")
                    .contains("/java/");

            // Click Get Started button (this now waits for navigation)
            docsPage.clickGetStarted();

            // Wait for page to fully load
            page.waitForLoadState();

            // Verify URL is exactly /java/docs/intro (Java-specific path)
            assertThat(docsPage.getCurrentUrl())
                    .as("URL should be /java/docs/intro after clicking Get Started")
                    .contains("/java/docs/intro");

            // Wait for the intro page content to load (check for common intro page elements)
            // The intro page should have content about getting started/installation
            // Wait for article content or main content area to be visible
            page.waitForSelector("article, main article, .markdown");

            // Get the visible text content from the page body
            var bodyLocator = page.locator("body");
            var pageText = bodyLocator.textContent();

            // Verify we're on the intro page by checking for common intro page text
            // The page should contain text about installation or getting started
            assertThat(pageText)
                    .as("Page text should not be null")
                    .isNotNull();

            // Check if page contains installation-related keywords (case-insensitive)
            String lowerPageText = pageText.toLowerCase();
            boolean containsInstallationContent = lowerPageText.contains("installation") ||
                                                  lowerPageText.contains("getting started") ||
                                                  lowerPageText.contains("maven") ||
                                                  lowerPageText.contains("gradle");

            assertThat(containsInstallationContent)
                    .as("Page should contain installation or getting started content")
                    .isTrue();
        });
    }

    /**
     * Test 2: Verify Java documentation navigation
     */
    @Test(description = "Navigate to Java documentation")
    @Description("Navigates to Java-specific documentation and verifies the URL")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Language Documentation")
    public void testJavaDocumentation() {
        log.info("Running test: Java documentation navigation");

        getCurrentPage().ifPresent(page -> {
            PlaywrightDocsPage docsPage = new PlaywrightDocsPage(page);

            // Navigate to homepage
            docsPage.navigate();

            // Navigate to Java documentation (should already be on Java page)
            docsPage.navigateToLanguage("java");

            // Verify URL is exactly /java/ (exact path for Java docs homepage)
            assertThat(docsPage.getCurrentUrl())
                    .as("URL should be the Java documentation homepage")
                    .isEqualTo("https://playwright.dev/java/");

            // Verify page title contains "Playwright Java"
            assertThat(page.title())
                    .as("Page title should indicate Java documentation")
                    .containsIgnoringCase("Playwright Java");
        });
    }

    /**
     * Test 3: Verify search functionality
     */
    @Test(description = "Verify search functionality")
    @Description("Opens search dialog and verifies it's visible")
    @Severity(SeverityLevel.NORMAL)
    @Story("Search Functionality")
    public void testSearch() {
        log.info("Running test: Search functionality");

        getCurrentPage().ifPresent(page -> {
            PlaywrightDocsPage docsPage = new PlaywrightDocsPage(page);

            // Navigate to homepage
            docsPage.navigate();

            // Open search dialog
            docsPage.openSearch();

            // Wait a moment for the modal to appear
            page.waitForTimeout(500);

            // Verify search dialog is visible using the page object method
            assertThat(docsPage.isSearchModalVisible())
                    .as("Search modal should be visible after clicking search button")
                    .isTrue();

            // Verify search input is focused or visible
            var searchInput = page.locator("input[type='search'], input[placeholder*='Search'], input[aria-label*='Search']").first();
            assertThat(searchInput.isVisible())
                    .as("Search input field should be visible")
                    .isTrue();
        });
    }

    /**
     * Test 4: Verify Playwright tools navigation
     */
    @Test(description = "Navigate to Playwright tools")
    @Description("Navigates to various Playwright tools (Codegen, Inspector, Trace Viewer)")
    @Severity(SeverityLevel.NORMAL)
    @Story("Tools Navigation")
    public void testToolsNavigation() {
        log.info("Running test: Tools navigation");

        getCurrentPage().ifPresent(page -> {
            PlaywrightDocsPage docsPage = new PlaywrightDocsPage(page);

            // Navigate to homepage
            docsPage.navigate();

            // Navigate to Codegen tool
            docsPage.navigateToTool("codegen");

            // Wait for page to fully load
            page.waitForLoadState();

            // Verify URL contains /codegen (Java-specific path)
            assertThat(docsPage.getCurrentUrl())
                    .as("URL should contain /codegen or /codegen-intro")
                    .matches(".*/(codegen|codegen-intro).*");

            // Verify page content indicates Codegen by checking for specific text content
            // Wait for the main heading to ensure page is fully rendered
            page.waitForSelector("h1");
            var pageContent = page.content().toLowerCase();
            assertThat(pageContent)
                    .as("Page content should mention codegen or generator")
                    .containsAnyOf("codegen", "test generator", "generator");

            // Navigate back to homepage
            docsPage.navigate();

            // Verify we're back on the homepage
            assertThat(docsPage.getCurrentUrl())
                    .as("Should be back on Java homepage")
                    .isEqualTo("https://playwright.dev/java/");

            // Navigate to Trace Viewer
            docsPage.navigateToTool("trace-viewer");

            // Wait for page to fully load
            page.waitForLoadState();

            // Verify URL contains trace-viewer (Java-specific path)
            assertThat(docsPage.getCurrentUrl())
                    .as("URL should contain trace-viewer")
                    .matches(".*trace-viewer.*");

            // Verify page content indicates Trace Viewer by checking for specific text content
            // Wait for the main heading to ensure page is fully rendered
            page.waitForSelector("h1");
            var traceViewerContent = page.content().toLowerCase();
            assertThat(traceViewerContent)
                    .as("Page content should mention trace viewer")
                    .containsAnyOf("trace", "viewer", "trace viewer");
        });
    }
}
