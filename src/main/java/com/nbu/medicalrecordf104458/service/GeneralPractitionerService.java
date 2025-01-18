package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;

import java.util.List;

public interface GeneralPractitionerService {

    List<GeneralPractitionerDto> getAllDoctors();

    GeneralPractitionerDto getDoctorById(Long id);

    GeneralPractitionerDto createDoctor(GeneralPractitionerDto gpDto);

    GeneralPractitionerDto updateDoctor(Long id, GeneralPractitionerDto gpDto);

    void deleteDoctor(Long id);

    GeneralPractitionerDto addSpecialization(Long gpDto, Long specializationId);

    GeneralPractitionerDto removeSpecialization(Long gpDto, Long specializationId);

    GeneralPractitionerDto addPatient(Long gpDto, Long patientId);

    GeneralPractitionerDto removePatient(Long gpDto, Long patientId);


}
