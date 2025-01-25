package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.GeneralPractitionerDto;
import com.nbu.medicalrecordf104458.dto.queries.GpPatientsCountDto;
import com.nbu.medicalrecordf104458.mapper.GeneralPractitionerMapper;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.service.GeneralPractitionerService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class GeneralPractitionerServiceImpl implements GeneralPractitionerService {

    private final PatientRepository patientRepository;
    private final SpecializationRepository specializationRepository;
    private final GeneralPractitionerRepository gpRepository;
    private final GeneralPractitionerMapper gpMapper;

    @Override
    public Set<GeneralPractitionerDto> getAllDoctors() {
        return gpRepository.findAll().stream()
                .map(gpMapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public GeneralPractitionerDto getDoctorById(Long id) {
        GeneralPractitioner gp = gpRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + id));

        return gpMapper.convertToDto(gp);
    }

    @Override
    public GeneralPractitionerDto createDoctor(GeneralPractitionerDto gpDto) {
        GeneralPractitioner gp = gpMapper.convertToEntity(gpDto);

        return gpMapper.convertToDto(gpRepository.save(gp));
    }

    @Override
    public GeneralPractitionerDto updateDoctor(Long id, GeneralPractitionerDto gpDto) {
        GeneralPractitioner gp = gpRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + id));

        gp.setName(gpDto.getDoctor().getName());

        if (!gpDto.getDoctor().getSpecializationIds().isEmpty()) {
            gp.setSpecializations(gpDto.getDoctor().getSpecializationIds().stream()
                    .map(specializationId -> specializationRepository.findById(specializationId)
                            .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + specializationId)))
                    .collect(Collectors.toSet()));
        }

        if (!gpDto.getPatients().isEmpty()) {
            gp.setPatients(gpDto.getPatients().stream()
                    .map(patientId -> patientRepository.findById(patientId)
                            .orElseThrow(() -> new EntityNotFoundException("No Patient found with id: " + patientId)))
                    .collect(Collectors.toSet()));
        }

        return gpMapper.convertToDto(gpRepository.save(gp));
    }

    @Override
    public void deleteDoctor(Long id) {
        GeneralPractitioner gp = gpRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + id));

        gpRepository.delete(gp);
    }

    @Override
    public GeneralPractitionerDto addSpecialization(Long gpId, Long specializationId) {
        GeneralPractitioner gp = gpRepository.findById(gpId)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + gpId));

        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        gp.getSpecializations().add(specialization);

        return gpMapper.convertToDto(gpRepository.save(gp));
    }

    @Override
    public GeneralPractitionerDto removeSpecialization(Long gpId, Long specializationId) {
        GeneralPractitioner gp = gpRepository.findById(gpId)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + gpId));

        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        gp.getSpecializations().remove(specialization);

        return gpMapper.convertToDto(gpRepository.save(gp));
    }

    @Override
    public GeneralPractitionerDto addPatient(Long gpId, Long patientId) {
        GeneralPractitioner gp = gpRepository.findById(gpId)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + gpId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + patientId));

        gp.getPatients().add(patient);

        return gpMapper.convertToDto(gpRepository.save(gp));
    }

    @Override
    public GeneralPractitionerDto removePatient(Long gpId, Long patientId) {
        GeneralPractitioner gp = gpRepository.findById(gpId)
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + gpId));

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + patientId));

        gp.getPatients().remove(patient);

        return gpMapper.convertToDto(gpRepository.save(gp));
    }

    @Override
    public Set<GpPatientsCountDto> getAllGeneralPractitionersWithPatientCount() {
        return gpRepository.findAllDoctorsWithPatientCount();
    }

    @Override
    public GpPatientsCountDto getGeneralPractitionerWithPatientCount(Long gpId) {
        return gpRepository.findGeneralPractitionerWithPatientCount(gpId);
    }

}
