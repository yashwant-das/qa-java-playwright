package io.github.mypixelquest.pja.examples;

import io.github.mypixelquest.pja.testdata.TestDataGenerator;
import io.github.mypixelquest.pja.testdata.TestDataManager;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates the test data management capabilities of the framework
 */
@Slf4j
public class TestDataManagementTest {
    private final TestDataManager dataManager;

    public TestDataManagementTest() {
        this.dataManager = new TestDataManager("qa");
    }

    @Test(description = "Demonstrate YAML data loading")
    @Description("Loads and uses data from YAML files")
    @Severity(SeverityLevel.NORMAL)
    @Story("Test Data Management")
    @SuppressWarnings("unchecked")
    public void testYamlDataLoading() {
        log.info("Loading YAML test data");
        Map<String, Object> data = dataManager.loadYamlData("playwright-test-data.yaml");

        // Get user data
        String adminUsername = (String) dataManager.getValue(data, "users.admin.username");
        String adminPassword = (String) dataManager.getValue(data, "users.admin.password");
        String adminRole = (String) dataManager.getValue(data, "users.admin.role");

        log.info("Admin user: {} (Role: {}, Password length: {})", adminUsername, adminRole, adminPassword.length());
        assertThat(adminUsername).isEqualTo("admin@playwright-test.com");
        assertThat(adminRole).isEqualTo("ADMIN");
        assertThat(adminPassword).isNotEmpty();

        // Get browser configuration data
        Map<String, Object> chromiumConfig = (Map<String, Object>) dataManager.getValue(data, "browsers.chromium");
        String browserName = (String) chromiumConfig.get("name");
        String browserType = (String) chromiumConfig.get("type");
        boolean headless = (boolean) chromiumConfig.get("headless");

        log.info("Browser: {} (Type: {}, Headless: {})", browserName, browserType, headless);
        assertThat(browserName).isEqualTo("Chromium");
        assertThat(browserType).isEqualTo("chromium");
        assertThat(headless).isTrue();
    }

    @Test(description = "Demonstrate JSON data loading")
    @Description("Loads and uses data from JSON files")
    @Severity(SeverityLevel.NORMAL)
    @Story("Test Data Management")
    @SuppressWarnings("unchecked")
    public void testJsonDataLoading() {
        log.info("Loading JSON test data");
        Map<String, Object> data = dataManager.loadJsonData("test-data.json");

        // Get test scenario data from JSON
        Map<String, Object> firstScenario = (Map<String, Object>) ((java.util.List<?>) data.get("testScenarios")).get(0);
        String scenarioId = (String) firstScenario.get("id");
        String scenarioName = (String) firstScenario.get("name");
        String description = (String) firstScenario.get("description");

        log.info("Test Data (JSON): Scenario {} - {} (Description: {})", scenarioId, scenarioName, description);
        assertThat(scenarioId).isEqualTo("SCENARIO-001");
        assertThat(scenarioName).isEqualTo("User Login Flow");
        assertThat(description).isNotEmpty();

        // Get test steps
        List<Map<String, Object>> steps = (List<Map<String, Object>>) firstScenario.get("steps");
        Map<String, Object> firstStep = steps.get(0);
        String action = (String) firstStep.get("action");
        String url = (String) firstStep.get("url");

        log.info("First step: {} to {}", action, url);
        assertThat(action).isEqualTo("navigate");
        assertThat(url).isEqualTo("https://playwright.dev/java/");
    }

    @Test(description = "Demonstrate CSV data loading")
    @Description("Loads and uses data from CSV files")
    @Severity(SeverityLevel.NORMAL)
    @Story("Test Data Management")
    @SuppressWarnings("unchecked")
    public void testCsvDataLoading() {
        log.info("Loading CSV test data");
        Map<String, Object> data = dataManager.loadCsvData("test-data.csv");

        // Get test data from CSV
        Map<String, Object> firstBrowser = (Map<String, Object>) ((java.util.List<?>) data.get("data")).get(0);
        String browserId = (String) firstBrowser.get("id");
        String browserName = (String) firstBrowser.get("name");
        String browserType = (String) firstBrowser.get("type");
        String version = (String) firstBrowser.get("version");
        boolean headless = Boolean.parseBoolean((String) firstBrowser.get("headless"));
        int viewportWidth = Integer.parseInt((String) firstBrowser.get("viewport_width"));
        int viewportHeight = Integer.parseInt((String) firstBrowser.get("viewport_height"));

        log.info("Test Data (CSV): Browser {} - {} (Type: {}, Version: {}, Headless: {}, Viewport: {}x{})",
                browserId, browserName, browserType, version, headless, viewportWidth, viewportHeight);
        assertThat(browserId).isEqualTo("BROWSER-001");
        assertThat(browserName).isEqualTo("Chromium");
        assertThat(browserType).isEqualTo("Browser");
        assertThat(version).isEqualTo("1.48.0");
        assertThat(headless).isTrue();
        assertThat(viewportWidth).isEqualTo(1920);
        assertThat(viewportHeight).isEqualTo(1080);
    }

    @Test(description = "Demonstrate dynamic data generation")
    @Description("Generates and uses dynamic test data")
    @Severity(SeverityLevel.NORMAL)
    @Story("Test Data Management")
    public void testDynamicDataGeneration() {
        // Generate user data
        String email = TestDataGenerator.generateEmail();
        String password = TestDataGenerator.generatePassword(12, true);
        String phone = TestDataGenerator.generatePhoneNumber();
        String address = TestDataGenerator.generateAddress();

        log.info("Generated user data:");
        log.info("Email: {}", email);
        log.info("Password: {}", password);
        log.info("Phone: {}", phone);
        log.info("Address: {}", address);

        // Validate generated data
        assertThat(email).contains("@example.com");
        assertThat(password).hasSize(12);
        assertThat(phone).matches("\\+1\\d{10}");
        assertThat(address).matches("\\d+ [\\w\\s]+,\\s+[\\w\\s]+ \\d{5}");
    }
}
