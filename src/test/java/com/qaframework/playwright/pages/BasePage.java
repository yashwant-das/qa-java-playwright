package com.qaframework.playwright.pages;

import com.microsoft.playwright.Page;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all Page Objects
 */
public abstract class BasePage {
    protected final Page page;
    protected final Logger log;

    /**
     * Constructor for BasePage
     *
     * @param page Playwright Page object
     */
    public BasePage(Page page) {
        this.page = page;
        this.log = LoggerFactory.getLogger(getClass());
        log.debug("Initializing {}", getClass().getSimpleName());
    }

    /**
     * Get the page title
     *
     * @return Page title
     */
    @Step("Get page title")
    public String getTitle() {
        String title = page.title();
        log.debug("Page title: {}", title);
        return title;
    }

    /**
     * Get the current URL
     *
     * @return Current URL
     */
    @Step("Get current URL")
    public String getCurrentUrl() {
        String url = page.url();
        log.debug("Current URL: {}", url);
        return url;
    }

    /**
     * Wait for page load state to be complete
     *
     * @return The page object for method chaining
     */
    @Step("Wait for page to load completely")
    public BasePage waitForPageLoad() {
        log.debug("Waiting for page to load completely");
        page.waitForLoadState();
        return this;
    }
}
