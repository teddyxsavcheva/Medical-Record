package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.mapper.DiagnoseMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.service.DiagnoseService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DiagnoseServiceImpl implements DiagnoseService {

    private final DiagnoseRepository diagnoseRepository;
    private final DoctorAppointmentRepository appointmentRepository;
    private final DiagnoseMapper mapper;

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public Set<DiagnoseDto> getAllDiagnoses() {
        return diagnoseRepository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public DiagnoseDto getDiagnoseById(Long id) {
        Diagnose diagnose = diagnoseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + id));

        return mapper.convertToDto(diagnose);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public DiagnoseDto createDiagnose(DiagnoseDto diagnoseDto) {
        Diagnose diagnose = mapper.convertToEntity(diagnoseDto);

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public DiagnoseDto updateDiagnose(Long id, DiagnoseDto diagnoseDto) {
        Diagnose diagnose = diagnoseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + id));

        diagnose.setName(diagnoseDto.getName());
        diagnose.setDescription(diagnoseDto.getDescription());

        if (!diagnoseDto.getAppointmentIds().isEmpty()) {
            diagnose.setAppointments(diagnoseDto.getAppointmentIds().stream()
                    .map(appointmentId -> appointmentRepository.findById(appointmentId)
                            .orElseThrow(() -> new EntityNotFoundException("No appointment with id: " + appointmentId)))
                    .collect(Collectors.toSet()));
        }

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteDiagnose(Long id) {
        Diagnose diagnose = diagnoseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + id));

        diagnoseRepository.delete(diagnose);
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public DiagnoseDto addAppointment(Long diagnoseId, Long appointmentId) {
        Diagnose diagnose = diagnoseRepository.findById(diagnoseId)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + diagnoseId));

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No appointment found with id: " + appointmentId));

        diagnose.getAppointments().add(appointment);

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    @Override
    @PreAuthorize("hasAuthority('ADMIN')")
    public DiagnoseDto removeAppointment(Long diagnoseId, Long appointmentId) {
        Diagnose diagnose = diagnoseRepository.findById(diagnoseId)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + diagnoseId));

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No appointment found with id: " + appointmentId));

        diagnose.getAppointments().remove(appointment);

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    // Queries
    @Override
    @PreAuthorize("hasAnyAuthority('ADMIN', 'DOCTOR')")
    public Set<DiagnoseDto> findMostCommonDiagnoses() {
        Set<Diagnose> diagnoses = new HashSet<>(diagnoseRepository.findAll());

        Map<Long, Integer> diagnoseCountMap = new HashMap<>();
        int maxCount = 0;

        for (Diagnose diagnose : diagnoses) {
            int count = diagnose.getAppointments().size();
            diagnoseCountMap.put(diagnose.getId(), diagnoseCountMap.getOrDefault(diagnose.getId(), 0) + count);

            maxCount = Math.max(maxCount, diagnoseCountMap.get(diagnose.getId()));
        }

        Set<DiagnoseDto> mostCommon = new HashSet<>();

        for (Diagnose diagnose : diagnoses) {
            if (diagnoseCountMap.get(diagnose.getId()) == maxCount) {
                DiagnoseDto dto = new DiagnoseDto(
                        diagnose.getId(),
                        diagnose.getName(),
                        diagnose.getDescription(),
                        diagnose.getAppointments().stream().map(DoctorAppointment::getId).collect(Collectors.toSet())
                );
                mostCommon.add(dto);
            }
        }

        return mostCommon;
    }

}
