package com.iexceed.seatmanagement.floors.entity;

import com.iexceed.seatmanagement.common.entity.BaseAuditEntity;
import com.iexceed.seatmanagement.floors.enums.FloorStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "floors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Floor extends BaseAuditEntity {

    @Id
    private String id;
    @Indexed(unique = true)
    private String floorCode;
    private String branchCode;
    private Integer floorNumber;
    private Integer totalSeats;
    private boolean layoutEnabled;
    private String activeLayoutVersionId;
    private FloorStatus status;

}