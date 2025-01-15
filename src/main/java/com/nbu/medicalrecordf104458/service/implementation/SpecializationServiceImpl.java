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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SpecializationServiceImpl implements SpecializationService {

    private final SpecializationRepository specializationRepository;
    private final DoctorRepository doctorRepository;
    private final SpecializationMapper mapper;

    @Override
    public List<SpecializationDto> getAllSpecializations() {
        List<Specialization> specializations = specializationRepository.findAll();

        return specializations.stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SpecializationDto getSpecializationById(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + id));

        return mapper.convertToDto(specialization);
    }

    @Override
    public SpecializationDto createSpecialization(SpecializationDto specializationDto) {
       Specialization specialization = mapper.convertToEntity(specializationDto);

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

    @Override
    public SpecializationDto updateSpecialization(Long id, SpecializationDto specializationDto) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + id));

        specialization.setName(specializationDto.getName());

        if (!specializationDto.getDoctorIds().isEmpty()) {
            specialization.setDoctors(specializationDto.getDoctorIds().stream()
                    .map(doctorId -> doctorRepository.findById(doctorId)
                            .orElseThrow(() -> new EntityNotFoundException("No Doctor found with id: " + doctorId)))
                    .collect(Collectors.toList()));
        }

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

    @Override
    public void deleteSpecialization(Long id) {
        Specialization specialization = specializationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + id));

        specializationRepository.delete(specialization);
    }

    @Override
    public SpecializationDto addDoctor(Long specializationId, Long doctorId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + doctorId));

        specialization.getDoctors().add(doctor);

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

    @Override
    public SpecializationDto removeDoctor(Long specializationId, Long doctorId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + doctorId));

        specialization.getDoctors().remove(doctor);

        return mapper.convertToDto(specializationRepository.save(specialization));
    }

}
