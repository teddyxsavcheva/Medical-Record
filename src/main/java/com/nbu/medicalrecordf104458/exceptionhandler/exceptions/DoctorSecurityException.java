package com.nbu.medicalrecordf104458.exceptionhandler.exceptions;

import jakarta.persistence.PersistenceException;

public class DoctorSecurityException extends PersistenceException {

    public DoctorSecurityException(String message) {
        super(message);
    }

}
