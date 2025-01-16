package com.nbu.medicalrecordf104458.service;


import com.nbu.medicalrecordf104458.dto.PatientDto;

import java.util.List;

public interface PatientService {

    List<PatientDto> getAllPatients();

    PatientDto getPatientById(Long id);

    PatientDto createPatient(PatientDto patientDto);

    PatientDto updatePatient(Long id, PatientDto patientDto);

    void deletePatient(Long id);

    boolean isInsurancePaidLast6Months(Long patientId);

}
