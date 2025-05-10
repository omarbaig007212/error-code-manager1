package com.example.error_code_manager.controller;

import com.example.error_code_manager.dto.ErrorCodeDTO;
import com.example.error_code_manager.entity.ErrorCode;
import com.example.error_code_manager.service.ErrorCodeService;
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
@RequestMapping("/api/errorCodes")
@Tag(name = "Error Code", description = "Error code management APIs")
public class ErrorCodeController {
    private final ErrorCodeService errorCodeService;

    @Autowired
    public ErrorCodeController(ErrorCodeService errorCodeService) {
        this.errorCodeService = errorCodeService;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @GetMapping
    @Operation(summary = "Get all error codes", description = "Retrieves a list of all error codes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved error codes")
    })
    public ResponseEntity<List<ErrorCode>> getAllErrorCodes() {
        List<ErrorCode> errorCodes = errorCodeService.getAllErrorCodes();
        return new ResponseEntity<>(errorCodes, HttpStatus.OK);
    }

    @GetMapping("/{errorCodeId}")
    @Operation(summary = "Get error code by ID", description = "Retrieves a specific error code by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the error code"),
        @ApiResponse(responseCode = "404", description = "Error code not found")
    })
    public ResponseEntity<ErrorCode> getErrorCodeById(@PathVariable Integer errorCodeId) {
        Optional<ErrorCode> errorCode = errorCodeService.getErrorCodeById(errorCodeId);
        return errorCode.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/byConditionId")
    @Operation(summary = "Get error codes by condition ID", description = "Retrieves error codes by their condition ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved error codes")
    })
    public ResponseEntity<List<ErrorCode>> getErrorCodesByConditionId(@RequestParam String conditionId) {
        List<ErrorCode> errorCodes = errorCodeService.getErrorCodesByConditionId(conditionId);
        return new ResponseEntity<>(errorCodes, HttpStatus.OK);
    }

    @GetMapping("/byComponent")
    @Operation(summary = "Get error codes by component", description = "Retrieves error codes by their component")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved error codes")
    })
    public ResponseEntity<List<ErrorCode>> getErrorCodesByComponent(@RequestParam String component) {
        List<ErrorCode> errorCodes = errorCodeService.getErrorCodesByComponent(component);
        return new ResponseEntity<>(errorCodes, HttpStatus.OK);
    }

    @GetMapping("/byId")
    @Operation(summary = "Get error codes by ID value", description = "Retrieves error codes by their ID value")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved error codes")
    })
    public ResponseEntity<List<ErrorCode>> getErrorCodesById(@RequestParam String id) {
        List<ErrorCode> errorCodes = errorCodeService.getErrorCodesById(id);
        return new ResponseEntity<>(errorCodes, HttpStatus.OK);
    }

    @GetMapping("/byVersionId/{versionId}")
    @Operation(summary = "Get error codes by version ID", description = "Retrieves error codes associated with a specific version")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved error codes")
    })
    public ResponseEntity<List<ErrorCode>> getErrorCodesByVersionId(@PathVariable Integer versionId) {
        List<ErrorCode> errorCodes = errorCodeService.getErrorCodesByVersionId(versionId);
        return new ResponseEntity<>(errorCodes, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new error code", description = "Creates a new error code with the specified ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Error code successfully created"),
        @ApiResponse(responseCode = "409", description = "Error code with this ID already exists")
    })
    public ResponseEntity<ErrorCode> createErrorCode(@RequestBody ErrorCodeDTO errorCodeDTO) {
        // Convert DTO to entity
        ErrorCode errorCode = new ErrorCode();
        errorCode.setErrorCodeId(errorCodeDTO.getErrorCodeId());
        errorCode.setConditionId(errorCodeDTO.getConditionId());
        errorCode.setComponent(errorCodeDTO.getComponent());
        errorCode.setIdValue(errorCodeDTO.getIdValue());
        errorCode.setSeverity(errorCodeDTO.getSeverity());
        errorCode.setCallhome(errorCodeDTO.getCallhome());
        errorCode.setAlertName(errorCodeDTO.getAlertName());
        errorCode.setDescription(errorCodeDTO.getDescription());
        errorCode.setRca(errorCodeDTO.getRca());
        errorCode.setCorrectiveAction(errorCodeDTO.getCorrectiveAction());
        errorCode.setEventSource(errorCodeDTO.getEventSource());
        errorCode.setAlertType(errorCodeDTO.getAlertType());
        
        ErrorCode createdErrorCode = errorCodeService.createErrorCode(errorCode);
        return new ResponseEntity<>(createdErrorCode, HttpStatus.CREATED);
    }

    @PutMapping("/{errorCodeId}")
    @Operation(summary = "Update an error code", description = "Updates an existing error code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Error code successfully updated"),
        @ApiResponse(responseCode = "404", description = "Error code not found")
    })
    public ResponseEntity<ErrorCode> updateErrorCode(@PathVariable Integer errorCodeId, 
                                                    @RequestBody ErrorCodeDTO errorCodeDTO) {
        Optional<ErrorCode> existingErrorCodeOptional = errorCodeService.getErrorCodeById(errorCodeId);
        
        if (existingErrorCodeOptional.isPresent()) {
            ErrorCode existingErrorCode = existingErrorCodeOptional.get();
            
            // Update fields from DTO
            existingErrorCode.setConditionId(errorCodeDTO.getConditionId());
            existingErrorCode.setComponent(errorCodeDTO.getComponent());
            existingErrorCode.setIdValue(errorCodeDTO.getIdValue());
            existingErrorCode.setSeverity(errorCodeDTO.getSeverity());
            existingErrorCode.setCallhome(errorCodeDTO.getCallhome());
            existingErrorCode.setAlertName(errorCodeDTO.getAlertName());
            existingErrorCode.setDescription(errorCodeDTO.getDescription());
            existingErrorCode.setRca(errorCodeDTO.getRca());
            existingErrorCode.setCorrectiveAction(errorCodeDTO.getCorrectiveAction());
            existingErrorCode.setEventSource(errorCodeDTO.getEventSource());
            existingErrorCode.setAlertType(errorCodeDTO.getAlertType());
            
            ErrorCode updatedErrorCode = errorCodeService.createErrorCode(existingErrorCode);
            return new ResponseEntity<>(updatedErrorCode, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{errorCodeId}")
    @Operation(summary = "Delete an error code", description = "Deletes an error code by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Error code successfully deleted")
    })
    public ResponseEntity<Void> deleteErrorCode(@PathVariable Integer errorCodeId) {
        errorCodeService.deleteErrorCode(errorCodeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}