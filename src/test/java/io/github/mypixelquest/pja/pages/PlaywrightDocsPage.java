package io.github.mypixelquest.pja.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;
import io.qameta.allure.Step;

import java.util.regex.Pattern;

/**
 * Page Object for the Playwright Documentation Homepage
 */
public class PlaywrightDocsPage extends BasePage {
    // Base URL from config - using Java-specific URL
    private static final String BASE_URL = "https://playwright.dev/java/";

    // Navigation and Header Elements
    private final Locator navbar;
    private final Locator docs;
    private final Locator api;
    private final Locator community;
    private final Locator search;
    private final Locator searchModal;
    private final Locator skipToContent;
    private final Locator getStartedButton;
    private final Locator languageDropdown;

    // Language Links (in dropdown)
    private final Locator javaLink;
    private final Locator pythonLink;
    private final Locator javascriptLink;
    private final Locator typescriptLink;
    private final Locator dotnetLink;

    // Tool Links (in main content)
    private final Locator codegenLink;
    private final Locator playwrightInspectorLink;
    private final Locator traceViewerLink;

    /**
     * Constructor for PlaywrightDocsPage
     *
     * @param page Playwright Page object
     */
    public PlaywrightDocsPage(Page page) {
        super(page);

        // Initialize navigation elements - using more stable selectors
        this.navbar = page.locator("nav[aria-label='Main']");
        this.docs = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Docs"));
        this.api = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("API"));
        this.community = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Community"));
        // The label includes the platform's shortcut key, e.g. "Search (Meta+k)", so match the prefix only
        this.search = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("^Search")));
        this.searchModal = page.locator("div[class*='DocSearch'], div[class*='search'], div[role='dialog']").first();
        this.skipToContent = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Skip to main content"));
        this.getStartedButton = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Get started"));
        this.languageDropdown = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Java"));

        // Initialize language links - using href-based selectors for reliability
        // Note: These are kept for potential future use but navigateToLanguage now uses direct URL navigation
        this.javaLink = page.locator("a[href='/java/']");
        this.pythonLink = page.locator("a[href='/python/']");
        this.javascriptLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("JavaScript"));
        this.typescriptLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("TypeScript"));
        this.dotnetLink = page.locator("a[href='/dotnet/']");

        // Tool links in the docs sidebar; the homepage no longer links to every tool
        Locator sidebar = page.locator("nav[aria-label='Docs sidebar']");
        this.codegenLink = sidebar.locator("a[href='/java/docs/codegen-intro']");
        this.playwrightInspectorLink = sidebar.locator("a[href='/java/docs/debug']");
        this.traceViewerLink = sidebar.locator("a[href='/java/docs/trace-viewer-intro']");
    }

    /**
     * Navigate to the Playwright documentation home page
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Navigate to Playwright documentation")
    public PlaywrightDocsPage navigate() {
        page.navigate(BASE_URL);
        return this;
    }

    /**
     * Click Get Started button
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Click Get Started button")
    public PlaywrightDocsPage clickGetStarted() {
        getStartedButton.click();
        // Wait for navigation to intro page
        page.waitForURL("**/docs/intro**");
        return this;
    }

    /**
     * Open search dialog and wait for it to be visible
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Open search dialog")
    public PlaywrightDocsPage openSearch() {
        search.click();
        // Wait for the search modal to be visible
        searchModal.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
        return this;
    }

    /**
     * Check if search modal is visible
     *
     * @return true if search modal is visible, false otherwise
     */
    @Step("Check if search modal is visible")
    public boolean isSearchModalVisible() {
        return searchModal.isVisible();
    }

    /**
     * Click the language dropdown to show language options
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Click language dropdown")
    public PlaywrightDocsPage clickLanguageDropdown() {
        // Hover to open dropdown (if it's a hoverable dropdown)
        languageDropdown.hover();
        return this;
    }

    /**
     * Navigate to language-specific documentation
     * Note: Since we're already on the Java page, this navigates directly via URL
     *
     * @param language The programming language (java, python, javascript, typescript, dotnet)
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Navigate to {language} documentation")
    public PlaywrightDocsPage navigateToLanguage(String language) {
        String languagePath = language.toLowerCase();
        if (languagePath.equals("dotnet") || languagePath.equals(".net")) {
            languagePath = "dotnet";
        }
        page.navigate("https://playwright.dev/" + languagePath + "/");
        return this;
    }

    /**
     * Navigate to a tool page
     *
     * @param tool The tool name (codegen, inspector, trace-viewer)
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Navigate to {tool}")
    public PlaywrightDocsPage navigateToTool(String tool) {
        page.navigate(BASE_URL + "docs/intro");
        switch (tool.toLowerCase()) {
            case "codegen":
                codegenLink.click();
                // Wait for navigation
                page.waitForURL("**/codegen**");
                break;
            case "inspector":
                playwrightInspectorLink.click();
                // Wait for navigation
                page.waitForURL("**/debug**");
                break;
            case "trace-viewer":
            case "traceviewer":
                traceViewerLink.click();
                // Wait for navigation
                page.waitForURL("**/trace-viewer**");
                break;
            default:
                throw new IllegalArgumentException("Unsupported tool: " + tool);
        }
        return this;
    }

    /**
     * Check if the page is loaded
     *
     * @return true if the page is loaded, false otherwise
     */
    @Step("Check if page is loaded")
    public boolean isLoaded() {
        return navbar.isVisible() && getStartedButton.isVisible();
    }

    /**
     * Get the current page URL
     *
     * @return the current page URL
     */
    @Step("Get current URL")
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Click on Docs link in the navigation
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Click Docs link")
    public PlaywrightDocsPage clickDocs() {
        docs.click();
        return this;
    }

    /**
     * Click on API link in the navigation
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Click API link")
    public PlaywrightDocsPage clickApi() {
        api.click();
        return this;
    }

    /**
     * Click on Community link in the navigation
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Click Community link")
    public PlaywrightDocsPage clickCommunity() {
        community.click();
        return this;
    }

    /**
     * Click on Skip to Content link for accessibility
     *
     * @return PlaywrightDocsPage instance for method chaining
     */
    @Step("Click Skip to Content link")
    public PlaywrightDocsPage clickSkipToContent() {
        skipToContent.click();
        return this;
    }
}

