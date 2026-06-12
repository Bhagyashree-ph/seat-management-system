package com.iexceed.seatmanagement.floors.entity;

import com.iexceed.seatmanagement.common.entity.BaseAuditEntity;
import com.iexceed.seatmanagement.floors.enums.FloorStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "floors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(
        name = "uk_branch_floor_code",
        def = "{'branchId': 1, 'floorCode': 1}"
)
public class Floor extends BaseAuditEntity {

    @Id
    private String id;
    private String floorCode;
    private String floorName;
    private String branchId;
    private Integer totalSeats;
    private boolean layoutEnabled;
    private String activeLayoutVersionId;
    private FloorStatus status;

}