package com.example.error_code_manager.dto;

import lombok.Data;

@Data
public class ErrorCodeDTO {
    private Integer errorCodeId;
    private String conditionId;
    private String component;
    private String idValue;
    private String severity;
    private String callhome;
    private String alertName;
    private String description;
    private String rca;
    private String correctiveAction;
    private String eventSource;
    private String alertType;
}