package com.iexceed.seatmanagement.employees.dto.zoho;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ZohoEmployeeResponseDTO {

    private List<Map<String, List<ZohoEmployeeRecordDTO>>> result;

    private String message;

    private String uri;

    private Integer status;
}