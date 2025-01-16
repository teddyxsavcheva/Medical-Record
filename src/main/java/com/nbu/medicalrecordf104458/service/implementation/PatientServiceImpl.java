package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.PatientDto;
import com.nbu.medicalrecordf104458.mapper.PatientMapper;
import com.nbu.medicalrecordf104458.model.GeneralPractitioner;
import com.nbu.medicalrecordf104458.model.Patient;
import com.nbu.medicalrecordf104458.repository.GeneralPractitionerRepository;
import com.nbu.medicalrecordf104458.repository.PatientRepository;
import com.nbu.medicalrecordf104458.service.PatientService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PatientServiceImpl implements PatientService {

    private final PatientMapper mapper;
    private final PatientRepository patientRepository;
    private final GeneralPractitionerRepository gpRepository;

    @Override
    public List<PatientDto> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();

        return patients.stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toList());
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

}
