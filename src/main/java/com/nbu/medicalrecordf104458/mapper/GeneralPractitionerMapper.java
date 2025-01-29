package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class GeneralPractitionerMapper {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final SpecializationRepository specializationRepository;
    private final DoctorMapper doctorMapper;

    public GeneralPractitionerDto convertToDto(GeneralPractitioner gp) {
        GeneralPractitionerDto dto = new GeneralPractitionerDto();

        dto.setDoctor(doctorMapper.convertToDto(gp));

        if (!gp.getPatients().isEmpty()) {
            dto.setPatients(gp.getPatients().stream()
                    .map(Patient::getId)
                    .collect(Collectors.toSet()));
        }

        if (gp.getUser() != null) {
            dto.setUserId(gp.getUser().getId());
        }

        return dto;
    }

    public GeneralPractitioner convertToEntity(GeneralPractitionerDto dto) {
        GeneralPractitioner gp = new GeneralPractitioner();

        gp.setName(dto.getDoctor().getName());

        if (!dto.getDoctor().getSpecializationIds().isEmpty()) {
            gp.setSpecializations(dto.getDoctor().getSpecializationIds().stream()
                    .map(id -> specializationRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + id)))
                    .collect(Collectors.toSet()));
        }

        if (!dto.getPatients().isEmpty()) {
            gp.setPatients(dto.getPatients().stream()
                    .map(id -> patientRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No Patient found with id: " + id)))
                    .collect(Collectors.toSet()));
        }

        if (dto.getUserId() != null) {
            gp.setUser(userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("No user found with id: " + dto.getUserId())));
        }

        return gp;
    }

}
