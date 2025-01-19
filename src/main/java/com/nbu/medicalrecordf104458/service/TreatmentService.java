package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.TreatmentDto;

import java.util.List;

public interface TreatmentService {

    List<TreatmentDto> getAllTreatments();

    TreatmentDto getTreatmentById(Long id);

    TreatmentDto createTreatment(TreatmentDto treatmentDto);

    TreatmentDto updateTreatment(Long id, TreatmentDto treatmentDto);

    void deleteTreatment(Long id);

    TreatmentDto addAppointment(Long treatmentId, Long appointmentId);

    TreatmentDto removeAppointment(Long treatmentId, Long appointmentId);

}
