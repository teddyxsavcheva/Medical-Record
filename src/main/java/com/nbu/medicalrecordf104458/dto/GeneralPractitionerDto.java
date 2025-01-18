package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeneralPractitionerDto {

    private Long id;

    @NotNull(message = "Doctor information is required for GP")
    private DoctorDto doctor;

    private List<Long> patients = new ArrayList<>();

}
