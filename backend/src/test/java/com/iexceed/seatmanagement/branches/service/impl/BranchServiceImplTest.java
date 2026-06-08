package com.iexceed.seatmanagement.branches.service.impl;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.request.UpdateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.entity.Branch;
import com.iexceed.seatmanagement.branches.enums.BranchStatus;
import com.iexceed.seatmanagement.branches.exceptions.BranchAlreadyExistsException;
import com.iexceed.seatmanagement.branches.exceptions.BranchNotFoundException;
import com.iexceed.seatmanagement.branches.repository.BranchRepository;
import com.iexceed.seatmanagement.branches.service.BranchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchServiceImpl branchServiceImpl;

    private Branch branch;
    private CreateBranchRequest createRequest;
    private UpdateBranchRequest updateRequest;

    @BeforeEach
    void setUp() {

        branch = Branch.builder()
                .id("1")
                .branchCode("CHN")
                .branchName("Chennai")
                .city("Chennai")
                .address("OMR")
                .timezone("Asia/Kolkata")
                .status(BranchStatus.ACTIVE)
                .build();

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
    }

    @Test
    void createBranch_success() {
        when(branchRepository.existsByBranchCodeAndDeletedFalse("CHN"))
                .thenReturn(false);

        when(branchRepository.existsByBranchNameAndDeletedFalse("Chennai"))
                .thenReturn(false);

        when(branchRepository.save(any(Branch.class)))
                .thenReturn(branch);

        BranchResponse response = branchServiceImpl.createBranch(createRequest);

        assertNotNull(response);
        assertEquals("CHN", response.getBranchCode());

        verify(branchRepository).save(any(Branch.class));
    }

    @Test
    void createBranch_duplicateCode() {

        when(branchRepository.existsByBranchCodeAndDeletedFalse("CHN"))
                .thenReturn(true);

        assertThrows(
                BranchAlreadyExistsException.class,
                () -> branchServiceImpl.createBranch(createRequest)
        );

        verify(branchRepository, never()).save(any());
    }

    @Test
    void createBranch_duplicateName() {

        when(branchRepository.existsByBranchCodeAndDeletedFalse("CHN"))
                .thenReturn(false);

        when(branchRepository.existsByBranchNameAndDeletedFalse("Chennai"))
                .thenReturn(true);

        assertThrows(
                BranchAlreadyExistsException.class,
                () -> branchServiceImpl.createBranch(createRequest)
        );
    }

    @Test
    void getBranchById_success() {
        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));

        BranchResponse response = branchServiceImpl.getBranchById("1");

        assertEquals("1", response.getId());
        assertEquals("CHN", response.getBranchCode());
    }

    @Test
    void getBranchById_notFound() {

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                BranchNotFoundException.class,
                () -> branchServiceImpl.getBranchById("1")
        );
    }

    @Test
    void updateBranch_success() {

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));

        when(branchRepository.existsByBranchNameAndDeletedFalse("Bangalore HQ"))
                .thenReturn(false);

        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BranchResponse response =
                branchServiceImpl.updateBranch("1", updateRequest);

        assertEquals("Bangalore HQ", response.getBranchName());
    }

    @Test
    void updateBranch_notFound() {

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.empty());

        UpdateBranchRequest request = UpdateBranchRequest.builder().build();

        assertThrows(
                BranchNotFoundException.class,
                () -> branchServiceImpl.updateBranch("1", request)
        );
    }

    @Test
    void updateBranch_duplicateName() {

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));

        when(branchRepository.existsByBranchNameAndDeletedFalse("Bangalore HQ"))
                .thenReturn(true);

        assertThrows(
                BranchAlreadyExistsException.class,
                () -> branchServiceImpl.updateBranch("1", updateRequest)
        );
    }

    @Test
    void deactivateBranch_success() {
        Branch activeBranch = Branch.builder()
                .id("1")
                .branchCode("CHN")
                .branchName("Chennai")
                .status(BranchStatus.ACTIVE)
                .build();
        activeBranch.setDeleted(false);
        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));
        branchServiceImpl.deactivateBranch("1");

        verify(branchRepository).save(any(Branch.class));

        assertTrue(branch.isDeleted());
        assertEquals(
                BranchStatus.INACTIVE,
                branch.getStatus()
        );
    }

    @Test
    void deactivateBranch_notFound() {

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.empty());

        assertThrows(
                BranchNotFoundException.class,
                () -> branchServiceImpl.deactivateBranch("1")
        );
    }
}