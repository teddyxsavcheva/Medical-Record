package com.nbu.medicalrecordf104458.exceptionhandler.exceptions;

import jakarta.persistence.PersistenceException;

public class SickLeaveAlreadyExistsException extends PersistenceException {

    public SickLeaveAlreadyExistsException(String message) {
        super(message);
    }

}