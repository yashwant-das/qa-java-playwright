package com.qaframework.playwright.util;

import com.qaframework.playwright.config.ConfigModel;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests that -D system properties override the values loaded from a YAML config file
 */
public class ConfigReaderOverrideTest {
    private static final String YAML = """
            environment:
              name: fixture
              baseUrl: https://example.test/
            browser:
              type: chromium
              headless: false
              slowMo: 0
              timeout: 30000
            screenshot:
              takeOnFailure: true
              fullPage: true
            testExecution:
              parallel: true
              threadCount: 3
            retry:
              enabled: true
              maxRetries: 2
              delayBetweenRetries: 1000
            """;

    private static ConfigModel parse(String... keyValues) throws IOException {
        Properties overrides = new Properties();
        for (int i = 0; i < keyValues.length; i += 2) {
            overrides.setProperty(keyValues[i], keyValues[i + 1]);
        }
        return ConfigReader.parse(new ByteArrayInputStream(YAML.getBytes(StandardCharsets.UTF_8)), overrides);
    }

    @Test(description = "Without overrides the YAML values are used")
    public void yamlValuesAreUsedWhenNoOverrideIsSet() throws IOException {
        ConfigModel config = parse();

        assertThat(config.getBrowser().getType()).isEqualTo("chromium");
        assertThat(config.getBrowser().isHeadless()).isFalse();
        assertThat(config.getTestExecution().isParallel()).isTrue();
        assertThat(config.getTestExecution().getThreadCount()).isEqualTo(3);
        assertThat(config.getRetry().isEnabled()).isTrue();
        assertThat(config.getRetry().getMaxRetries()).isEqualTo(2);
        assertThat(config.getRetry().getDelayBetweenRetries()).isEqualTo(1000L);
    }

    @Test(description = "-Dbrowser.type replaces the YAML browser type")
    public void browserTypeOverrideReplacesYamlValue() throws IOException {
        ConfigModel config = parse("browser.type", "firefox");

        assertThat(config.getBrowser().getType()).isEqualTo("firefox");
    }

    @Test(description = "-Dbrowser.headless replaces the YAML headless flag")
    public void browserHeadlessOverrideReplacesYamlValue() throws IOException {
        ConfigModel config = parse("browser.headless", "true");

        assertThat(config.getBrowser().isHeadless()).isTrue();
    }

    @Test(description = "-Dheadless is still accepted as a short form of -Dbrowser.headless")
    public void headlessShortFormOverridesYamlValue() throws IOException {
        ConfigModel config = parse("headless", "true");

        assertThat(config.getBrowser().isHeadless()).isTrue();
    }

    @Test(description = "-Dbrowser.headless wins when both it and -Dheadless are set")
    public void browserHeadlessTakesPrecedenceOverShortForm() throws IOException {
        ConfigModel config = parse("headless", "true", "browser.headless", "false");

        assertThat(config.getBrowser().isHeadless()).isFalse();
    }

    @Test(description = "-DtestExecution.parallel and -DtestExecution.threadCount replace the YAML values")
    public void testExecutionOverridesReplaceYamlValues() throws IOException {
        ConfigModel config = parse("testExecution.parallel", "false", "testExecution.threadCount", "7");

        assertThat(config.getTestExecution().isParallel()).isFalse();
        assertThat(config.getTestExecution().getThreadCount()).isEqualTo(7);
    }

    @Test(description = "-Dretry.* properties replace the YAML retry settings")
    public void retryOverridesReplaceYamlValues() throws IOException {
        ConfigModel config = parse("retry.enabled", "false", "retry.maxRetries", "5",
                "retry.delayBetweenRetries", "250");

        assertThat(config.getRetry().isEnabled()).isFalse();
        assertThat(config.getRetry().getMaxRetries()).isEqualTo(5);
        assertThat(config.getRetry().getDelayBetweenRetries()).isEqualTo(250L);
    }

    @Test(description = "Settings outside the browser, testExecution and retry sections can be overridden too")
    public void anyYamlSettingCanBeOverriddenByItsPath() throws IOException {
        ConfigModel config = parse("environment.baseUrl", "https://override.test/", "browser.slowMo", "250",
                "screenshot.fullPage", "false");

        assertThat(config.getEnvironment().getBaseUrl()).isEqualTo("https://override.test/");
        assertThat(config.getBrowser().getSlowMo()).isEqualTo(250);
        assertThat(config.getScreenshot().isFullPage()).isFalse();
    }

    @Test(description = "An override leaves the other YAML values untouched")
    public void overrideDoesNotChangeOtherValues() throws IOException {
        ConfigModel config = parse("browser.type", "webkit");

        assertThat(config.getBrowser().getTimeout()).isEqualTo(30000);
        assertThat(config.getEnvironment().getBaseUrl()).isEqualTo("https://example.test/");
        assertThat(config.getRetry().getMaxRetries()).isEqualTo(2);
    }

    @Test(description = "A non-numeric value for a numeric setting fails instead of being ignored")
    public void invalidNumericOverrideIsRejected() {
        assertThatThrownBy(() -> parse("testExecution.threadCount", "many"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("threadCount");
    }
}
