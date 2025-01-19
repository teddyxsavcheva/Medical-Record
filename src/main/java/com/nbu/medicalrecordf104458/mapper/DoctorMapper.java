package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class DoctorMapper {

    private final SpecializationRepository specializationRepository;

    public DoctorDto convertToDto(Doctor doctor) {
        DoctorDto dto = new DoctorDto();

        dto.setId(doctor.getId());
        dto.setName(doctor.getName());

        if (!doctor.getSpecializations().isEmpty()) {
            dto.setSpecializationIds(doctor.getSpecializations().stream()
                    .map(Specialization::getId)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public Doctor convertToEntity(DoctorDto dto) {
        Doctor doctor = new Doctor();

        doctor.setName(dto.getName());

        if (!dto.getSpecializationIds().isEmpty()) {
            doctor.setSpecializations(dto.getSpecializationIds().stream()
                    .map(id -> specializationRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + id)))
                    .collect(Collectors.toSet()));
        }

        return doctor;
    }

}
