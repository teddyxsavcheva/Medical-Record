package com.nbu.medicalrecordf104458.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterPatientDto {

    @NotEmpty(message = "Patient should have a name")
    private String name;

    @NotNull(message = "Patient should have an unified civil number")
    private Long unifiedCivilNumber;

    @NotNull(message = "Patient should have the last insurance payment date")
    private LocalDate lastInsurancePayment;

    @NotNull(message = "Patient should have a family doctor")
    private Long familyDoctorId;

    @NotEmpty(message = "Doctor should have an email")
    private String email;

    @NotEmpty(message = "Doctor should have a password")
    private String password;

}
