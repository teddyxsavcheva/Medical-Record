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
//TODO: Think about using Builder pattern and checking for duplicate
// (if I have time left)
public class DiagnoseDto {

    private Long id;

    @NotEmpty(message = "Diagnose should have a name")
    private String name;

    @NotEmpty(message = "Diagnose should have a description")
    private String description;

    private Set<Long> appointmentIds = new HashSet<>();

}
