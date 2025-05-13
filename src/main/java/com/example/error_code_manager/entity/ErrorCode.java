package com.example.error_code_manager.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "error_codes")
@Data
public class ErrorCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Changed from UUID to IDENTITY
    private Integer id;  // Changed from String to Integer
    
    private String errorCodeId;  // Business identifier (can have duplicates)
    private String conditionId;
    private String component;
    
    // @Column(name = "id_value")
    // private String idValue;  // Commented out as errorCodeId is used instead
    
    private String severity;
    private String callhome;
    private String alertName;
    private String description;
    private String rca;
    private String correctiveAction;
    private String eventSource;
    private String alertType;
    
    @ManyToMany(mappedBy = "errorCodes", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnoreProperties("errorCodes")
    private List<Version> versions = new ArrayList<>();
}