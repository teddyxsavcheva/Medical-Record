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
public class GeneralPractitionerDto {

    @NotNull(message = "Doctor information is required for GP")
    private DoctorDto doctor;

    private Set<Long> patients = new HashSet<>();

}
