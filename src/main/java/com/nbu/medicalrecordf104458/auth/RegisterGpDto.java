package com.nbu.medicalrecordf104458.auth;

import com.nbu.medicalrecordf104458.dto.DoctorDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterGpDto {

    @NotNull(message = "GP information is required for GP")
    private DoctorDto doctor;

    private Set<Long> patients = new HashSet<>();

    @NotEmpty(message = "GP should have an email")
    private String email;

    @NotEmpty(message = "GP should have a password")
    private String password;

}
