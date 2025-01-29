package com.nbu.medicalrecordf104458.auth;
import com.nbu.medicalrecordf104458.model.user.Role;

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
public class UserDto {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private Role role;

    private Long doctorId;

    private Long patientId;

}
