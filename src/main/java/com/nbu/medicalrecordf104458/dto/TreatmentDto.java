package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TreatmentDto {

    private Long id;

    @NotNull(message = "Medicine name cannot be null")
    private String medicineName;

    @NotNull(message = "Dosage amount cannot be null")
    private String dosageAmount;

    @NotNull(message = "Frequency cannot be null")
    private String frequency;

    private Set<Long> appointmentIds = new HashSet<>();


}
