package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.model.DoctorAppointment;
import com.nbu.medicalrecordf104458.model.SickLeave;
import com.nbu.medicalrecordf104458.repository.DoctorAppointmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SickLeaveMapper {

    private final DoctorAppointmentRepository appointmentRepository;

    public SickLeaveDto convertToDto(SickLeave sickLeave) {
        SickLeaveDto sickLeaveDto = new SickLeaveDto();

        sickLeaveDto.setId(sickLeave.getId());
        sickLeaveDto.setStartDate(sickLeave.getStartDate());
        sickLeaveDto.setEndDate(sickLeave.getEndDate());
        sickLeaveDto.setDoctorAppointmentId(sickLeave.getDoctorAppointment().getId());

        return sickLeaveDto;
    }

    public SickLeave convertToEntity(SickLeaveDto sickLeaveDto) {
        SickLeave sickLeave = new SickLeave();

        sickLeave.setStartDate(sickLeaveDto.getStartDate());
        sickLeave.setEndDate(sickLeaveDto.getEndDate());

        DoctorAppointment appointment = appointmentRepository.findById(sickLeaveDto.getDoctorAppointmentId())
                .orElseThrow(() -> new EntityNotFoundException("No Appointment found with id: " + sickLeaveDto.getDoctorAppointmentId() ));

        sickLeave.setDoctorAppointment(appointment);

        return sickLeave;
    }

}
