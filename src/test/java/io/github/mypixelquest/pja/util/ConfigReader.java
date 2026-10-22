package io.github.mypixelquest.pja.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.mypixelquest.pja.config.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Singleton utility class for loading and accessing test configuration
 */
public class ConfigReader {
    private static final Logger log = LoggerFactory.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private ConfigModel config;

    private ConfigReader() {
        loadConfig();
    }

    /**
     * Get the singleton instance of ConfigReader
     *
     * @return ConfigReader instance
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Load configuration from the appropriate YAML file
     */
    private void loadConfig() {
        String environment = System.getProperty("environment", "qa");
        String configFile = String.format("/config/%s.yaml", environment);

        log.info("Loading configuration from {}", configFile);

        try (InputStream inputStream = getClass().getResourceAsStream(configFile)) {
            if (inputStream == null) {
                throw new IOException("Configuration file not found: " + configFile);
            }

            config = parse(inputStream, System.getProperties());
            log.info("Configuration loaded successfully for environment: {}", environment);
        } catch (IOException e) {
            log.error("Failed to load configuration", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    /**
     * Parse a YAML configuration stream and apply property overrides to it.
     * A setting is overridden by a property named after its YAML path,
     * e.g. {@code browser.type} or {@code retry.maxRetries}.
     *
     * @param yaml      YAML configuration content
     * @param overrides Properties that override YAML values
     * @return ConfigModel containing the resulting settings
     */
    static ConfigModel parse(InputStream yaml, Properties overrides) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode root = mapper.readTree(yaml);
        applyOverrides(root, "", overrides);
        return mapper.treeToValue(root, ConfigModel.class);
    }

    /**
     * Replace each YAML value that has an override property set for its path
     */
    private static void applyOverrides(JsonNode node, String pathPrefix, Properties overrides) {
        if (!(node instanceof ObjectNode section)) {
            return;
        }

        List<String> names = new ArrayList<>();
        section.fieldNames().forEachRemaining(names::add);

        for (String name : names) {
            String path = pathPrefix + name;
            JsonNode value = section.get(name);

            if (value.isObject()) {
                applyOverrides(value, path + ".", overrides);
                continue;
            }

            String override = overrides.getProperty(path);
            // -Dheadless is kept as a short form of -Dbrowser.headless
            if (override == null && path.equals("browser.headless")) {
                override = overrides.getProperty("headless");
            }
            if (override != null) {
                log.info("Overriding {} from command line: {}", path, override);
                section.put(name, override);
            }
        }
    }

    /**
     * Get the loaded configuration
     *
     * @return ConfigModel containing all configuration settings
     */
    public ConfigModel getConfig() {
        return config;
    }

    /**
     * Get base URL from configuration
     *
     * @return Base URL string
     */
    public String getBaseUrl() {
        return Optional.ofNullable(config.getEnvironment().getBaseUrl())
                .orElseThrow(() -> new RuntimeException("Base URL not configured"));
    }

    /**
     * Get browser type from configuration
     *
     * @return Browser type string (chromium, firefox, webkit)
     */
    public String getBrowserType() {
        return Optional.ofNullable(config.getBrowser().getType())
                .orElse("chromium");
    }

    /**
     * Check if browser should run in headless mode
     *
     * @return True if headless, false otherwise
     */
    public boolean isHeadless() {
        return config.getBrowser().isHeadless();
    }

    /**
     * Get default timeout for Playwright actions
     *
     * @return Timeout in milliseconds
     */
    public int getTimeout() {
        return config.getBrowser().getTimeout();
    }

    /**
     * Check if screenshots should be taken on test failure
     *
     * @return True if screenshots should be taken on failure, false otherwise
     */
    public boolean shouldTakeScreenshotOnFailure() {
        var screenshotConfig = config.getScreenshot();
        return screenshotConfig != null && screenshotConfig.isTakeOnFailure();
    }

    /**
     * Check if full page screenshots should be captured
     *
     * @return True if full page screenshots should be captured, false for viewport only
     */
    public boolean shouldCaptureFullPageScreenshot() {
        var screenshotConfig = config.getScreenshot();
        return screenshotConfig != null && screenshotConfig.isFullPage();
    }
}
