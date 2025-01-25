package com.nbu.medicalrecordf104458.dto.queries;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GpPatientsCountDto {

    private Long doctorId;

    private String doctorName;

    private Long patientCount;

}
