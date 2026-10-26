package com.qaframework.playwright.listeners;

import com.qaframework.playwright.util.ConfigReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TestNG retry analyzer that handles test retries based on configuration
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    private static final Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
    private final ConfigReader configReader = ConfigReader.getInstance();
    // Track retry count per test using unique test identifier (class + method name)
    // Using String key instead of ITestResult because TestNG creates new ITestResult objects on each retry
    private static final Map<String, Integer> retryCountMap = new ConcurrentHashMap<>();

    /**
     * Generate a unique key for the test based on class, method name, and instance
     * This ensures proper tracking even in parallel execution scenarios
     */
    private String getTestKey(ITestResult result) {
        String className = result.getTestClass().getName();
        String methodName = result.getMethod().getMethodName();
        Object instance = result.getInstance();
        // Include instance hash to handle parallel execution of same test method
        String instanceId = instance != null ? String.valueOf(instance.hashCode()) : "default";
        return className + "." + methodName + "[" + instanceId + "]";
    }

    @Override
    public boolean retry(ITestResult result) {
        var retryConfig = configReader.getConfig().getRetry();
        if (retryConfig == null || !retryConfig.isEnabled()) {
            return false;
        }

        String testKey = getTestKey(result);
        int retryCount = retryCountMap.getOrDefault(testKey, 0);

        if (retryCount < retryConfig.getMaxRetries()) {
            retryCount++;
            retryCountMap.put(testKey, retryCount);
            log.info("Retrying test '{}' for the {} time (attempt {}/{})",
                    result.getName(), retryCount, retryCount + 1, retryConfig.getMaxRetries() + 1);

            // Wait between retries if configured
            if (retryConfig.getDelayBetweenRetries() > 0) {
                try {
                    Thread.sleep(retryConfig.getDelayBetweenRetries());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Retry delay was interrupted", e);
                }
            }

            return true;
        }

        // Clean up after max retries reached
        retryCountMap.remove(testKey);
        log.warn("Test '{}' has reached maximum retry attempts ({}). Marking as failed.",
                result.getName(), retryConfig.getMaxRetries());
        return false;
    }
}
