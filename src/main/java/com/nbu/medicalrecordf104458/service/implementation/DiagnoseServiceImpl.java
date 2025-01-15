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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DiagnoseServiceImpl implements DiagnoseService {

    private final DiagnoseRepository diagnoseRepository;
    private final DoctorAppointmentRepository appointmentRepository;
    private final DiagnoseMapper mapper;

    @Override
    public List<DiagnoseDto> getAllDiagnoses() {
        List<Diagnose> diagnoses = diagnoseRepository.findAll();

        return diagnoses.stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public DiagnoseDto getDiagnoseById(Long id) {
        Diagnose diagnose = diagnoseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + id));

        return mapper.convertToDto(diagnose);
    }

    @Override
    public DiagnoseDto createDiagnose(DiagnoseDto diagnoseDto) {
        Diagnose diagnose = mapper.convertToEntity(diagnoseDto);

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    @Override
    public DiagnoseDto updateDiagnose(Long id, DiagnoseDto diagnoseDto) {
        Diagnose diagnose = diagnoseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + id));

        diagnose.setName(diagnoseDto.getName());
        diagnose.setDescription(diagnoseDto.getDescription());

        if (!diagnoseDto.getAppointmentIds().isEmpty()) {
            diagnose.setAppointments(diagnoseDto.getAppointmentIds().stream()
                    .map(appointmentId -> appointmentRepository.findById(appointmentId)
                            .orElseThrow(() -> new EntityNotFoundException("No appointment with id: " + appointmentId)))
                    .collect(Collectors.toList()));
        }

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    @Override
    public void deleteDiagnose(Long id) {
        Diagnose diagnose = diagnoseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + id));

        diagnoseRepository.delete(diagnose);
    }

    @Override
    public DiagnoseDto addAppointment(Long diagnoseId, Long appointmentId) {
        Diagnose diagnose = diagnoseRepository.findById(diagnoseId)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + diagnoseId));

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No appointment found with id: " + appointmentId));

        diagnose.getAppointments().add(appointment);

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

    @Override
    public DiagnoseDto removeAppointment(Long diagnoseId, Long appointmentId) {
        Diagnose diagnose = diagnoseRepository.findById(diagnoseId)
                .orElseThrow(() -> new EntityNotFoundException("No diagnose found with id: " + diagnoseId));

        DoctorAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("No appointment found with id: " + appointmentId));

        diagnose.getAppointments().remove(appointment);

        return mapper.convertToDto(diagnoseRepository.save(diagnose));
    }

}
