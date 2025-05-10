package com.example.error_code_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "error_codes")
@Data
public class ErrorCode {
    @Id
    private Integer errorCodeId;
    
    private String conditionId;
    private String component;
    
    @Column(name = "id_value")
    private String idValue;
    
    private String severity;
    private String callhome;
    private String alertName;
    private String description;
    private String rca;
    private String correctiveAction;
    private String eventSource;
    private String alertType;
    
    @ManyToMany(mappedBy = "errorCodes")
    @JsonIgnoreProperties("errorCodes")
    private List<Version> versions = new ArrayList<>();
}