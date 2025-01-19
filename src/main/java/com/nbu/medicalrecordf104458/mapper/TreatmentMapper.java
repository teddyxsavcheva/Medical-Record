package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.TreatmentDto;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class TreatmentMapper {

    private final DoctorAppointmentRepository appointmentRepository;

    public TreatmentDto convertToDto(Treatment treatment) {
        TreatmentDto dto = new TreatmentDto();

        dto.setId(treatment.getId());
        dto.setMedicineName(treatment.getMedicineName());
        dto.setDosageAmount(treatment.getDosageAmount());
        dto.setFrequency(treatment.getFrequency());

        if (!treatment.getAppointments().isEmpty()) {
            dto.setAppointmentIds(treatment.getAppointments().stream()
                    .map(DoctorAppointment::getId)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Treatment convertToEntity(TreatmentDto dto) {
        Treatment treatment = new Treatment();

        treatment.setMedicineName(dto.getMedicineName());
        treatment.setDosageAmount(dto.getDosageAmount());
        treatment.setFrequency(dto.getFrequency());

        if (!dto.getAppointmentIds().isEmpty()) {
            treatment.setAppointments(dto.getAppointmentIds().stream()
                    .map(id -> appointmentRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No appointment found with id: " + id)))
                    .collect(Collectors.toList()));
        }

        return treatment;
    }

}
