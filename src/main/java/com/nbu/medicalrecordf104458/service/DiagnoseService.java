package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.DiagnoseDto;

import java.util.Set;

public interface DiagnoseService {

    Set<DiagnoseDto> getAllDiagnoses();

    DiagnoseDto getDiagnoseById(Long id);

    DiagnoseDto createDiagnose(DiagnoseDto diagnoseDto);

    DiagnoseDto updateDiagnose(Long id, DiagnoseDto diagnoseDto);

    void deleteDiagnose(Long id);

    DiagnoseDto addAppointment(Long diagnoseId, Long appointmentId);

    DiagnoseDto removeAppointment(Long diagnoseId, Long appointmentId);

    // Queries
    Set<DiagnoseDto> findMostCommonDiagnoses();

}
