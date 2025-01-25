package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.queries.DoctorAppointmentsCountDto;
import com.nbu.medicalrecordf104458.dto.DoctorDto;
import com.nbu.medicalrecordf104458.mapper.DoctorMapper;
import com.nbu.medicalrecordf104458.model.Doctor;
import com.nbu.medicalrecordf104458.model.Specialization;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.SpecializationRepository;
import com.nbu.medicalrecordf104458.service.DoctorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorMapper mapper;
    private final DoctorRepository doctorRepository;
    private final SpecializationRepository specializationRepository;

    @Override
    public Set<DoctorDto> getAllDoctors() {
        return doctorRepository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public DoctorDto getDoctorById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + id));

        return mapper.convertToDto(doctor);
    }

    @Override
    public DoctorDto createDoctor(DoctorDto doctorDto) {
        Doctor doctor = mapper.convertToEntity(doctorDto);

        return mapper.convertToDto(doctorRepository.save(doctor));
    }

    @Override
    public DoctorDto updateDoctor(Long id, DoctorDto doctorDto) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + id));

        doctor.setName(doctorDto.getName());

        if (!doctorDto.getSpecializationIds().isEmpty()) {
            doctor.setSpecializations(doctorDto.getSpecializationIds().stream()
                    .map(specializationId -> specializationRepository.findById(specializationId)
                            .orElseThrow(() -> new EntityNotFoundException("No Specialization found with id: " + specializationId)))
                    .collect(Collectors.toSet()));
        }

        return mapper.convertToDto(doctorRepository.save(doctor));
    }

    @Override
    public void deleteDoctor(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + id));

        doctorRepository.delete(doctor);
    }

    @Override
    public DoctorDto addSpecialization(Long doctorId, Long specializationId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + doctorId));

        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        doctor.getSpecializations().add(specialization);

        return mapper.convertToDto(doctorRepository.save(doctor));
    }

    @Override
    public DoctorDto removeSpecialization(Long doctorId, Long specializationId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("No doctor found with id: " + doctorId));

        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(() -> new EntityNotFoundException("No specialization found with id: " + specializationId));

        doctor.getSpecializations().remove(specialization);

        return mapper.convertToDto(doctorRepository.save(doctor));
    }

    // Queries
    @Override
    public Set<DoctorAppointmentsCountDto> getAllDoctorsWithAppointmentCount() {
        return doctorRepository.findAllDoctorsWithAppointmentCount();
    }

    @Override
    public DoctorAppointmentsCountDto getDoctorWithAppointmentCount(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            throw new EntityNotFoundException("No Doctor with id: " + doctorId);
        }

        return doctorRepository.findDoctorWithAppointmentCount(doctorId);
    }

    @Override
    public Set<DoctorDto> findDoctorsWithMostSickLeaves() {
        Set<Doctor> doctors = new HashSet<>(doctorRepository.findAll());

        Map<Long, Integer> doctorSickLeaveCountMap = new HashMap<>();
        int maxSickLeaveCount = 0;

        for (Doctor doctor : doctors) {
            int sickLeaveCount = (int) doctor.getAppointments().stream()
                    .filter(appointment -> appointment.getSickLeave() != null)
                    .count();

            doctorSickLeaveCountMap.put(doctor.getId(), sickLeaveCount);

            maxSickLeaveCount = Math.max(maxSickLeaveCount, sickLeaveCount);
        }

        Set<DoctorDto> doctorsWithMostSickLeaves = new HashSet<>();
        for (Map.Entry<Long, Integer> entry : doctorSickLeaveCountMap.entrySet()) {
            if (entry.getValue() == maxSickLeaveCount) {
                Doctor doctor = doctorRepository.findById(entry.getKey())
                        .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));

                DoctorDto dto = new DoctorDto(
                        doctor.getId(),
                        doctor.getName(),
                        doctor.getSpecializations().stream()
                                .map(Specialization::getId)
                                .collect(Collectors.toSet())
                );
                doctorsWithMostSickLeaves.add(dto);
            }
        }

        return doctorsWithMostSickLeaves;
    }

}
