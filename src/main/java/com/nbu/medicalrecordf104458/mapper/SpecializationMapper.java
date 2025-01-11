package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.SpecializationDto;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class SpecializationMapper {

    private final DoctorRepository doctorRepository;

    public SpecializationDto convertToDto(Specialization specialization) {
        SpecializationDto dto = new SpecializationDto();

        dto.setId(specialization.getId());
        dto.setName(specialization.getName());

        if (!specialization.getDoctors().isEmpty()) {
            dto.setDoctorIds(specialization.getDoctors().stream()
                    .map(Doctor::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Specialization convertToEntity(SpecializationDto dto) {

        Specialization specialization = new Specialization();

        specialization.setName(dto.getName());

        if (!dto.getDoctorIds().isEmpty()) {
            specialization.setDoctors(dto.getDoctorIds().stream()
                    .map(id -> doctorRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No Doctor found with id: " + id)))
                    .collect(Collectors.toList()));
        }

        return specialization;
    }

}
