package com.qaframework.playwright.testdata;

import java.util.Random;
import java.util.UUID;

/**
 * Generates dynamic test data for testing purposes
 */
public class TestDataGenerator {
    private static final Random random = new Random();
    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    /**
     * Generates a random email address
     *
     * @return A random email address
     */
    public static String generateEmail() {
        return "user" + System.currentTimeMillis() + "@example.com";
    }

    /**
     * Generates a random password
     *
     * @param length              The length of the password
     * @param includeSpecialChars Whether to include special characters
     * @return A random password
     */
    public static String generatePassword(int length, boolean includeSpecialChars) {
        StringBuilder password = new StringBuilder();
        String chars = includeSpecialChars ? ALPHA_NUMERIC + SPECIAL_CHARS : ALPHA_NUMERIC;

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * Generates a random string
     *
     * @param length The length of the string
     * @return A random string
     */
    public static String generateString(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(ALPHA_NUMERIC.charAt(random.nextInt(ALPHA_NUMERIC.length())));
        }
        return result.toString();
    }

    /**
     * Generates a random number between min and max (inclusive)
     *
     * @param min The minimum value
     * @param max The maximum value
     * @return A random number
     */
    public static int generateNumber(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * Generates a random UUID
     *
     * @return A random UUID
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * Generates a random boolean
     *
     * @return A random boolean
     */
    public static boolean generateBoolean() {
        return random.nextBoolean();
    }

    /**
     * Generates a random date in the future
     *
     * @param daysInFuture The number of days in the future
     * @return A random date in the future
     */
    public static String generateFutureDate(int daysInFuture) {
        long currentTime = System.currentTimeMillis();
        long futureTime = currentTime + (daysInFuture * 24L * 60L * 60L * 1000L);
        return new java.util.Date(futureTime).toString();
    }

    /**
     * Generates a random phone number
     *
     * @return A random phone number
     */
    public static String generatePhoneNumber() {
        return String.format("+1%03d%03d%04d",
                generateNumber(100, 999),
                generateNumber(100, 999),
                generateNumber(1000, 9999));
    }

    /**
     * Generates a random address
     *
     * @return A random address
     */
    public static String generateAddress() {
        return String.format("%d %s %s, %s %s",
                generateNumber(1, 9999),
                generateString(10),
                generateString(8),
                generateString(10),
                generateNumber(10000, 99999));
    }
}
