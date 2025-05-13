package com.example.error_code_manager.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "versions")
@Data
public class Version {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer versionId;  // Keep as Integer since it's auto-generated
    
    private String versionNumber;  // Changed from Integer to String
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties("versions")
    private Product product;
    
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
        name = "version_error_code",
        joinColumns = @JoinColumn(name = "version_id"),
        inverseJoinColumns = @JoinColumn(name = "error_code_id", referencedColumnName = "id")
    )
    @JsonIgnoreProperties("versions")
    private List<ErrorCode> errorCodes = new ArrayList<>();

    public void addErrorCode(ErrorCode errorCode) {
        this.errorCodes.add(errorCode);
        errorCode.getVersions().add(this);
    }

    public void removeErrorCode(ErrorCode errorCode) {
        this.errorCodes.remove(errorCode);
        errorCode.getVersions().remove(this);
    }
}