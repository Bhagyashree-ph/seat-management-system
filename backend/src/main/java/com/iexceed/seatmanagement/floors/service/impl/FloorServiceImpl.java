package com.iexceed.seatmanagement.floors.service.impl;

import com.iexceed.seatmanagement.audit.entity.FieldChange;
import com.iexceed.seatmanagement.audit.enums.AuditAction;
import com.iexceed.seatmanagement.audit.enums.EntityType;
import com.iexceed.seatmanagement.audit.service.AuditService;
import com.iexceed.seatmanagement.audit.util.AuditDiffUtil;
import com.iexceed.seatmanagement.audit.util.AuditObjectCloneUtil;
import com.iexceed.seatmanagement.branches.entity.Branch;
import com.iexceed.seatmanagement.branches.exceptions.BranchNotFoundException;
import com.iexceed.seatmanagement.branches.repository.BranchRepository;
import com.iexceed.seatmanagement.common.exceptions.DuplicateResourceException;
import com.iexceed.seatmanagement.common.utils.AuditUtil;
import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.entity.Floor;
import com.iexceed.seatmanagement.floors.exceptions.FloorNotFoundException;
import com.iexceed.seatmanagement.floors.mapper.FloorMapper;
import com.iexceed.seatmanagement.floors.repository.FloorRepository;
import com.iexceed.seatmanagement.floors.service.FloorService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FloorServiceImpl implements FloorService {

    private final FloorRepository floorRepository;
    private final BranchRepository branchRepository;
    private final AuditService auditService;

    @Override
    public FloorResponse createFloor(CreateFloorRequest request) {
        branchRepository.findByIdAndDeletedFalse(request.getBranchId())
                .orElseThrow(() -> new BranchNotFoundException("Branch not found with id: " + request.getBranchId()));
        request.setFloorCode(
                request.getFloorCode().trim().toUpperCase()
        );
        if (floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalse(request.getBranchId(), request.getFloorCode())) {
            throw new DuplicateResourceException("Floor code already exists in this branch");
        }
        Floor floor = FloorMapper.toEntity(request);
        AuditUtil.setCreateAudit(floor);
        Floor savedFloor = floorRepository.save(floor);
        auditService.log(EntityType.FLOOR, savedFloor.getId(), AuditAction.CREATE_FLOOR, Map.of());
        return FloorMapper.toResponse(savedFloor);
    }

    @Override
    public FloorResponse updateFloor(String floorId, UpdateFloorRequest request) {
        Floor floor = floorRepository.findByIdAndDeletedFalse(floorId)
                .orElseThrow(() -> new FloorNotFoundException("Floor not found with id: " + floorId));
        if (floorRepository.existsByBranchIdAndFloorCodeAndDeletedFalseAndIdNot(floor.getBranchId(), request.getFloorCode(), floorId)) {
            throw new DuplicateResourceException("Floor code already exists in this branch");
        }
        Floor oldFloor = AuditObjectCloneUtil.deepCopy(floor, Floor.class);
        FloorMapper.updateEntity(floor, request);
        AuditUtil.setUpdateAudit(floor);
        Floor updatedFloor = floorRepository.save(floor);
        auditService.log(EntityType.FLOOR, updatedFloor.getId(), AuditAction.UPDATE_FLOOR, AuditDiffUtil.compare(oldFloor, updatedFloor));
        return FloorMapper.toResponse(updatedFloor);
    }

    @Override
    public List<FloorResponse> getAllFloors() {
        return floorRepository.findByDeletedFalse()
                .stream()
                .map(FloorMapper::toResponse)
                .toList();
    }

    @Override
    public void deleteFloor(String floorId) {

        // Only active (non-deleted) floors can be deleted.
        Floor floor = floorRepository.findByIdAndDeletedFalse(floorId)
                .orElseThrow(() -> new FloorNotFoundException("Floor not found with id: " + floorId));

        // Prevent deletion when layout module introduces active layouts.
        validateNoActiveLayouts(floor);

        // Prevent deletion when seat module introduces active seat assignments.
        validateNoActiveSeatAssignments(floor);

        // Capture pre-delete state for audit history.
        Floor oldFloor = AuditObjectCloneUtil.deepCopy(floor, Floor.class);

        floor.setDeleted(true);
        AuditUtil.setUpdateAudit(floor);

        Floor deletedFloor = floorRepository.save(floor);
        auditService.log(
                EntityType.FLOOR,
                deletedFloor.getId(),
                AuditAction.DELETE_FLOOR,
                AuditDiffUtil.compare(oldFloor, deletedFloor)
        );
    }

    /**
     * Layout module integration point.
     * A floor with active layouts must not be deleted.
     */
    private void validateNoActiveLayouts(Floor floor) {
        // TODO: Implement after Layout module.
    }

    /**
     * Seat module integration point.
     * A floor with active seat assignments must not be deleted.
     */
    private void validateNoActiveSeatAssignments(Floor floor) {
        // TODO: Implement after Seat module.
    }

    @Override
    public FloorResponse getFloorById(String floorId) {

        Floor floor = floorRepository
                .findByIdAndDeletedFalse(floorId)
                .orElseThrow(() ->
                        new FloorNotFoundException(
                                "Floor not found with id: " + floorId));

        return FloorMapper.toResponse(floor);
    }
}