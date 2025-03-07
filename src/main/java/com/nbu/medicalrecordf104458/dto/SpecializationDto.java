package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotEmpty;
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
public class SpecializationDto {

    private Long id;

    @NotEmpty(message = "Specialization should have a name")
    private String name;

    private Set<Long> doctorIds = new HashSet<>();

}
