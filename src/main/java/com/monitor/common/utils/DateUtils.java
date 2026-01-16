package com.monitor.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Date utility class
 *
 * @author monitor
 */
public class DateUtils {

    private DateUtils() {
    }

    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String CHINESE_PATTERN = "yyyy年MM月dd日 HH:mm:ss";

    private static final Map<String, DateTimeFormatter> FORMATTERS = new HashMap<>();

    static {
        FORMATTERS.put(DEFAULT_PATTERN, DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
        FORMATTERS.put(DATE_PATTERN, DateTimeFormatter.ofPattern(DATE_PATTERN));
        FORMATTERS.put(TIME_PATTERN, DateTimeFormatter.ofPattern(TIME_PATTERN));
        FORMATTERS.put(TIMESTAMP_PATTERN, DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
        FORMATTERS.put(CHINESE_PATTERN, DateTimeFormatter.ofPattern(CHINESE_PATTERN));
    }

    // ==================== Format Methods ====================

    /**
     * Format LocalDateTime to string
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_PATTERN);
    }

    /**
     * Format LocalDateTime to string with pattern
     */
    public static String formatDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(pattern,
                DateTimeFormatter.ofPattern(pattern));
        return dateTime.format(formatter);
    }

    /**
     * Format LocalDate to string
     */
    public static String formatLocalDate(LocalDate date) {
        return formatLocalDate(date, DATE_PATTERN);
    }

    /**
     * Format LocalDate to string with pattern
     */
    public static String formatLocalDate(LocalDate date, String pattern) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(pattern,
                DateTimeFormatter.ofPattern(pattern));
        return date.format(formatter);
    }

    /**
     * Format LocalTime to string
     */
    public static String formatLocalTime(LocalTime time) {
        return formatLocalTime(time, TIME_PATTERN);
    }

    /**
     * Format LocalTime to string with pattern
     */
    public static String formatLocalTime(LocalTime time, String pattern) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(pattern,
                DateTimeFormatter.ofPattern(pattern));
        return time.format(formatter);
    }

    /**
     * Format Date to string
     */
    public static String formatDate(Date date) {
        return formatDate(date, DEFAULT_PATTERN);
    }

    /**
     * Format Date to string with pattern
     */
    public static String formatDate(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    // ==================== Parse Methods ====================

    /**
     * Parse string to LocalDateTime
     */
    public static LocalDateTime parseLocalDateTime(String dateStr) {
        return parseLocalDateTime(dateStr, DEFAULT_PATTERN);
    }

    /**
     * Parse string to LocalDateTime with pattern
     */
    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(pattern,
                DateTimeFormatter.ofPattern(pattern));
        return LocalDateTime.parse(dateStr, formatter);
    }

    /**
     * Parse string to LocalDate
     */
    public static LocalDate parseLocalDate(String dateStr) {
        return parseLocalDate(dateStr, DATE_PATTERN);
    }

    /**
     * Parse string to LocalDate with pattern
     */
    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(pattern,
                DateTimeFormatter.ofPattern(pattern));
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * Parse string to LocalTime
     */
    public static LocalTime parseLocalTime(String timeStr) {
        return parseLocalTime(timeStr, TIME_PATTERN);
    }

    /**
     * Parse string to LocalTime with pattern
     */
    public static LocalTime parseLocalTime(String timeStr, String pattern) {
        if (timeStr == null || timeStr.isEmpty()) {
            return null;
        }
        DateTimeFormatter formatter = FORMATTERS.getOrDefault(pattern,
                DateTimeFormatter.ofPattern(pattern));
        return LocalTime.parse(timeStr, formatter);
    }

    /**
     * Parse string to Date (java.util.Date)
     */
    public static Date parseUtilDate(String dateStr) {
        return parseUtilDate(dateStr, DEFAULT_PATTERN);
    }

    /**
     * Parse string to Date (java.util.Date) with pattern
     */
    public static Date parseUtilDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse date: " + dateStr, e);
        }
    }

    // ==================== Add/Subtract Methods ====================

    /**
     * Add days to LocalDateTime
     */
    public static LocalDateTime addDaysToDateTime(LocalDateTime dateTime, int days) {
        return dateTime.plusDays(days);
    }

    /**
     * Add days to LocalDate
     */
    public static LocalDate addDaysToLocalDate(LocalDate date, int days) {
        return date.plusDays(days);
    }

    /**
     * Add hours to LocalDateTime
     */
    public static LocalDateTime addHoursToDateTime(LocalDateTime dateTime, int hours) {
        return dateTime.plusHours(hours);
    }

    /**
     * Add minutes to LocalDateTime
     */
    public static LocalDateTime addMinutesToDateTime(LocalDateTime dateTime, int minutes) {
        return dateTime.plusMinutes(minutes);
    }

    /**
     * Add seconds to LocalDateTime
     */
    public static LocalDateTime addSecondsToDateTime(LocalDateTime dateTime, int seconds) {
        return dateTime.plusSeconds(seconds);
    }

    /**
     * Subtract days from LocalDateTime
     */
    public static LocalDateTime subtractDaysFromDateTime(LocalDateTime dateTime, int days) {
        return dateTime.minusDays(days);
    }

    // ==================== Day Start/End Methods ====================

    /**
     * Get start of day
     */
    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    /**
     * Get end of day
     */
    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    /**
     * Get first day of month
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * Get last day of month
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    // ==================== Convert Methods ====================

    /**
     * Convert LocalDateTime to Date
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.toInstant(ZoneOffset.of("+8")));
    }

    /**
     * Convert Date to LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
    }

    // ==================== Other Methods ====================

    /**
     * Get current timestamp in milliseconds
     */
    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * Get current LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Get current LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * Check if date is today
     */
    public static boolean isToday(LocalDate date) {
        return date.equals(today());
    }

    /**
     * Calculate difference in days between two dates
     */
    public static long daysBetween(LocalDate start, LocalDate end) {
        return end.toEpochDay() - start.toEpochDay();
    }

    /**
     * Calculate difference in hours between two datetime
     */
    public static long hoursBetween(LocalDateTime start, LocalDateTime end) {
        return end.toInstant(ZoneOffset.of("+8")).toEpochMilli()
                - start.toInstant(ZoneOffset.of("+8")).toEpochMilli() / (1000 * 60 * 60);
    }
}
