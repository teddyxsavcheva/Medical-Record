package com.nbu.medicalrecordf104458.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbu.medicalrecordf104458.config.JwtAuthFilter;
import com.nbu.medicalrecordf104458.dto.SickLeaveDto;
import com.nbu.medicalrecordf104458.service.SickLeaveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SickLeaveController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
@MockBean(JwtAuthFilter.class) // Mocks out JWT filter so it's not used in tests
public class SickLeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SickLeaveService sickLeaveService;

    private SickLeaveDto sickLeaveDto;

    @BeforeEach
    public void setup() {
        sickLeaveDto = new SickLeaveDto(1L, LocalDate.parse("2025-01-01"),
                LocalDate.parse("2025-01-10"), 123L);
    }

    @Test
    public void testGetAllSickLeaves() throws Exception {
        Set<SickLeaveDto> sickLeaves = new HashSet<>();
        sickLeaves.add(sickLeaveDto);
        when(sickLeaveService.getAllSickLeaves()).thenReturn(sickLeaves);

        mockMvc.perform(get("/sick-leaves/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    public void testGetSickLeaveById() throws Exception {
        Long sickLeaveId = 1L;

        when(sickLeaveService.getSickLeaveById(sickLeaveId)).thenReturn(sickLeaveDto);

        mockMvc.perform(get("/sick-leaves/{id}", sickLeaveId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sickLeaveId));
    }

    @Test
    public void testCreateSickLeave() throws Exception {
        when(sickLeaveService.createSickLeave(any(SickLeaveDto.class))).thenReturn(sickLeaveDto);

        mockMvc.perform(post("/sick-leaves/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sickLeaveDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testUpdateSickLeave() throws Exception {
        Long sickLeaveId = 1L;

        when(sickLeaveService.updateSickLeave(eq(sickLeaveId), any(SickLeaveDto.class))).thenReturn(sickLeaveDto);

        mockMvc.perform(put("/sick-leaves/{id}", sickLeaveId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sickLeaveDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sickLeaveId));
    }

    @Test
    public void testDeleteSickLeave() throws Exception {
        Long sickLeaveId = 1L;

        mockMvc.perform(delete("/sick-leaves/{id}", sickLeaveId))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetMostCommonSickLeaveMonth() throws Exception {
        String mostCommonMonth = "2025-01";

        when(sickLeaveService.getMonthWithMostSickLeaves()).thenReturn(mostCommonMonth);

        mockMvc.perform(get("/sick-leaves/most-common-sick-leave-month"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(mostCommonMonth));
    }
}
