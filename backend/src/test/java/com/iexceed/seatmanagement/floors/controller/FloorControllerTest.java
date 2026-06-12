package com.iexceed.seatmanagement.floors.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.enums.FloorStatus;
import com.iexceed.seatmanagement.floors.service.FloorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FloorController.class)
class FloorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private FloorService floorService;

    private FloorResponse floorResponse() {
        return FloorResponse.builder()
                .id("floor-1")
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();
    }

    @Test
    @WithMockUser(roles = {"HR_ADMIN"})
    void createFloor_success() throws Exception {

        CreateFloorRequest request = CreateFloorRequest.builder()
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();

        when(floorService.createFloor(any()))
                .thenReturn(floorResponse());

        mockMvc.perform(post("/v1/floors")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(floorService).createFloor(any());
    }

    @Test
    @WithMockUser
    void getAllFloors_success() throws Exception {

        when(floorService.getAllFloors())
                .thenReturn(List.of(floorResponse()));

        mockMvc.perform(get("/v1/floors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(floorService).getAllFloors();
    }

    @Test
    @WithMockUser
    void getFloorById_success() throws Exception {

        when(floorService.getFloorById("floor-1"))
                .thenReturn(floorResponse());

        mockMvc.perform(get("/v1/floors/floor-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(floorService).getFloorById("floor-1");
    }

    @Test
    @WithMockUser(roles = {"HR_ADMIN"})
    void updateFloor_success() throws Exception {

        UpdateFloorRequest request = UpdateFloorRequest.builder()
                .floorCode("F2")
                .floorName("Floor 2")
                .totalSeats(200)
                .status(FloorStatus.INACTIVE)
                .build();

        when(floorService.updateFloor(eq("floor-1"), any()))
                .thenReturn(floorResponse());

        mockMvc.perform(put("/v1/floors/floor-1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(floorService).updateFloor(eq("floor-1"), any());
    }

    @Test
    @WithMockUser(roles = {"HR_ADMIN"})
    void deleteFloor_success() throws Exception {

        doNothing().when(floorService).deleteFloor("floor-1");

        mockMvc.perform(delete("/v1/floors/floor-1"))
                .andExpect(status().isOk());

        verify(floorService).deleteFloor("floor-1");
    }

    @Test
    void createFloor_unauthorized() throws Exception {

        mockMvc.perform(post("/v1/floors")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}