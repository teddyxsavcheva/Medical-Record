package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.TreatmentDto;
import com.nbu.medicalrecordf104458.mapper.TreatmentMapper;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.Treatment;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.TreatmentRepository;
import com.nbu.medicalrecordf104458.service.TreatmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TreatmentServiceImpl implements TreatmentService {

    private final TreatmentMapper mapper;
    private final TreatmentRepository treatmentRepository;
    private final DoctorAppointmentRepository appointmentRepository;

    @Override
    public Set<TreatmentDto> getAllTreatments() {
        return treatmentRepository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public TreatmentDto getTreatmentById(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + id));

        return mapper.convertToDto(treatment);
    }

    @Override
    public TreatmentDto createTreatment(TreatmentDto treatmentDto) {
        Treatment treatment = mapper.convertToEntity(treatmentDto);

        return mapper.convertToDto(treatmentRepository.save(treatment));
    }

    @Override
    public TreatmentDto updateTreatment(Long id, TreatmentDto treatmentDto) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + id));

        treatment.setMedicineName(treatmentDto.getMedicineName());
        treatment.setDosageAmount(treatmentDto.getDosageAmount());
        treatment.setFrequency(treatment.getFrequency());

        if (!treatmentDto.getAppointmentIds().isEmpty()) {
            treatment.setAppointments(treatmentDto.getAppointmentIds().stream()
                    .map(appointmentId -> appointmentRepository.findById(appointmentId)
                            .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId)))
                    .collect(Collectors.toSet()));
        }

        return mapper.convertToDto(treatmentRepository.save(treatment));
    }

    @Override
    public void deleteTreatment(Long id) {
        Treatment treatment = treatmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + id));

        treatmentRepository.delete(treatment);
    }

    @Override
    public TreatmentDto addAppointment(Long treatmentId, Long appointmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + treatmentId));

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId));

        treatment.getAppointments().add(appointment);

        return mapper.convertToDto(treatmentRepository.save(treatment));
    }

    @Override
    public TreatmentDto removeAppointment(Long treatmentId, Long appointmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Treatment found with id: " + treatmentId));

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + appointmentId));

        treatment.getAppointments().remove(appointment);

        return mapper.convertToDto(treatmentRepository.save(treatment));
    }

}
