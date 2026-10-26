package com.qaframework.playwright.smoke;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.AriaRole;
import com.qaframework.playwright.core.PlaywrightTest;
import io.qameta.allure.Step;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Real browser checks using controlled HTML; run with -Denvironment=local. */
public class BrowserSmokeTest extends PlaywrightTest {
    @Test(description = "A user can submit a form on a controlled page")
    @Step("Submit the local starter form")
    public void submitsControlledForm() {
        Page page = getCurrentPage().orElseThrow();
        page.route("https://starter.test/**", route -> {
            String content = route.request().url().contains("/submitted")
                    ? "<h1>Form submitted</h1>"
                    : """
                      <title>Starter form</title>
                      <form action="/submitted">
                        <label>Name <input name="name" required></label>
                        <button type="submit">Send</button>
                      </form>
                      """;
            route.fulfill(new Route.FulfillOptions().setContentType("text/html").setBody(content));
        });
        page.navigate("https://starter.test/form");
        assertThat(page.evaluate("localStorage.getItem('previous-test')")).isNull();
        page.evaluate("localStorage.setItem('previous-test', 'submitted-form')");
        assertThat(page.title()).isEqualTo("Starter form");
        page.getByLabel("Name").fill("Starter user");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Send")).click();
        page.waitForURL("**/submitted?name=Starter+user");
        assertThat(page.getByRole(AriaRole.HEADING).textContent()).isEqualTo("Form submitted");
        assertThat(page.screenshot()).isNotEmpty();
    }

    @Test(description = "Each method starts with an isolated browser context")
    public void startsWithIsolatedContext() {
        Page page = getCurrentPage().orElseThrow();
        assertThat(getCurrentContext().orElseThrow().cookies()).isEmpty();
        page.route("https://starter.test/**", route -> route.fulfill(
                new Route.FulfillOptions().setContentType("text/html").setBody("<h1>Fresh context</h1>")));
        page.navigate("https://starter.test/");
        assertThat(page.evaluate("localStorage.getItem('previous-test')")).isNull();
        page.evaluate("localStorage.setItem('previous-test', 'present')");
        assertThat(page.evaluate("localStorage.getItem('previous-test')")).isEqualTo("present");
    }
}
