package com.nbu.medicalrecordf104458.exceptionhandler;

import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.DoctorSecurityException;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.InvalidDateException;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.PatientSecurityException;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.SickLeaveAlreadyExistsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Username not found: " + ex.getMessage());
    }

    @ExceptionHandler(SickLeaveAlreadyExistsException.class)
    public ResponseEntity<String> handleSickLeaveAlreadyExistsException(SickLeaveAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidDateException.class)
    public ResponseEntity<String> handleInvalidDateException(InvalidDateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(PatientSecurityException.class)
    public ResponseEntity<String> handlePatientSecurityException(PatientSecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    @ExceptionHandler(DoctorSecurityException.class)
    public ResponseEntity<String> handleDoctorSecurityException(DoctorSecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

}
