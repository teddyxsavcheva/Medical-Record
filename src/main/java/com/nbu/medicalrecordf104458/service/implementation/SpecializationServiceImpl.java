package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.SpecializationDto;
import com.nbu.medicalrecordf104458.mapper.SpecializationMapper;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.service.SpecializationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationMapper mapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public Set<SpecializationDto> getAllSpecializations() {
        return specializationRepository.findAllByDeletedFalse().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public SpecializationDto getSpecializationById(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .filter(specialization1 -> !specialization1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + id));

        return mapper.convertToDto(specialization);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public SpecializationDto createSpecialization(SpecializationDto specializationDto) {
       Specialization specialization = mapper.convertToEntity(specializationDto);

        Set<Doctor> doctors = specialization.getDoctors().stream()
                .filter(doctor -> doctor.isDeleted())
                .collect(Collectors.toSet());

        if (!doctors.isEmpty()) {
            throw new IllegalArgumentException("You can't use records that are marked for deletion!");
        }

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public SpecializationDto updateSpecialization(Long id, SpecializationDto specializationDto) {
        Specialization specialization = specializationRepository.findById(id)
                .filter(specialization1 -> !specialization1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + id));

        specialization.setName(specializationDto.getName());

        if (!specializationDto.getDoctorIds().isEmpty()) {
            specialization.setDoctors(specializationDto.getDoctorIds().stream()
                    .map(doctorId -> doctorRepository.findById(doctorId)
                            .filter(doctor -> !doctor.isDeleted())
                            .orElseThrow(() -> new EntityNotFoundException("No Doctor found with id: " + doctorId)))
                    .collect(Collectors.toSet()));
        }

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteSpecialization(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .filter(specialization1 -> !specialization1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + id));

        specialization.setDeleted(true);

        specializationRepository.save(specialization);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public SpecializationDto addDoctor(Long specializationId, Long doctorId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .filter(specialization1 -> !specialization1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .filter(doctor1 -> !doctor1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + doctorId));

        specialization.getDoctors().add(doctor);

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public SpecializationDto removeDoctor(Long specializationId, Long doctorId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .filter(specialization1 -> !specialization1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .filter(doctor1 -> !doctor1.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + doctorId));

        specialization.getDoctors().remove(doctor);

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

}
