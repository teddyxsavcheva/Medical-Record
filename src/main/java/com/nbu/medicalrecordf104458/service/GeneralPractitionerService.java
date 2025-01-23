package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;

import java.util.Set;

public interface GeneralPractitionerService {

    Set<GeneralPractitionerDto> getAllDoctors();

    GeneralPractitionerDto getDoctorById(Long id);

    GeneralPractitionerDto createDoctor(GeneralPractitionerDto gpDto);

    GeneralPractitionerDto updateDoctor(Long id, GeneralPractitionerDto gpDto);

    void deleteDoctor(Long id);

    // Add/Remove methods for Patient and Specialization many-to-many tables

    GeneralPractitionerDto addSpecialization(Long gpId, Long specializationId);

    GeneralPractitionerDto removeSpecialization(Long gpId, Long specializationId);

    GeneralPractitionerDto addPatient(Long gpId, Long patientId);

    GeneralPractitionerDto removePatient(Long gpId, Long patientId);

    // Queries

    Long getPatientsCountByGPId(Long gpId);

}
