package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;

import java.util.Set;

public interface DoctorAppointmentService {

    Set<AppointmentDto> getAllAppointments();

    AppointmentDto getAppointmentById(Long id);

    AppointmentDto createAppointment(AppointmentDto appointmentDto);

    AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto);

    void deleteAppointment(Long id);

    // Add/Remove methods for Treatment and Diagnose  many-to-many table

    AppointmentDto addDiagnose(Long appointmentId, Long diagnoseId);

    AppointmentDto removeDiagnose(Long appointmentId, Long diagnoseId);

    AppointmentDto addTreatment(Long appointmentId, Long treatmentId);

    AppointmentDto removeTreatment(Long appointmentId, Long treatmentId);

    // Queries

    Set<PatientDto> getPatientsByDiagnoseId(Long diagnoseId);

}
