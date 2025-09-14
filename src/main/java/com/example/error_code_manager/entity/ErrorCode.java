package com.example.error_code_manager.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
    
    @Column(length = 2000)
    private String errorCodeId;  // Business identifier (can have duplicates)
    @Column(length = 2000)
    private String conditionId;
    @Column(length = 2000)
    private String component;
    
    // @Column(name = "id_value")
    // private String idValue;  // Commented out as errorCodeId is used instead
    
    @Column(length = 2000)
    private String severity;
    @Column(length = 2000)
    private String callhome;
    @Column(length = 2000)
    private String alertName;
    @Column(length = 2000)
    private String description;
    @Column(length = 2000)
    private String rca;
    @Column(length = 2000)
    private String correctiveAction;
    @Column(length = 2000)
    private String eventSource;
    @Column(length = 2000)
    private String alertType;
    
    @ManyToMany(mappedBy = "errorCodes", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JsonIgnoreProperties("errorCodes")
    private List<Version> versions = new ArrayList<>();
}