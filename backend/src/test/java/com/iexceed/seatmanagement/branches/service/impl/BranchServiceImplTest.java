package com.iexceed.seatmanagement.branches.service.impl;

import com.iexceed.seatmanagement.branches.dto.request.CreateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.request.UpdateBranchRequest;
import com.iexceed.seatmanagement.branches.dto.response.BranchResponse;
import com.iexceed.seatmanagement.branches.entity.Branch;
import com.iexceed.seatmanagement.branches.enums.BranchStatus;
import com.iexceed.seatmanagement.branches.exceptions.BranchAlreadyExistsException;
import com.iexceed.seatmanagement.branches.exceptions.BranchNotFoundException;
import com.iexceed.seatmanagement.branches.repository.BranchRepository;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.service.AuditService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BranchServiceImplTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AuditService auditService;

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

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createBranch_success() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@example.com", "password")
        );
        when(branchRepository.existsByBranchCodeAndDeletedFalse("CHN"))
                .thenReturn(false);

        when(branchRepository.existsByBranchNameAndDeletedFalse("Chennai"))
                .thenReturn(false);

        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> {
                    Branch savedBranch = invocation.getArgument(0);
                    savedBranch.setId("1");
                    return savedBranch;
                });

        BranchResponse response = branchServiceImpl.createBranch(createRequest);

        assertNotNull(response);
        assertEquals("1", response.getId());
        assertEquals("CHN", response.getBranchCode());
        assertFalse(response.isDeleted());

        verify(branchRepository).save(any(Branch.class));
        verify(auditService).log(EntityType.BRANCH, "1", AuditAction.CREATE_BRANCH, Map.of());
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
        verifyNoInteractions(auditService);
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
        verify(branchRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void getAllBranches_success() {
        when(branchRepository.findByDeletedFalse())
                .thenReturn(List.of(branch));

        List<BranchResponse> response = branchServiceImpl.getAllBranches();

        assertEquals(1, response.size());
        assertEquals("1", response.get(0).getId());
        assertEquals("CHN", response.get(0).getBranchCode());
        verify(branchRepository).findByDeletedFalse();
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
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@example.com", "password")
        );

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));

        when(branchRepository.existsByBranchNameAndDeletedFalse("Bangalore HQ"))
                .thenReturn(false);

        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BranchResponse response =
                branchServiceImpl.updateBranch("1", updateRequest);

        assertEquals("Bangalore HQ", response.getBranchName());
        assertEquals("Bangalore", response.getCity());
        assertEquals("admin@example.com", branch.getUpdatedBy());
        verify(auditService).log(eq(EntityType.BRANCH), eq("1"), eq(AuditAction.UPDATE_BRANCH), any());
    }

    @Test
    void updateBranch_sameName_doesNotCheckDuplicateName() {
        updateRequest = UpdateBranchRequest.builder()
                .branchName("chennai")
                .city("Chennai")
                .address("ECR")
                .timezone("Asia/Kolkata")
                .status(BranchStatus.ACTIVE)
                .build();

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));

        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BranchResponse response = branchServiceImpl.updateBranch("1", updateRequest);

        assertEquals("chennai", response.getBranchName());
        verify(branchRepository, never()).existsByBranchNameAndDeletedFalse(any());
        verify(auditService).log(eq(EntityType.BRANCH), eq("1"), eq(AuditAction.UPDATE_BRANCH), any());
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
        verify(branchRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void updateBranch_invalidTimezone() {

        UpdateBranchRequest request = UpdateBranchRequest.builder()
                .branchName("Bangalore HQ")
                .city("Bangalore")
                .address("Koramangala")
                .timezone("Not/A-Timezone")
                .status(BranchStatus.ACTIVE)
                .build();

        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));

        when(branchRepository.existsByBranchNameAndDeletedFalse("Bangalore HQ"))
                .thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> branchServiceImpl.updateBranch("1", request)
        );

        assertEquals("Invalid timezone", exception.getMessage());
        verify(branchRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void deactivateBranch_success() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin@example.com", "password")
        );
        when(branchRepository.findByIdAndDeletedFalse("1"))
                .thenReturn(Optional.of(branch));
        when(branchRepository.save(any(Branch.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        branchServiceImpl.deactivateBranch("1");

        verify(branchRepository).save(any(Branch.class));
        verify(auditService).log(eq(EntityType.BRANCH), eq("1"), eq(AuditAction.DELETE_BRANCH), any());

        assertTrue(branch.isDeleted());
        assertEquals("admin@example.com", branch.getUpdatedBy());
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
        verify(branchRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }
}
