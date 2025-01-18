package com.nbu.medicalrecordf104458.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SickLeaveDto {

    private Long id;

    @NotNull(message = "Start date of sick leave cannot be null")
    private LocalDate startDate;

    @NotNull(message = "End date of sick leave cannot be null")
    private LocalDate endDate;

}
