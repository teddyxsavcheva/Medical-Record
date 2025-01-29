package com.nbu.medicalrecordf104458.exceptionhandler.exceptions;

import jakarta.persistence.PersistenceException;

public class PatientSecurityException extends PersistenceException {

    public PatientSecurityException(String message) {
        super(message);
    }

}
