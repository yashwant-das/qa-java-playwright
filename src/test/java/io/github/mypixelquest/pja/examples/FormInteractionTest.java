package io.github.mypixelquest.pja.examples;

import io.github.mypixelquest.pja.core.PlaywrightTest;
import io.qameta.allure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Demonstrates form interaction capabilities
 * Uses Selenium web form for stable form testing
 * Reference: https://www.selenium.dev/selenium/web/web-form.html
 */
@Epic("Framework Capabilities")
@Feature("Form Interaction")
public class FormInteractionTest extends PlaywrightTest {
    private static final Logger log = LoggerFactory.getLogger(FormInteractionTest.class);
    private static final String FORM_URL = "https://www.selenium.dev/selenium/web/web-form.html";

    @Test(description = "Test form submission")
    @Description("Demonstrates filling and submitting a form")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Interaction")
    public void testFormSubmission() {
        log.info("Testing form submission");

        getCurrentPage().ifPresent(page -> {
            // Navigate to Selenium web form page
            page.navigate(FORM_URL);
            page.waitForLoadState();

            // Fill text input field (name="my-text", id="my-text-id")
            page.fill("input[name='my-text']", "Test User");

            // Fill password field (name="my-password")
            page.fill("input[name='my-password']", "SecurePassword123");

            // Fill textarea (name="my-textarea")
            page.fill("textarea[name='my-textarea']", "This is a test message for the textarea field.");

            // Select from dropdown (name="my-select", options: "One", "Two", "Three")
            page.selectOption("select[name='my-select']", "Two"); // Select option with text "Two"

            // Fill datalist input (name="my-datalist", placeholder="Type to search...")
            page.fill("input[name='my-datalist']", "New York");

            // Uncheck the default checked checkbox and check the default checkbox
            // Checkboxes: name="my-check", id="my-check-1" (checked by default), id="my-check-2" (unchecked)
            page.uncheck("input[id='my-check-1']"); // Uncheck the checked checkbox
            page.check("input[id='my-check-2']"); // Check the default checkbox

            // Select radio button (name="my-radio", id="my-radio-1" checked by default, id="my-radio-2" unchecked)
            page.check("input[id='my-radio-2']"); // Select the default radio

            // Submit form
            page.click("button[type='submit']");

            // Wait for form submission response
            page.waitForLoadState();

            // Verify form was submitted successfully
            // The form redirects to submitted-form.html with "Form submitted" heading
            assertThat(page.url()).contains("submitted-form.html");
            var heading = page.locator("h1").textContent();
            assertThat(heading).isNotNull();
            assertThat(heading.trim()).isEqualTo("Form submitted");

            log.info("Form submitted successfully");
        });
    }

    @Test(description = "Test form field interactions")
    @Description("Demonstrates various form field interactions")
    @Severity(SeverityLevel.NORMAL)
    @Story("Form Interaction")
    public void testFormFieldInteractions() {
        log.info("Testing form field interactions");

        getCurrentPage().ifPresent(page -> {
            page.navigate(FORM_URL);
            page.waitForLoadState();

            // Test text input (name="my-text", id="my-text-id")
            var textField = page.locator("input[name='my-text']");
            textField.fill("John Doe");
            assertThat(textField.inputValue()).isEqualTo("John Doe");

            // Test password input (name="my-password")
            var passwordField = page.locator("input[name='my-password']");
            passwordField.fill("MyPassword123");
            assertThat(passwordField.inputValue()).isEqualTo("MyPassword123");

            // Test textarea (name="my-textarea")
            var textareaField = page.locator("textarea[name='my-textarea']");
            textareaField.fill("This is a test message.");
            assertThat(textareaField.inputValue()).isEqualTo("This is a test message.");

            // Test dropdown selection (name="my-select", options: "One", "Two", "Three")
            var selectField = page.locator("select[name='my-select']");
            selectField.selectOption("One");
            var selectedOption = selectField.locator("option:checked").textContent();
            assertThat(selectedOption).isEqualTo("One");

            // Test datalist input (name="my-datalist", placeholder="Type to search...")
            var datalistField = page.locator("input[name='my-datalist']");
            datalistField.fill("Chicago");
            assertThat(datalistField.inputValue()).isEqualTo("Chicago");

            // Test checkboxes (name="my-check", id="my-check-1" checked by default, id="my-check-2" unchecked)
            var checkedCheckbox = page.locator("input[id='my-check-1']");
            assertThat(checkedCheckbox.isChecked()).isTrue(); // Should be checked by default

            var defaultCheckbox = page.locator("input[id='my-check-2']");
            assertThat(defaultCheckbox.isChecked()).isFalse(); // Should be unchecked by default
            defaultCheckbox.check();
            assertThat(defaultCheckbox.isChecked()).isTrue();

            // Test radio buttons (name="my-radio", id="my-radio-1" checked by default, id="my-radio-2" unchecked)
            var checkedRadio = page.locator("input[id='my-radio-1']");
            assertThat(checkedRadio.isChecked()).isTrue(); // Should be checked by default

            var defaultRadio = page.locator("input[id='my-radio-2']");
            assertThat(defaultRadio.isChecked()).isFalse(); // Should be unchecked by default
            defaultRadio.check();
            assertThat(defaultRadio.isChecked()).isTrue();
            // When default radio is checked, checked radio should be unchecked
            assertThat(checkedRadio.isChecked()).isFalse();

            // Verify readonly field (name="my-readonly", value="Readonly input")
            var readonlyField = page.locator("input[name='my-readonly']");
            assertThat(readonlyField.isEditable()).isFalse();
            assertThat(readonlyField.inputValue()).isEqualTo("Readonly input");

            // Verify disabled field (name="my-disabled", disabled=true)
            var disabledField = page.locator("input[name='my-disabled']");
            assertThat(disabledField.isEnabled()).isFalse();

            // Test color picker (name="my-colors", default value="#563d7c")
            var colorPicker = page.locator("input[name='my-colors']");
            assertThat(colorPicker.inputValue()).isEqualTo("#563d7c");

            // Test date picker (name="my-date")
            var datePicker = page.locator("input[name='my-date']");
            datePicker.fill("2024-12-25");
            assertThat(datePicker.inputValue()).isEqualTo("2024-12-25");

            // Test range slider (name="my-range", default value="5")
            var rangeSlider = page.locator("input[name='my-range']");
            assertThat(rangeSlider.inputValue()).isEqualTo("5");
            rangeSlider.fill("8");
            assertThat(rangeSlider.inputValue()).isEqualTo("8");

            log.info("Form field interactions successful");
        });
    }
}
