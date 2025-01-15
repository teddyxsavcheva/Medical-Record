package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiagnoseDto {

    private Long id;

    @NotEmpty(message = "Diagnose should have a name")
    private String name;

    @NotEmpty(message = "Diagnose should have a description")
    private String description;

    private List<Long> appointmentIds;

}
