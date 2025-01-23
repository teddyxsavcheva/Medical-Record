package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.mapper.DoctorAppointmentMapper;
import com.nbu.medicalrecordf104458.mapper.PatientMapper;
import com.nbu.medicalrecordf104458.model.Diagnose;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.DoctorRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import com.nbu.medicalrecordf104458.repository.TreatmentRepository;
import com.nbu.medicalrecordf104458.service.DoctorAppointmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DoctorAppointmentServiceImpl implements DoctorAppointmentService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final DiagnoseRepository diagnoseRepository;
    private final TreatmentRepository treatmentRepository;
    private final SickLeaveRepository sickLeaveRepository;
    private final DoctorAppointmentRepository appointmentRepository;
    private final DoctorAppointmentMapper mapper;
    private final PatientMapper patientMapper;

    @Override
    public Set<AppointmentDto> getAllAppointments() {
        return appointmentRepository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public AppointmentDto getAppointmentById(Long id) {
        DoctorAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + id));

        return mapper.convertToDto(appointment);
    }

    @Override
    public AppointmentDto createAppointment(AppointmentDto appointmentDto) {
        DoctorAppointment appointment = mapper.convertToEntity(appointmentDto);

        return mapper.convertToDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto updateAppointment(Long id, AppointmentDto appointmentDto) {
        DoctorAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + id));

        appointment.setVisitDate(appointmentDto.getVisitDate());

        appointment.setPatient(patientRepository.findById(appointmentDto.getPatientId())
                .orElseThrow(() -> new EntityNotFoundException("No Patient found with id: " + appointmentDto.getPatientId())));

        appointment.setDoctor(doctorRepository.findById(appointmentDto.getDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("No Doctor found with id: " + appointmentDto.getDoctorId())));

        appointment.setDiagnoses(appointmentDto.getDiagnoses().stream()
                .map(diagnoseId -> diagnoseRepository.findById(diagnoseId)
                        .orElseThrow(() -> new EntityNotFoundException("No Diagnose found with id: " + diagnoseId)))
                .collect(Collectors.toSet()));

        if (!appointmentDto.getTreatments().isEmpty()) {
            appointment.setTreatments(appointmentDto.getTreatments().stream()
                    .map(treatmentId -> treatmentRepository.findById(treatmentId)
                            .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + treatmentId)))
                    .collect(Collectors.toSet()));
        }

        if (appointmentDto.getSickLeaveId() != null) {
            appointment.setSickLeave(sickLeaveRepository.findById(appointmentDto.getSickLeaveId())
                    .orElseThrow(() -> new EntityNotFoundException("No Sick Leave found with id: " + appointmentDto.getSickLeaveId())));
        }

        return mapper.convertToDto(appointmentRepository.save(appointment));
    }

    @Override
    public void deleteAppointment(Long id) {
        DoctorAppointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + id));

        appointmentRepository.delete(appointment);
    }

    // Add/Remove methods for Treatment and Diagnose  many-to-many table

    @Override
    public AppointmentDto addDiagnose(Long appointmentId, Long diagnoseId) {
        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId));

        Diagnose diagnose = diagnoseRepository.findById(diagnoseId)
                .orElseThrow(() -> new EntityNotFoundException("No Diagnose found with id: " + diagnoseId));

        appointment.getDiagnoses().add(diagnose);

        return mapper.convertToDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto removeDiagnose(Long appointmentId, Long diagnoseId) {
        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId));

        Diagnose diagnose = diagnoseRepository.findById(diagnoseId)
                .orElseThrow(() -> new EntityNotFoundException("No Diagnose found with id: " + diagnoseId));

        appointment.getDiagnoses().remove(diagnose);

        return mapper.convertToDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto addTreatment(Long appointmentId, Long treatmentId) {
        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId));

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + treatmentId));

        appointment.getTreatments().add(treatment);

        return mapper.convertToDto(appointmentRepository.save(appointment));
    }

    @Override
    public AppointmentDto removeTreatment(Long appointmentId, Long treatmentId) {
        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId));

        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + treatmentId));

        appointment.getTreatments().remove(treatment);

        return mapper.convertToDto(appointmentRepository.save(appointment));
    }

    // Queries

    @Override
    public Set<PatientDto> getPatientsByDiagnoseId(Long diagnoseId) {
        return appointmentRepository.findPatientsByDiagnoseId(diagnoseId).stream()
                .map(patientMapper::convertToDto)
                .collect(Collectors.toSet());
    }

}
