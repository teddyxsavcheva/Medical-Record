package com.nbu.medicalrecordf104458.utility;

import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.DoctorSecurityException;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.PatientSecurityException;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.model.user.Role;
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

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean isPatientAccessingOwnData(Long patientId) {
        User currentUser = getCurrentUser();
        Patient userPatient = currentUser.getPatient();

        if (userPatient == null || !userPatient.getId().equals(patientId)) {
            throw new PatientSecurityException("Access denied: Current user does not have access to that page.");
        }

        return true;
    }

    // TODO: Check rest
    public boolean checkDoctorAccessForAppointmentCreation(Long appointmentDoctorId) {
        User currentUser = getCurrentUser();
        Doctor userDoctor = currentUser.getDoctor();

        if (userDoctor == null || !userDoctor.getId().equals(appointmentDoctorId)) {
            if (!currentUser.getRole().equals(Role.ADMIN)) {
                throw new DoctorSecurityException("Access denied: Current user does not have access to that page.");
            }
        }

        return true;
    }

    public boolean isDoctorAssociatedWithAppointment(Long appointmentId) {
        User currentUser = getCurrentUser();
        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No appointment found with id: " + appointmentId));

        if (currentUser.getDoctor() == null || !currentUser.getDoctor().getId().equals(appointment.getDoctor().getId())) {
            if (!currentUser.getRole().equals(Role.ADMIN)) {
                throw new DoctorSecurityException("Access denied: Current user does not have access to that page.");
            }
        }

        return true;
    }

    public boolean isDoctorAssociatedWithSickLeave(Long sickLeaveId) {
        User currentUser = getCurrentUser();
        Doctor userDoctor = currentUser.getDoctor();
        SickLeave sickLeave = sickLeaveRepository.findById(sickLeaveId)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + sickLeaveId));

        if (userDoctor == null || !userDoctor.getId().equals(sickLeave.getDoctorAppointment().getDoctor().getId())) {
            if (!currentUser.getRole().equals(Role.ADMIN)) {
                throw new DoctorSecurityException("Access denied: Current user does not have access to that page.");
            }
        }

        return true;
    }

}
