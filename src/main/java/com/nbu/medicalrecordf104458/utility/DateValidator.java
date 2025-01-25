package com.nbu.medicalrecordf104458.utility;

import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.InvalidDateException;

import java.time.LocalDate;

public class DateValidator {

    private DateValidator() {
        // Private constructor to prevent class instantiation
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateException("The start date cannot be after the end date.");
        }
    }

}
