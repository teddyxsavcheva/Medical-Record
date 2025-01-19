package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;

import java.util.Set;

public interface DoctorAppointmentService {

    Set<AppointmentDto> getAllAppointments();

    AppointmentDto getAppointmentById(Long id);

    AppointmentDto createAppointment(AppointmentDto appointmentDto);

    AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto);

    void deleteAppointment(Long id);

    AppointmentDto addDiagnose(Long appointmentId, Long diagnoseId);

    AppointmentDto removeDiagnose(Long appointmentId, Long diagnoseId);

    AppointmentDto addTreatment(Long appointmentId, Long treatmentId);

    AppointmentDto removeTreatment(Long appointmentId, Long treatmentId);

}
