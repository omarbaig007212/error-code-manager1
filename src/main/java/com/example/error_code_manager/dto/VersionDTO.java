package com.example.error_code_manager.dto;

import lombok.Data;

@Data
public class VersionDTO {
    private Integer versionId;
    private String versionNumber;  // Changed from Integer to String
    private Integer productId;
}