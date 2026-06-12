package com.iexceed.seatmanagement.floors.mapper;

import com.iexceed.seatmanagement.floors.dto.request.CreateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.request.UpdateFloorRequest;
import com.iexceed.seatmanagement.floors.dto.response.FloorResponse;
import com.iexceed.seatmanagement.floors.entity.Floor;
import com.iexceed.seatmanagement.floors.enums.FloorStatus;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class FloorMapperTest {

    @Test
    void toEntity_success() {
        CreateFloorRequest request = CreateFloorRequest.builder()
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();

        Floor floor = FloorMapper.toEntity(request);

        assertNotNull(floor);
        assertEquals("branch-1", floor.getBranchId());
        assertEquals("F1", floor.getFloorCode());
        assertEquals("Floor 1", floor.getFloorName());
        assertEquals(100, floor.getTotalSeats());
        assertEquals(FloorStatus.ACTIVE, floor.getStatus());

        assertFalse(floor.isLayoutEnabled());
        assertNull(floor.getActiveLayoutVersionId());
    }

    @Test
    void toResponse_success() {
        Floor floor = Floor.builder()
                .id("floor-1")
                .branchId("branch-1")
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .layoutEnabled(true)
                .activeLayoutVersionId("layout-1")
                .status(FloorStatus.ACTIVE)
                .build();

        FloorResponse response = FloorMapper.toResponse(floor);

        assertNotNull(response);
        assertEquals("floor-1", response.getId());
        assertEquals("branch-1", response.getBranchId());
        assertEquals("F1", response.getFloorCode());
        assertEquals("Floor 1", response.getFloorName());
        assertEquals(100, response.getTotalSeats());
        assertTrue(response.isLayoutEnabled());
        assertEquals("layout-1", response.getActiveLayoutVersionId());
        assertEquals(FloorStatus.ACTIVE, response.getStatus());
    }

    @Test
    void updateEntity_success() {
        Floor floor = Floor.builder()
                .floorCode("F1")
                .floorName("Floor 1")
                .totalSeats(100)
                .status(FloorStatus.ACTIVE)
                .build();

        UpdateFloorRequest request = UpdateFloorRequest.builder()
                .floorCode("F2")
                .floorName("Floor 2")
                .totalSeats(200)
                .status(FloorStatus.UNDER_MAINTENANCE)
                .build();

        FloorMapper.updateEntity(floor, request);

        assertEquals("F2", floor.getFloorCode());
        assertEquals("Floor 2", floor.getFloorName());
        assertEquals(200, floor.getTotalSeats());
        assertEquals(FloorStatus.UNDER_MAINTENANCE, floor.getStatus());
    }

    @Test
    void constructor_shouldThrowException() throws Exception {

        var constructor = FloorMapper.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);

        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());

        assertEquals("Utility class", exception.getCause().getMessage());
    }
}