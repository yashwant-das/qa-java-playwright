package com.qaframework.playwright.listeners;

import com.qaframework.playwright.util.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;

import java.util.List;

/**
 * TestNG listener that configures test suite parameters and parallel execution
 * based on YAML configuration settings
 */
public class TestConfigurationListener implements IAlterSuiteListener {
    private static final Logger log = LoggerFactory.getLogger(TestConfigurationListener.class);
    private final ConfigReader configReader = ConfigReader.getInstance();

    @Override
    public void alter(List<XmlSuite> suites) {
        for (XmlSuite suite : suites) {
            configureSuiteParameters(suite);
            configureParallelExecution(suite);
        }
    }

    /**
     * Configure suite parameters from YAML configuration
     */
    private void configureSuiteParameters(XmlSuite suite) {
        var browser = configReader.getConfig().getBrowser();
        var environment = configReader.getConfig().getEnvironment();

        // Set parameters from YAML configuration
        suite.getParameters().put("browser", browser.getType());
        suite.getParameters().put("headless", String.valueOf(browser.isHeadless()));
        suite.getParameters().put("environment", environment.getName());

        log.info("Configured suite parameters from YAML - browser: {}, headless: {}, environment: {}",
                browser.getType(), browser.isHeadless(), environment.getName());
    }

    /**
     * Configure parallel execution based on configuration settings
     */
    private void configureParallelExecution(XmlSuite suite) {
        var testExecution = configReader.getConfig().getTestExecution();

        if (testExecution != null && testExecution.isParallel()) {
            log.info("Enabling parallel execution with {} threads", testExecution.getThreadCount());
            suite.setParallel(XmlSuite.ParallelMode.METHODS);
            suite.setThreadCount(testExecution.getThreadCount());
        } else {
            log.info("Parallel execution is disabled");
            suite.setParallel(XmlSuite.ParallelMode.NONE);
        }
    }
}
