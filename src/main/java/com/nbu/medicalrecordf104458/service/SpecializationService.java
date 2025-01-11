package com.nbu.medicalrecordf104458.service;

import com.nbu.medicalrecordf104458.dto.SpecializationDto;

import java.util.List;

public interface SpecializationService {

    List<SpecializationDto> getAllSpecializations();

    SpecializationDto getSpecializationById(Long id);

    SpecializationDto createSpecialization(SpecializationDto specializationDto);

    SpecializationDto updateSpecialization(Long id, SpecializationDto specializationDto);

    void deleteSpecialization(Long id);

}
