package com.example.error_code_manager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.error_code_manager.entity.ErrorCode;
import com.example.error_code_manager.repository.ErrorCodeRepository;

@Service
public class ErrorCodeService {
    private final ErrorCodeRepository errorCodeRepository;

    public ErrorCodeService(ErrorCodeRepository errorCodeRepository) {
        this.errorCodeRepository = errorCodeRepository;
    }

    public List<ErrorCode> getAllErrorCodes() {
        return errorCodeRepository.findAll();
    }

    public Optional<ErrorCode> getErrorCodeById(String id) {
        // Try finding by internal id first, then by errorCodeId
        try {
            Integer intId = Integer.parseInt(id);
            Optional<ErrorCode> byId = errorCodeRepository.findById(intId);
            if (byId.isPresent()) {
                return byId;
            }
        } catch (NumberFormatException e) {
            // If id is not a number, try finding by errorCodeId
        }
        return errorCodeRepository.findByErrorCodeId(id);
    }

    public List<ErrorCode> getAllByErrorCodeId(String errorCodeId) {
        return errorCodeRepository.findAllByErrorCodeId(errorCodeId);
    }

    public List<ErrorCode> getErrorCodesByConditionId(String conditionId) {
        return errorCodeRepository.findByConditionId(conditionId);
    }

    public List<ErrorCode> getErrorCodesByComponent(String component) {
        return errorCodeRepository.findByComponent(component);
    }

    // Comment out or remove this method
    // public List<ErrorCode> getErrorCodesById(String idValue) {
    //     return errorCodeRepository.findByIdValue(idValue);
    // }

    public List<ErrorCode> getErrorCodesByVersionId(Integer versionId) {
        return errorCodeRepository.findByVersionsVersionId(versionId);
    }

    public void deleteErrorCode(String id) {
        try {
            Integer intId = Integer.parseInt(id);
            errorCodeRepository.deleteById(intId);
        } catch (NumberFormatException e) {
            // Handle the case where id is not a number
            Optional<ErrorCode> errorCode = errorCodeRepository.findByErrorCodeId(id);
            errorCode.ifPresent(ec -> errorCodeRepository.deleteById(ec.getId()));
        }
    }

    public ErrorCode createErrorCode(ErrorCode errorCode) {
        // Remove the uniqueness check to allow duplicate entries
        return errorCodeRepository.save(errorCode);
    }
}