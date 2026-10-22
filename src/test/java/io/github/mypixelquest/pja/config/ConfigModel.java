package io.github.mypixelquest.pja.config;

import lombok.Data;

/**
 * Configuration model class that maps to the YAML configuration structure.
 * <p>
 * This class serves as the root configuration model containing all framework settings
 * organized into logical sections: environment, browser, screenshot, test execution, and retry.
 * </p>
 * <p>
 * All nested configuration classes use Lombok's {@code @Data} annotation to automatically
 * generate getters, setters, toString, equals, and hashCode methods.
 * </p>
 */
@Data
public class ConfigModel {
    /**
     * Environment-specific configuration settings
     */
    private EnvironmentConfig environment;

    /**
     * Browser automation configuration settings
     */
    private BrowserConfig browser;

    /**
     * Screenshot capture configuration settings
     */
    private ScreenshotConfig screenshot;

    /**
     * Test execution configuration settings (parallel execution, thread count, etc.)
     */
    private TestExecutionConfig testExecution;

    /**
     * Test retry mechanism configuration settings
     */
    private RetryConfig retry;

    /**
     * Environment-specific configuration settings.
     * <p>
     * Contains environment name and base URL for test execution.
     * </p>
     */
    @Data
    public static class EnvironmentConfig {
        /**
         * Environment name (e.g., "dev", "qa", "prod")
         */
        private String name;

        /**
         * Base URL for the application under test
         */
        private String baseUrl;
    }

    /**
     * Browser automation configuration settings.
     * <p>
     * Controls browser type, headless mode, slow motion, and timeout settings.
     * </p>
     */
    @Data
    public static class BrowserConfig {
        /**
         * Browser type: "chromium", "firefox", or "webkit"
         */
        private String type;

        /**
         * Whether to run browser in headless mode
         */
        private boolean headless;

        /**
         * Slow motion delay in milliseconds between actions (0 = no delay)
         * Useful for debugging and watching test execution
         */
        private int slowMo;

        /**
         * Default timeout in milliseconds for Playwright actions
         */
        private int timeout;
    }

    /**
     * Screenshot capture configuration settings.
     * <p>
     * Controls when and how screenshots are captured during test execution.
     * </p>
     */
    @Data
    public static class ScreenshotConfig {
        /**
         * Whether to automatically capture screenshots on test failure
         */
        private boolean takeOnFailure;

        /**
         * Whether to capture full page screenshots (true) or viewport only (false)
         */
        private boolean fullPage;
    }

    /**
     * Test execution configuration settings.
     * <p>
     * Controls parallel execution and thread count for test runs.
     * </p>
     */
    @Data
    public static class TestExecutionConfig {
        /**
         * Whether to enable parallel test execution
         */
        private boolean parallel;

        /**
         * Number of parallel threads to use when parallel execution is enabled
         * Must be a positive integer (typically 2-4 for optimal performance)
         */
        private int threadCount;
    }

    /**
     * Test retry mechanism configuration settings.
     * <p>
     * Controls automatic retry behavior for failed tests to handle flaky test scenarios.
     * </p>
     */
    @Data
    public static class RetryConfig {
        /**
         * Whether to enable automatic test retry on failure
         */
        private boolean enabled;

        /**
         * Maximum number of retry attempts (0 = no retries, 1+ = number of retries)
         */
        private int maxRetries;

        /**
         * Delay in milliseconds between retry attempts
         * Useful for allowing transient issues to resolve
         */
        private long delayBetweenRetries; // in milliseconds
    }
}
