package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.AppointmentDto;
import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.mapper.DoctorAppointmentMapper;
import com.nbu.medicalrecordf104458.mapper.PatientMapper;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.repository.DiagnoseRepository;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PatientServiceImpl implements PatientService {

    private final PatientMapper mapper;
    private final DoctorAppointmentMapper appointmentMapper;
    private final PatientRepository patientRepository;
    private final GeneralPractitionerRepository gpRepository;
    private final DiagnoseRepository diagnoseRepository;

    @Override
    public Set<PatientDto> getAllPatients() {

        return patientRepository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public PatientDto getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + id));

        return mapper.convertToDto(patient);
    }

    @Override
    public PatientDto createPatient(PatientDto patientDto) {
        Patient patient = mapper.convertToEntity(patientDto);

        return mapper.convertToDto(patientRepository.save(patient));
    }

    @Override
    public PatientDto updatePatient(Long id, PatientDto patientDto) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + id));

        patient.setName(patientDto.getName());
        patient.setUnifiedCivilNumber(patientDto.getUnifiedCivilNumber());
        patient.setLastInsurancePayment(patientDto.getLastInsurancePayment());

        GeneralPractitioner gp = gpRepository.findById(patientDto.getFamilyDoctorId())
                .orElseThrow(() -> new EntityNotFoundException("No GP found with id: " + patientDto.getFamilyDoctorId()));

        patient.setFamilyDoctor(gp);

        return mapper.convertToDto(patientRepository.save(patient));
    }

    @Override
    public void deletePatient(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + id));

        patientRepository.delete(patient);
    }

    @Override
    public boolean isInsurancePaidLast6Months(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new EntityNotFoundException("No patient found with id: " + patientId));

        LocalDate currentDate = LocalDate.now();
        LocalDate lastPaymentDate = patient.getLastInsurancePayment();

        long monthsBetween = ChronoUnit.MONTHS.between(lastPaymentDate, currentDate);

        return monthsBetween <= 6;
    }

    // Queries

    @Override
    public Set<PatientDto> getPatientsByDiagnoseId(Long diagnoseId) {
        if (!diagnoseRepository.existsById(diagnoseId)) {
            throw new EntityNotFoundException("No Diagnose with id: " + diagnoseId);
        }

        return patientRepository.findPatientsByDiagnoseId(diagnoseId).stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PatientDto> getPatientsByGeneralPractitioner(Long gpId) {
        if (!gpRepository.existsById(gpId)) {
            throw new EntityNotFoundException("No GP with id: " + gpId);
        }

        return patientRepository.findPatientsByGeneralPractitionerId(gpId).stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<AppointmentDto> getVisitsByPatient(Long patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new EntityNotFoundException("No Patient found with id: " + patientId);
        }

        return patientRepository.findVisitsByPatientId(patientId).stream()
                .map(appointmentMapper::convertToDto)
                .collect(Collectors.toSet());
    }

}
