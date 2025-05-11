package com.example.error_code_manager.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "versions")
@Data
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer versionId;
    
    private Integer versionNumber;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("versions")
    private Product product;
    
    @ManyToMany
    @JoinTable(
        name = "version_error_code",
        joinColumns = @JoinColumn(name = "version_id"),
        inverseJoinColumns = @JoinColumn(name = "error_code_id")
    )
    @JsonIgnoreProperties("versions")
    private List<ErrorCode> errorCodes = new ArrayList<>();
}