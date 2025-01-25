package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;

import java.util.Set;

public interface PatientService {

    Set<PatientDto> getAllPatients();

    PatientDto getPatientById(Long id);

    PatientDto createPatient(PatientDto patientDto);

    PatientDto updatePatient(Long id, PatientDto patientDto);

    void deletePatient(Long id);

    boolean isInsurancePaidLast6Months(Long patientId);

    // Queries
    Set<PatientDto> getPatientsByDiagnoseId(Long diagnoseId);

    Set<PatientDto> getPatientsByGeneralPractitioner(Long gpId);

    Set<AppointmentDto> getVisitsByPatient(Long patientId);

}
