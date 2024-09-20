package com.baticuisine.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public boolean isFutureOrPresentDate(LocalDate date) {
        return date != null && !date.isBefore(LocalDate.now());
    }

    public boolean isValidDate(LocalDate date) {
        return date != null;
    }

    public boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    public String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    public String formatDateLong(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));
    }

    public LocalDate parseDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use dd/MM/yyyy.");
        }
    }

    public int calculateDaysBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    public int calculateWeeksBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.WEEKS.between(startDate, endDate);
    }

    public int calculateMonthsBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.MONTHS.between(startDate, endDate);
    }
}
