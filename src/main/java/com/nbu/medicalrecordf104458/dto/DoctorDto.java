package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDto {

    private Long id;

    @NotEmpty(message = "Doctor should have a name")
    private String name;

    @NotEmpty(message = "Doctor should have at least one specialization")
    private Set<Long> specializationIds;

    private Long userId;

}
