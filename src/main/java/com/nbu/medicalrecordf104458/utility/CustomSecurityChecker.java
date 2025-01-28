package com.nbu.medicalrecordf104458.utility;

import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("customSecurityChecker")
public class CustomSecurityChecker {

    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorAppointmentRepository appointmentRepository;

    public boolean isDoctorAssociatedWithSickLeave(Long sickLeaveId, Long doctorId) {
        SickLeave sickLeave = sickLeaveRepository.findById(sickLeaveId)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + sickLeaveId));

        return sickLeave.getDoctorAppointment().getDoctor().getId().equals(doctorId);
    }

    public boolean isDoctorAssociatedWithAppointment(Long appointmentId, Long doctorId) {
        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        return appointment.getDoctor().getId().equals(doctorId);
    }

}
