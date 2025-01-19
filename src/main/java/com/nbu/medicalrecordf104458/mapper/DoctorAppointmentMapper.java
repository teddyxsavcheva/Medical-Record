package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import com.nbu.medicalrecordf104458.repository.TreatmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class DoctorAppointmentMapper {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final TreatmentRepository treatmentRepository;
    private final SickLeaveRepository sickLeaveRepository;

    public AppointmentDto convertToDto(DoctorAppointment appointment) {
        AppointmentDto dto = new AppointmentDto();

        dto.setId(appointment.getId());
        dto.setVisitDate(appointment.getVisitDate());
        dto.setDoctorId(appointment.getDoctor().getId());
        dto.setPatientId(appointment.getPatient().getId());

        dto.setDiagnoses(appointment.getDiagnoses().stream()
                .map(Diagnose::getId)
                .collect(Collectors.toSet()));

        if (appointment.getSickLeave() != null) {
            dto.setSickLeaveId(appointment.getSickLeave().getId());
        }

        if (!appointment.getTreatments().isEmpty()) {
            dto.setTreatments(appointment.getTreatments().stream()
                    .map(Treatment::getId)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public DoctorAppointment convertToEntity(AppointmentDto dto) {
        DoctorAppointment appointment = new DoctorAppointment();

        appointment.setVisitDate(dto.getVisitDate());

        appointment.setDoctor(doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("No Doctor found with id: " + dto.getDoctorId())));

        appointment.setPatient(patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("No Patient found with id: " + dto.getPatientId())));

        appointment.setDiagnoses(dto.getDiagnoses().stream()
                .map(id -> diagnoseRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("No Diagnose found with id: " + id)))
                .collect(Collectors.toSet()));

        if (dto.getSickLeaveId() != null) {
            appointment.setSickLeave(sickLeaveRepository.findById(dto.getSickLeaveId())
                    .orElseThrow(() -> new EntityNotFoundException("No Sick Leave found with id: " + dto.getSickLeaveId())));
        }

        if (!dto.getTreatments().isEmpty()) {
            appointment.setTreatments(dto.getTreatments().stream()
                    .map(id -> treatmentRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + id)))
                    .collect(Collectors.toSet()));
        }

        return appointment;
    }

}
