package com.nbu.medicalrecordf104458.dto.auth;
import com.nbu.medicalrecordf104458.model.user.Role;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

    @NotEmpty(message = "User should have an email")
    private String email;

    @NotEmpty(message = "User should have a password")
    private String password;

    @NotNull(message = "User should have an authentication role")
    private Role role;

}
