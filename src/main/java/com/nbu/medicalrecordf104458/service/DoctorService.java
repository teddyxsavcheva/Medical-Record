package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.DoctorDto;

import java.util.List;

public interface DoctorService {

    List<DoctorDto> getAllDoctors();

    DoctorDto getDoctorById(Long id);

    DoctorDto createDoctor(DoctorDto doctorDto);

    DoctorDto updateDoctor(Long id, DoctorDto doctorDto);

    void deleteDoctor(Long id);

    DoctorDto addSpecialization(Long doctorId, Long specializationId);

    DoctorDto removeSpecialization(Long doctorId, Long specializationId);

}
