package com.nbu.medicalrecordf104458.service.implementation;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.exceptionhandler.exceptions.SickLeaveAlreadyExistsException;
import com.nbu.medicalrecordf104458.mapper.SickLeaveMapper;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import com.nbu.medicalrecordf104458.repository.SickLeaveRepository;
import com.nbu.medicalrecordf104458.service.SickLeaveService;
import com.nbu.medicalrecordf104458.utility.DateValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SickLeaveServiceImpl implements SickLeaveService {

    private final DoctorAppointmentRepository appointmentRepository;
    private final SickLeaveMapper mapper;
    private final SickLeaveRepository repository;

    @Override
    public Set<SickLeaveDto> getAllSickLeaves() {
        return repository.findAll().stream()
                .map(mapper::convertToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public SickLeaveDto getSickLeaveById(Long id) {
        SickLeave sickLeave = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + id));

        return mapper.convertToDto(sickLeave);
    }

    @Override
    public SickLeaveDto createSickLeave(SickLeaveDto sickLeaveDto) {
        DateValidator.validateDateRange(sickLeaveDto.getStartDate(), sickLeaveDto.getEndDate());

        SickLeave sickLeave = mapper.convertToEntity(sickLeaveDto);

        if (sickLeave.getDoctorAppointment().getSickLeave() != null) {
            throw new SickLeaveAlreadyExistsException("The appointment already has a sick leave associated with it.");
        }

        sickLeave.getDoctorAppointment().setSickLeave(sickLeave);

        return mapper.convertToDto(repository.save(sickLeave));
    }

    @Override
    public SickLeaveDto updateSickLeave(Long id, SickLeaveDto sickLeaveDto) {
        DateValidator.validateDateRange(sickLeaveDto.getStartDate(), sickLeaveDto.getEndDate());

        SickLeave sickLeave = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + id));

        sickLeave.setStartDate(sickLeaveDto.getStartDate());
        sickLeave.setEndDate(sickLeaveDto.getEndDate());

        DoctorAppointment appointment = appointmentRepository.findById(sickLeaveDto.getDoctorAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + sickLeaveDto.getDoctorAppointmentId()));

        if (appointment.getSickLeave() != null && !appointment.getSickLeave().getId().equals(id)) {
            throw new SickLeaveAlreadyExistsException("The appointment is already associated with another sick leave.");
        }

        sickLeave.setDoctorAppointment(appointment);

        return mapper.convertToDto(repository.save(sickLeave));
    }

    @Override
    public void deleteSickLeave(Long id) {
        SickLeave sickLeave = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No sick leave found with id: " + id));

        // So that we can delete them when they are already connected to an appointment
        sickLeave.getDoctorAppointment().setSickLeave(null);
        repository.delete(sickLeave);
    }

    // Queries
    @Override
    public String getMonthWithMostSickLeaves() {
        // Извличане на всички болнични
        Set<SickLeave> sickLeaves = new HashSet<>(repository.findAll());

        // Групиране по година и месец и преброяване
        Map<YearMonth, Long> monthCounts = sickLeaves.stream()
                .collect(Collectors.groupingBy(
                        sickLeave -> YearMonth.from(sickLeave.getStartDate()), // Използваме YearMonth за по-ясен код
                        Collectors.counting()
                ));

        // Намиране на месеца с най-много болнични
        return monthCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> String.format(
                        "The most sick leaves were issued in %s %d, with a total of %d sick leaves.",
                        entry.getKey().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault()),
                        entry.getKey().getYear(),
                        entry.getValue()
                ))
                .orElse("There are no records of sick leaves.");
    }

}
