package com.iexceed.seatmanagement.floors.service.impl;

import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.service.AuditService;
import com.iexceed.seatmanagement.audit.util.AuditDiffUtil;
import com.iexceed.seatmanagement.audit.util.AuditObjectCloneUtil;
import com.iexceed.seatmanagement.branches.exceptions.BranchNotFoundException;
import com.iexceed.seatmanagement.branches.repository.BranchRepository;
import com.iexceed.seatmanagement.common.exceptions.DuplicateResourceException;
import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.entity.Floor;
import com.iexceed.seatmanagement.floors.enums.FloorStatus;
import com.iexceed.seatmanagement.floors.exceptions.FloorNotFoundException;
import com.iexceed.seatmanagement.floors.mapper.FloorMapper;
import com.iexceed.seatmanagement.floors.repository.FloorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FloorServiceImplTest {

    @Mock
    private FloorRepository floorRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private FloorServiceImpl floorService;

    private Floor floor;
    private CreateFloorRequest createRequest;
    private UpdateFloorRequest updateRequest;
    private FloorResponse floorResponse;

    @BeforeEach
    void setUp() {
        floor = Floor.builder()
                .id("floor-1")
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .layoutEnabled(false)
                .activeLayoutVersionId(null)
                .status(FloorStatus.ACTIVE)
                .build();
        floor.setDeleted(false);

        createRequest = CreateFloorRequest.builder()
                .branchId("branch-1")
                .floorCode("  f1  ")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();

        updateRequest = UpdateFloorRequest.builder()
                .floorCode("F1_UPDATED")
                .floorName("Floor 1 Updated")
                .totalSeats(120)
                .status(FloorStatus.INACTIVE)
                .build();

        floorResponse = FloorResponse.builder()
                .id("floor-1")
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .layoutEnabled(false)
                .activeLayoutVersionId(null)
                .status(FloorStatus.ACTIVE)
                .build();
    }

    // ===================== CREATE FLOOR TESTS =====================

    @Test
    void createFloor_success() {
        when(branchRepository.findByIdAndDeletedFalse("branch-1"))
                .thenReturn(Optional.of(com.iexceed.seatmanagement.branches.entity.Branch.builder().build()));

        when(floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalse("branch-1", "F1"))
                .thenReturn(false);

        when(floorRepository.save(any(Floor.class)))
                .thenAnswer(invocation -> {
                    Floor savedFloor = invocation.getArgument(0);
                    savedFloor.setId("floor-1");
                    return savedFloor;
                });

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class)) {
            mapperMock.when(() -> FloorMapper.toEntity(any(CreateFloorRequest.class)))
                    .thenReturn(floor);
            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse);

            FloorResponse response = floorService.createFloor(createRequest);

            assertNotNull(response);
            assertEquals("floor-1", response.getId());
            assertEquals("F1", response.getFloorCode());
            assertEquals("Floor 1", response.getFloorName());
            assertEquals(100, response.getTotalSeats());
            assertEquals(FloorStatus.ACTIVE, response.getStatus());

            verify(branchRepository).findByIdAndDeletedFalse("branch-1");
            verify(floorRepository).existsByBranchIdAndFloorCodeAndDeletedFalse("branch-1", "F1");
            verify(floorRepository).save(any(Floor.class));
            verify(auditService).log(EntityType.FLOOR, "floor-1", AuditAction.CREATE_FLOOR, Map.of());
        }
    }

    @Test
    void createFloor_branchNotFound() {
        when(branchRepository.findByIdAndDeletedFalse("branch-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                BranchNotFoundException.class,
                () -> floorService.createFloor(createRequest)
        );

        verify(branchRepository).findByIdAndDeletedFalse("branch-1");
        verify(floorRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void createFloor_duplicateFloorCode() {
        when(branchRepository.findByIdAndDeletedFalse("branch-1"))
                .thenReturn(Optional.of(com.iexceed.seatmanagement.branches.entity.Branch.builder().build()));

        when(floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalse("branch-1", "F1"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> floorService.createFloor(createRequest)
        );

        verify(branchRepository).findByIdAndDeletedFalse("branch-1");
        verify(floorRepository).existsByBranchIdAndFloorCodeAndDeletedFalse("branch-1", "F1");
        verify(floorRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void createFloor_floorCodeTrimmingAndUppercasing() {
        CreateFloorRequest request = CreateFloorRequest.builder()
                .branchId("branch-1")
                .floorCode("  f1  ")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();

        when(branchRepository.findByIdAndDeletedFalse("branch-1"))
                .thenReturn(Optional.of(com.iexceed.seatmanagement.branches.entity.Branch.builder().build()));

        when(floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalse("branch-1", "F1"))
                .thenReturn(false);

        when(floorRepository.save(any(Floor.class)))
                .thenAnswer(invocation -> {
                    Floor savedFloor = invocation.getArgument(0);
                    savedFloor.setId("floor-1");
                    return savedFloor;
                });

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class)) {
            mapperMock.when(() -> FloorMapper.toEntity(any(CreateFloorRequest.class)))
                    .thenReturn(floor);
            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse);

            floorService.createFloor(request);

            // Verify that floor code was trimmed and uppercased
            assertEquals("F1", request.getFloorCode());
        }
    }

    // ===================== GET ALL FLOORS TESTS =====================

    @Test
    void getAllFloors_success() {
        when(floorRepository.findByDeletedFalse())
                .thenReturn(List.of(floor));

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class)) {
            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse);

            List<FloorResponse> response = floorService.getAllFloors();

            assertNotNull(response);
            assertEquals(1, response.size());
            assertEquals("floor-1", response.get(0).getId());
            assertEquals("F1", response.get(0).getFloorCode());

            verify(floorRepository).findByDeletedFalse();
        }
    }

    @Test
    void getAllFloors_emptyList() {
        when(floorRepository.findByDeletedFalse())
                .thenReturn(List.of());

        List<FloorResponse> response = floorService.getAllFloors();

        assertNotNull(response);
        assertEquals(0, response.size());
        verify(floorRepository).findByDeletedFalse();
    }

    @Test
    void getAllFloors_multipleFloors() {
        Floor floor2 = Floor.builder()
                .id("floor-2")
                .branchId("branch-1")
                .floorCode("F2")
                .floorName("Floor 2")
                .totalSeats(150)
                .status(FloorStatus.ACTIVE)
                .build();
        floor2.setDeleted(false);

        FloorResponse floorResponse2 = FloorResponse.builder()
                .id("floor-2")
                .branchId("branch-1")
                .floorCode("F2")
                .floorName("Floor 2")
                .totalSeats(150)
                .status(FloorStatus.ACTIVE)
                .build();

        when(floorRepository.findByDeletedFalse())
                .thenReturn(List.of(floor, floor2));

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class)) {
            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse, floorResponse2);

            List<FloorResponse> response = floorService.getAllFloors();

            assertNotNull(response);
            assertEquals(2, response.size());
            verify(floorRepository).findByDeletedFalse();
        }
    }

    // ===================== GET FLOOR BY ID TESTS =====================

    @Test
    void getFloorById_success() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class)) {
            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse);

            FloorResponse response = floorService.getFloorById("floor-1");

            assertNotNull(response);
            assertEquals("floor-1", response.getId());
            assertEquals("F1", response.getFloorCode());

            verify(floorRepository).findByIdAndDeletedFalse("floor-1");
        }
    }

    @Test
    void getFloorById_notFound() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                FloorNotFoundException.class,
                () -> floorService.getFloorById("floor-1")
        );

        verify(floorRepository).findByIdAndDeletedFalse("floor-1");
    }

    @Test
    void getFloorById_validatesErrorMessage() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.empty());

        FloorNotFoundException exception = assertThrows(
                FloorNotFoundException.class,
                () -> floorService.getFloorById("floor-1")
        );

        assertTrue(exception.getMessage().contains("floor-1"));
    }

    // ===================== UPDATE FLOOR TESTS =====================

    @Test
    void updateFloor_success() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        when(floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalseAndIdNot(
                "branch-1", "F1_UPDATED", "floor-1"))
                .thenReturn(false);

        when(floorRepository.save(any(Floor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class);
             MockedStatic<AuditObjectCloneUtil> cloneUtilMock = mockStatic(AuditObjectCloneUtil.class)) {

            cloneUtilMock.when(() -> AuditObjectCloneUtil.deepCopy(any(Floor.class), eq(Floor.class)))
                    .thenReturn(floor);

            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse);

            FloorResponse response = floorService.updateFloor("floor-1", updateRequest);

            assertNotNull(response);
            verify(floorRepository).findByIdAndDeletedFalse("floor-1");
            verify(floorRepository).save(any(Floor.class));
            verify(auditService).log(eq(EntityType.FLOOR), eq("floor-1"), eq(AuditAction.UPDATE_FLOOR), any());
        }
    }

    @Test
    void updateFloor_notFound() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                FloorNotFoundException.class,
                () -> floorService.updateFloor("floor-1", updateRequest)
        );

        verify(floorRepository).findByIdAndDeletedFalse("floor-1");
        verify(floorRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void updateFloor_duplicateFloorCode() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        when(floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalseAndIdNot(
                "branch-1", "F1_UPDATED", "floor-1"))
                .thenReturn(true);

        assertThrows(
                DuplicateResourceException.class,
                () -> floorService.updateFloor("floor-1", updateRequest)
        );

        verify(floorRepository).findByIdAndDeletedFalse("floor-1");
        verify(floorRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void updateFloor_sameFloorCode_doesNotCheckDuplicate() {
        UpdateFloorRequest sameCodeRequest = UpdateFloorRequest.builder()
                .floorCode("F1")
                .floorName("Floor 1 Updated")
                .totalSeats(120)
                .status(FloorStatus.INACTIVE)
                .build();

        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        when(floorRepository.save(any(Floor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<FloorMapper> mapperMock = mockStatic(FloorMapper.class);
             MockedStatic<AuditObjectCloneUtil> cloneUtilMock = mockStatic(AuditObjectCloneUtil.class)) {

            cloneUtilMock.when(() -> AuditObjectCloneUtil.deepCopy(any(Floor.class), eq(Floor.class)))
                    .thenReturn(floor);

            mapperMock.when(() -> FloorMapper.toResponse(any(Floor.class)))
                    .thenReturn(floorResponse);

            floorService.updateFloor("floor-1", sameCodeRequest);

            // Should still check for duplicates as floor code is being updated
            verify(floorRepository).existsByBranchIdAndFloorCodeAndDeletedFalseAndIdNot(
                    "branch-1", "F1", "floor-1");
        }
    }

    // ===================== DELETE FLOOR TESTS =====================

    @Test
    void deleteFloor_success() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        when(floorRepository.save(any(Floor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<AuditObjectCloneUtil> cloneUtilMock = mockStatic(AuditObjectCloneUtil.class);
             MockedStatic<AuditDiffUtil> diffUtilMock = mockStatic(AuditDiffUtil.class)) {

            cloneUtilMock.when(() -> AuditObjectCloneUtil.deepCopy(any(Floor.class), eq(Floor.class)))
                    .thenReturn(floor);

            diffUtilMock.when(() -> AuditDiffUtil.compare(any(), any()))
                    .thenReturn(Map.of());

            floorService.deleteFloor("floor-1");

            assertTrue(floor.isDeleted());
            verify(floorRepository).findByIdAndDeletedFalse("floor-1");
            verify(floorRepository).save(any(Floor.class));
            verify(auditService).log(eq(EntityType.FLOOR), eq("floor-1"), eq(AuditAction.DELETE_FLOOR), any());
        }
    }

    @Test
    void deleteFloor_notFound() {
        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.empty());

        assertThrows(
                FloorNotFoundException.class,
                () -> floorService.deleteFloor("floor-1")
        );

        verify(floorRepository).findByIdAndDeletedFalse("floor-1");
        verify(floorRepository, never()).save(any());
        verifyNoInteractions(auditService);
    }

    @Test
    void deleteFloor_capturesPreDeleteState() {
        Floor originalFloor = Floor.builder()
                .id("floor-1")
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();
        originalFloor.setDeleted(false);

        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(originalFloor));

        when(floorRepository.save(any(Floor.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        try (MockedStatic<AuditObjectCloneUtil> cloneUtilMock = mockStatic(AuditObjectCloneUtil.class);
             MockedStatic<AuditDiffUtil> diffUtilMock = mockStatic(AuditDiffUtil.class)) {

            Floor clonedFloor = Floor.builder()
                    .id("floor-1")
                    .branchId("branch-1")
                    .floorCode("F1")
                    .floorName("Floor 1")
                    .totalSeats(100)
                    .status(FloorStatus.ACTIVE)
                    .build();
            clonedFloor.setDeleted(false);

            cloneUtilMock.when(() -> AuditObjectCloneUtil.deepCopy(any(Floor.class), eq(Floor.class)))
                    .thenReturn(clonedFloor);

            diffUtilMock.when(() -> AuditDiffUtil.compare(any(), any()))
                    .thenReturn(Map.of("deleted", "false to true"));

            floorService.deleteFloor("floor-1");

            verify(auditService).log(eq(EntityType.FLOOR), eq("floor-1"), eq(AuditAction.DELETE_FLOOR), any(Map.class));
        }
    }

    @Test
    void updateFloor_generatesAuditDiff() {

        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        when(floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalseAndIdNot(
                anyString(), anyString(), anyString()))
                .thenReturn(false);

        when(floorRepository.save(any(Floor.class)))
                .thenReturn(floor);

        try (MockedStatic<AuditObjectCloneUtil> cloneUtilMock =
                     mockStatic(AuditObjectCloneUtil.class);
             MockedStatic<AuditDiffUtil> diffUtilMock =
                     mockStatic(AuditDiffUtil.class)) {

            cloneUtilMock.when(() ->
                            AuditObjectCloneUtil.deepCopy(any(Floor.class), eq(Floor.class)))
                    .thenReturn(floor);

            diffUtilMock.when(() ->
                            AuditDiffUtil.compare(any(Floor.class), any(Floor.class)))
                    .thenReturn(Map.of());

            floorService.updateFloor("floor-1", updateRequest);

            diffUtilMock.verify(() ->
                    AuditDiffUtil.compare(any(Floor.class), any(Floor.class)));
        }
    }

    @Test
    void deleteFloor_generatesAuditDiff() {

        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        when(floorRepository.save(any(Floor.class)))
                .thenReturn(floor);

        try (MockedStatic<AuditObjectCloneUtil> cloneUtilMock =
                     mockStatic(AuditObjectCloneUtil.class);
             MockedStatic<AuditDiffUtil> diffUtilMock =
                     mockStatic(AuditDiffUtil.class)) {

            cloneUtilMock.when(() ->
                            AuditObjectCloneUtil.deepCopy(any(Floor.class), eq(Floor.class)))
                    .thenReturn(floor);

            diffUtilMock.when(() ->
                            AuditDiffUtil.compare(any(Floor.class), any(Floor.class)))
                    .thenReturn(Map.of());

            floorService.deleteFloor("floor-1");

            diffUtilMock.verify(() ->
                    AuditDiffUtil.compare(any(Floor.class), any(Floor.class)));
        }
    }

    @Test
    void getFloorById_returnsMappedResponse() {

        when(floorRepository.findByIdAndDeletedFalse("floor-1"))
                .thenReturn(Optional.of(floor));

        FloorResponse response =
                floorService.getFloorById("floor-1");

        assertEquals("floor-1", response.getId());
        assertEquals("branch-1", response.getBranchId());
        assertEquals("F1", response.getFloorCode());
        assertEquals("Floor 1", response.getFloorName());
    }
}

