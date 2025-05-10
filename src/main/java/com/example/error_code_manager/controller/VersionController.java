package com.example.error_code_manager.controller;

import com.example.error_code_manager.dto.VersionDTO;
import com.example.error_code_manager.entity.ErrorCode;
import com.example.error_code_manager.entity.Product;
import com.example.error_code_manager.entity.Version;
import com.example.error_code_manager.service.ErrorCodeService;
import com.example.error_code_manager.service.ProductService;
import com.example.error_code_manager.service.VersionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/versions")
@Tag(name = "Version", description = "Version management APIs")
public class VersionController {
    private final VersionService versionService;
    private final ProductService productService;
    private final ErrorCodeService errorCodeService;

    @Autowired
    public VersionController(VersionService versionService, ProductService productService, ErrorCodeService errorCodeService) {
        this.versionService = versionService;
        this.productService = productService;
        this.errorCodeService = errorCodeService;
    }

    @GetMapping
    @Operation(summary = "Get all versions", description = "Retrieves a list of all versions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved versions")
    })
    public ResponseEntity<List<Version>> getAllVersions() {
        List<Version> versions = versionService.getAllVersions();
        return new ResponseEntity<>(versions, HttpStatus.OK);
    }

    @GetMapping("/{versionId}")
    @Operation(summary = "Get version by ID", description = "Retrieves a specific version by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the version"),
        @ApiResponse(responseCode = "404", description = "Version not found")
    })
    public ResponseEntity<Version> getVersionById(@PathVariable Integer versionId) {
        Optional<Version> version = versionService.getVersionById(versionId);
        return version.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get versions by product ID", description = "Retrieves all versions for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved versions")
    })
    public ResponseEntity<List<Version>> getVersionsByProductId(@PathVariable Integer productId) {
        List<Version> versions = versionService.getVersionsByProductId(productId);
        return new ResponseEntity<>(versions, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new version", description = "Creates a new version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Version successfully created"),
        @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<Version> createVersion(@RequestBody VersionDTO versionDTO) {
        // Find the product
        Optional<Product> productOptional = productService.getProductByID(versionDTO.getProductId());
        
        if (productOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Convert DTO to entity
        Version version = new Version();
        version.setVersionNumber(versionDTO.getVersionNumber());
        version.setProduct(productOptional.get());
        
        Version createdVersion = versionService.createVersion(version);
        return new ResponseEntity<>(createdVersion, HttpStatus.CREATED);
    }

    @PutMapping("/{versionId}")
    @Operation(summary = "Update a version", description = "Updates an existing version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Version successfully updated"),
        @ApiResponse(responseCode = "404", description = "Version or Product not found")
    })
    public ResponseEntity<Version> updateVersion(@PathVariable Integer versionId, @RequestBody VersionDTO versionDTO) {
        Optional<Version> existingVersionOptional = versionService.getVersionById(versionId);
        
        if (existingVersionOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        // Check if product exists if productId is provided
        Product product = null;
        if (versionDTO.getProductId() != null) {
            Optional<Product> productOptional = productService.getProductByID(versionDTO.getProductId());
            if (productOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            product = productOptional.get();
        }
        
        Version existingVersion = existingVersionOptional.get();
        existingVersion.setVersionNumber(versionDTO.getVersionNumber());
        
        if (product != null) {
            existingVersion.setProduct(product);
        }
        
        Version updatedVersion = versionService.createVersion(existingVersion);
        return new ResponseEntity<>(updatedVersion, HttpStatus.OK);
    }

    @PostMapping("/{versionId}/errorCodes/{errorCodeId}")
    @Operation(summary = "Associate an error code with a version", description = "Associates an existing error code with a version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Error code successfully associated with version"),
        @ApiResponse(responseCode = "404", description = "Version or Error code not found")
    })
    public ResponseEntity<Version> addErrorCodeToVersion(@PathVariable Integer versionId, 
                                                        @PathVariable Integer errorCodeId) {
        Optional<Version> versionOptional = versionService.getVersionById(versionId);
        Optional<ErrorCode> errorCodeOptional = errorCodeService.getErrorCodeById(errorCodeId);
        
        if (versionOptional.isPresent() && errorCodeOptional.isPresent()) {
            Version version = versionOptional.get();
            ErrorCode errorCode = errorCodeOptional.get();
            
            version.getErrorCodes().add(errorCode);
            Version savedVersion = versionService.createVersion(version);
            
            return new ResponseEntity<>(savedVersion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{versionId}/errorCodes/{errorCodeId}")
    @Operation(summary = "Remove an error code from a version", description = "Removes the association between an error code and a version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Error code successfully removed from version"),
        @ApiResponse(responseCode = "404", description = "Version or Error code not found")
    })
    public ResponseEntity<Version> removeErrorCodeFromVersion(@PathVariable Integer versionId, 
                                                            @PathVariable Integer errorCodeId) {
        Optional<Version> versionOptional = versionService.getVersionById(versionId);
        Optional<ErrorCode> errorCodeOptional = errorCodeService.getErrorCodeById(errorCodeId);
        
        if (versionOptional.isPresent() && errorCodeOptional.isPresent()) {
            Version version = versionOptional.get();
            ErrorCode errorCode = errorCodeOptional.get();
            
            version.getErrorCodes().remove(errorCode);
            Version savedVersion = versionService.createVersion(version);
            
            return new ResponseEntity<>(savedVersion, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{versionId}")
    @Operation(summary = "Delete a version", description = "Deletes a version by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Version successfully deleted")
    })
    public ResponseEntity<Void> deleteVersion(@PathVariable Integer versionId) {
        versionService.deleteVersion(versionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}