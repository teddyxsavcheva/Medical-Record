package com.nbu.medicalrecordf104458.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDoctorDto {

    @NotEmpty(message = "Doctor should have a name")
    private String name;

    @NotEmpty(message = "Doctor should have at least one specialization")
    private Set<Long> specializationIds;

    @NotEmpty(message = "Doctor should have an email")
    private String email;

    @NotEmpty(message = "Doctor should have a password")
    private String password;

}
