package com.nbu.medicalrecordf104458.exceptionhandler.exceptions;

import jakarta.persistence.PersistenceException;

public class InvalidDateException extends PersistenceException {

    public InvalidDateException(String message) {
        super(message);
    }

}