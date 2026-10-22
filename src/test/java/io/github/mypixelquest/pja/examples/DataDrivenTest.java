package io.github.mypixelquest.pja.examples;

import io.github.mypixelquest.pja.core.PlaywrightTest;
import io.github.mypixelquest.pja.testdata.TestDataManager;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates data-driven testing capabilities using YAML, JSON, and CSV data sources
 */
@Epic("Framework Capabilities")
@Feature("Data-Driven Testing")
public class DataDrivenTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(DataDrivenTest.class);
    private TestDataManager dataManager;

    @BeforeClass
    public void setupDataManager() {
        dataManager = new TestDataManager("qa");
    }

    @Test(description = "Data-driven test using YAML data")
    @Description("Loads test data from YAML and uses it in test")
    @Severity(SeverityLevel.NORMAL)
    @Story("Data-Driven Testing")
    public void testWithYamlData() {
        log.info("Running data-driven test with YAML data");

        Map<String, Object> data = dataManager.loadYamlData("playwright-test-data.yaml");
        String adminUsername = (String) dataManager.getValue(data, "users.admin.username");
        String adminRole = (String) dataManager.getValue(data, "users.admin.role");

        log.info("Testing with user: {} (Role: {})", adminUsername, adminRole);

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev");
            assertThat(page.title()).isNotEmpty();
        });

        assertThat(adminUsername).isEqualTo("admin@playwright-test.com");
        assertThat(adminRole).isEqualTo("ADMIN");
    }

    @Test(description = "Data-driven test using JSON data")
    @Description("Loads test data from JSON and uses it in test")
    @Severity(SeverityLevel.NORMAL)
    @Story("Data-Driven Testing")
    @SuppressWarnings("unchecked")
    public void testWithJsonData() {
        log.info("Running data-driven test with JSON data");

        Map<String, Object> data = dataManager.loadJsonData("test-data.json");
        List<Map<String, Object>> testScenarios = (List<Map<String, Object>>) data.get("testScenarios");

        assertThat(testScenarios).isNotEmpty();

        Map<String, Object> firstScenario = testScenarios.get(0);
        String scenarioId = (String) firstScenario.get("id");
        String scenarioName = (String) firstScenario.get("name");

        log.info("Testing with scenario: {} - {}", scenarioId, scenarioName);

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev/java/");
            assertThat(page.url()).contains("playwright");
        });

        assertThat(scenarioId).isEqualTo("SCENARIO-001");
        assertThat(scenarioName).isEqualTo("User Login Flow");
    }

    @Test(description = "Data-driven test using CSV data")
    @Description("Loads test data from CSV and uses it in test")
    @Severity(SeverityLevel.NORMAL)
    @Story("Data-Driven Testing")
    @SuppressWarnings("unchecked")
    public void testWithCsvData() {
        log.info("Running data-driven test with CSV data");

        Map<String, Object> data = dataManager.loadCsvData("test-data.csv");
        List<Map<String, Object>> browsers = (List<Map<String, Object>>) data.get("data");

        assertThat(browsers).isNotEmpty();

        Map<String, Object> firstBrowser = browsers.get(0);
        String browserId = (String) firstBrowser.get("id");
        String browserName = (String) firstBrowser.get("name");
        String browserType = (String) firstBrowser.get("type");
        String version = (String) firstBrowser.get("version");

        log.info("Testing with browser: {} - {} (Type: {}, Version: {})", browserId, browserName, browserType, version);

        getCurrentPage().ifPresent(page -> {
            page.navigate("https://playwright.dev/java/");
            assertThat(page.title()).isNotEmpty();
        });

        assertThat(browserId).isEqualTo("BROWSER-001");
        assertThat(browserName).isEqualTo("Chromium");
        assertThat(browserType).isEqualTo("Browser");
        assertThat(version).isEqualTo("1.48.0");
    }
}
