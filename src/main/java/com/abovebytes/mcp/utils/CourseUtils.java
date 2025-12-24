package com.abovebytes.mcp.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class CourseUtils {

    private static final Random RANDOM = new Random();

    /**
     * Generate a random course duration in minutes.
     * For example, between 30 minutes and 180 minutes.
     */
    public static int randomDurationMinutes() {
        int min = 30;
        int max = 360;
        return RANDOM.nextInt(max - min + 1) + min;
    }

    /**
     * Generate a random course URL based on the course name.
     * Example: "Java Basics" -> "https://courses.abovebytes.com/java-basics-237"
     */
    public static String randomCourseUrl(String provider) {
        if (provider == null || provider.isBlank()) {
            provider = "course";
        }

        // Replace spaces and special chars with hyphens for URL-friendly format
        String slug = URLEncoder.encode(provider.trim().toLowerCase().replaceAll("\\s+", "-"), StandardCharsets.UTF_8);

        // Append a random number to ensure uniqueness
        int randomSuffix = RANDOM.nextInt(900) + 100; // 100-999

        return "https://courses.abovebytes.com/" + slug + "-" + randomSuffix;
    }

    /**
     * Generate a random course rating between 1.0 and 5.0 (inclusive)
     * Rounded to one decimal place
     */
    public static double randomRating() {
        double rating = 1.0 + (RANDOM.nextDouble() * 4.0); // 1.0 <= rating < 5.0
        return Math.round(rating * 10.0) / 10.0; // round to 1 decimal place
    }
}
