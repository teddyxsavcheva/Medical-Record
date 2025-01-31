package com.nbu.medicalrecordf104458.utility;

import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.DoctorSecurityException;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.PatientSecurityException;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.user.User;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("customSecurityChecker")
public class CustomSecurityChecker {

    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorAppointmentRepository appointmentRepository;

    public boolean isPatientAccessingOwnData(Long patientId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser.getPatient() == null
                || !currentUser.getPatient().getId().equals(patientId)) {
            throw new PatientSecurityException
                    ("Access denied: Current user is not a patient or does not have access to that page.");
        }

        return true;
    }

    public boolean checkDoctorAccessForAppointmentCreation(Long doctorId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser.getDoctor() == null) {
            throw new DoctorSecurityException("Access denied: Current user is not a doctor.");
        }

        Long userDoctorId = currentUser.getDoctor().getId();

        // Ако потребителят не е администратор или ако докторът не съвпада с този в заявката, хвърляне на изключение
        if (!userDoctorId.equals(doctorId)) {
            throw new DoctorSecurityException
                    ("Access denied: You are not authorized to create appointments for this doctor.");
        }

        return true;
    }

    public boolean isDoctorAssociatedWithSickLeave(Long sickLeaveId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser.getDoctor() == null) {
            throw new DoctorSecurityException("Access denied: Current user is not a doctor.");
        }

        Long userDoctorId = currentUser.getDoctor().getId();

        SickLeave sickLeave = sickLeaveRepository.findById(sickLeaveId)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + sickLeaveId));

        if (!userDoctorId.equals(sickLeave.getDoctorAppointment().getDoctor().getId())) {
            throw new DoctorSecurityException("Access denied: You are not the doctor associated with this sick leave.");
        }

        return true;
    }

    public boolean isDoctorAssociatedWithAppointment(Long appointmentId) {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (currentUser.getDoctor() == null) {
            throw new DoctorSecurityException("Access denied: Current user is not a doctor.");
        }

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (!currentUser.getDoctor().getId().equals(appointment.getDoctor().getId())) {
            throw new DoctorSecurityException("Access denied: You are not the doctor associated with this appointment.");
        }

        return true;
    }

}
