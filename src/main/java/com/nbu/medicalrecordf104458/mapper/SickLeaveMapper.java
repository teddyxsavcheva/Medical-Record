package com.nbu.medicalrecordf104458.mapper;

import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.model.SickLeave;
import org.springframework.stereotype.Component;

@Component
public class SickLeaveMapper {

    public SickLeaveDto convertToDto(SickLeave sickLeave) {
        SickLeaveDto sickLeaveDto = new SickLeaveDto();

        sickLeaveDto.setId(sickLeave.getId());
        sickLeaveDto.setStartDate(sickLeave.getStartDate());
        sickLeaveDto.setEndDate(sickLeave.getEndDate());

        return sickLeaveDto;
    }

    public SickLeave convertToEntity(SickLeaveDto sickLeaveDto) {
        SickLeave sickLeave = new SickLeave();

        sickLeave.setStartDate(sickLeaveDto.getStartDate());
        sickLeave.setEndDate(sickLeaveDto.getEndDate());

        return sickLeave;
    }

}
