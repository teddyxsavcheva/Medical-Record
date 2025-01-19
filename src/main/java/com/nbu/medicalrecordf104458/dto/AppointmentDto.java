package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDto {

    private Long id;

    @NotNull(message = "Visit date of doctor appointment cannot be null")
    private LocalDate visitDate;

    @NotNull(message = "An appointment must have a patient")
    private Long patientId;

    @NotNull(message = "An appointment must have a doctor")
    private Long doctorId;

    @NotEmpty(message = "There must be at least one diagnose for this appointment")
    private List<Long> diagnoses;

    private List<Long> treatments = new ArrayList<>();

    private Long sickLeaveId;

}
