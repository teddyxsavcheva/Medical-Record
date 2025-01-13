package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.DiagnoseDto;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class DiagnoseMapper {

    private final DoctorAppointmentRepository appointmentRepository;

    public DiagnoseDto convertToDto(Diagnose diagnose) {

        DiagnoseDto dto = new DiagnoseDto();

        dto.setId(diagnose.getId());
        dto.setName(diagnose.getName());
        dto.setDescription(dto.getDescription());

        if (!diagnose.getAppointments().isEmpty()) {
            dto.setAppointmentIds(diagnose.getAppointments().stream()
                    .map(DoctorAppointment::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Diagnose convertToEntity(DiagnoseDto dto) {

        Diagnose entity = new Diagnose();

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());

        if (!dto.getAppointmentIds().isEmpty()) {
            entity.setAppointments(dto.getAppointmentIds().stream()
                    .map(id -> appointmentRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No Diagnose found with id: " + id)))
                    .collect(Collectors.toList()));
        }

        return entity;
    }
}
