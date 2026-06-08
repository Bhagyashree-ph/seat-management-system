package com.iexceed.seatmanagement.branches.service.impl.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.seatmanagement.branches.controller.BranchController;
import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.request.UpdateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.enums.BranchStatus;
import com.iexceed.seatmanagement.branches.exceptions.BranchAlreadyExistsException;
import com.iexceed.seatmanagement.branches.exceptions.BranchNotFoundException;
import com.iexceed.seatmanagement.branches.service.BranchService;
import com.iexceed.seatmanagement.exceptions.GlobalExceptionHandler;
import com.iexceed.seatmanagement.security.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BranchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class BranchControllerTest {

    private static final String BRANCH_ID = "1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BranchService branchService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private CreateBranchRequest createRequest;
    private UpdateBranchRequest updateRequest;
    private BranchResponse branchResponse;

    @BeforeEach
    void setUp() {
        createRequest = CreateBranchRequest.builder()
                .branchCode("CHN")
                .branchName("Chennai")
                .city("Chennai")
                .address("OMR")
                .timezone("Asia/Kolkata")
                .status(BranchStatus.ACTIVE)
                .build();

        updateRequest = UpdateBranchRequest.builder()
                .branchName("Bangalore HQ")
                .city("Bangalore")
                .address("Koramangala")
                .timezone("Asia/Kolkata")
                .status(BranchStatus.ACTIVE)
                .build();

        branchResponse = BranchResponse.builder()
                .id(BRANCH_ID)
                .branchCode("CHN")
                .branchName("Chennai")
                .city("Chennai")
                .address("OMR")
                .status(BranchStatus.ACTIVE)
                .deleted(false)
                .build();
    }

    @Test
    void createBranch_success() throws Exception {
        when(branchService.createBranch(any(CreateBranchRequest.class)))
                .thenReturn(branchResponse);

        mockMvc.perform(post("/v1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Branch created successfully"))
                .andExpect(jsonPath("$.data.id").value(BRANCH_ID))
                .andExpect(jsonPath("$.data.branchCode").value("CHN"))
                .andExpect(jsonPath("$.data.branchName").value("Chennai"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));

        verify(branchService).createBranch(any(CreateBranchRequest.class));
    }

    @Test
    void createBranch_duplicate_returnsConflict() throws Exception {
        when(branchService.createBranch(any(CreateBranchRequest.class)))
                .thenThrow(new BranchAlreadyExistsException("Branch code already exists"));

        mockMvc.perform(post("/v1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Branch code already exists"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void createBranch_invalidRequest_returnsBadRequest() throws Exception {
        CreateBranchRequest invalidRequest = CreateBranchRequest.builder()
                .branchName("Chennai")
                .city("Chennai")
                .address("OMR")
                .status(BranchStatus.ACTIVE)
                .build();

        mockMvc.perform(post("/v1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("branchCode : must not be blank"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void getAllBranches_success() throws Exception {
        when(branchService.getAllBranches())
                .thenReturn(List.of(branchResponse));

        mockMvc.perform(get("/v1/branches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Branches fetched successfully"))
                .andExpect(jsonPath("$.data[0].id").value(BRANCH_ID))
                .andExpect(jsonPath("$.data[0].branchCode").value("CHN"));

        verify(branchService).getAllBranches();
    }

    @Test
    void getBranchById_success() throws Exception {
        when(branchService.getBranchById(BRANCH_ID))
                .thenReturn(branchResponse);

        mockMvc.perform(get("/v1/branches/{branchId}", BRANCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Branch fetched successfully"))
                .andExpect(jsonPath("$.data.id").value(BRANCH_ID))
                .andExpect(jsonPath("$.data.branchName").value("Chennai"));

        verify(branchService).getBranchById(BRANCH_ID);
    }

    @Test
    void getBranchById_notFound_returnsNotFound() throws Exception {
        when(branchService.getBranchById(BRANCH_ID))
                .thenThrow(new BranchNotFoundException("Branch not found with id: " + BRANCH_ID));

        mockMvc.perform(get("/v1/branches/{branchId}", BRANCH_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Branch not found with id: " + BRANCH_ID))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void deactivateBranch_success() throws Exception {
        mockMvc.perform(delete("/v1/branches/{branchId}", BRANCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Branch deactivated successfully"))
                .andExpect(jsonPath("$.data").doesNotExist());

        verify(branchService).deactivateBranch(BRANCH_ID);
    }

    @Test
    void deactivateBranch_notFound_returnsNotFound() throws Exception {
        doThrow(new BranchNotFoundException("Branch not found with id: " + BRANCH_ID))
                .when(branchService).deactivateBranch(BRANCH_ID);

        mockMvc.perform(delete("/v1/branches/{branchId}", BRANCH_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Branch not found with id: " + BRANCH_ID))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void updateBranch_success() throws Exception {
        BranchResponse updatedResponse = BranchResponse.builder()
                .id(BRANCH_ID)
                .branchCode("CHN")
                .branchName("Bangalore HQ")
                .city("Bangalore")
                .address("Koramangala")
                .status(BranchStatus.ACTIVE)
                .deleted(false)
                .build();

        when(branchService.updateBranch(eq(BRANCH_ID), any(UpdateBranchRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/v1/branches/{branchId}", BRANCH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Branch updated successfully"))
                .andExpect(jsonPath("$.data.id").value(BRANCH_ID))
                .andExpect(jsonPath("$.data.branchName").value("Bangalore HQ"))
                .andExpect(jsonPath("$.data.city").value("Bangalore"));

        verify(branchService).updateBranch(eq(BRANCH_ID), any(UpdateBranchRequest.class));
    }

    @Test
    void updateBranch_invalidRequest_returnsBadRequest() throws Exception {
        UpdateBranchRequest invalidRequest = UpdateBranchRequest.builder()
                .branchName("Bangalore HQ")
                .city("Bangalore")
                .address("Koramangala")
                .status(BranchStatus.ACTIVE)
                .build();

        mockMvc.perform(put("/v1/branches/{branchId}", BRANCH_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("timezone : must not be blank"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}

