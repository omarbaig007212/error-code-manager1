package com.example.error_code_manager.service;

import com.example.error_code_manager.entity.ErrorCode;
import com.example.error_code_manager.repository.ErrorCodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ErrorCodeService {
    private final ErrorCodeRepository errorCodeRepository;

    public ErrorCodeService(ErrorCodeRepository errorCodeRepository) {
        this.errorCodeRepository = errorCodeRepository;
    }

    public List<ErrorCode> getAllErrorCodes() {
        return errorCodeRepository.findAll();
    }

    public Optional<ErrorCode> getErrorCodeById(Integer errorCodeId) {
        return errorCodeRepository.findById(errorCodeId);
    }

    public List<ErrorCode> getErrorCodesByConditionId(String conditionId) {
        return errorCodeRepository.findByConditionId(conditionId);
    }

    public List<ErrorCode> getErrorCodesByComponent(String component) {
        return errorCodeRepository.findByComponent(component);
    }

    public List<ErrorCode> getErrorCodesById(String idValue) {
        return errorCodeRepository.findByIdValue(idValue);
    }

    public List<ErrorCode> getErrorCodesByVersionId(Integer versionId) {
        return errorCodeRepository.findByVersionsVersionId(versionId);
    }

    public void deleteErrorCode(Integer errorCodeId) {
        errorCodeRepository.deleteById(errorCodeId);
    }

    public ErrorCode createErrorCode(ErrorCode errorCode) {
        if (errorCode.getErrorCodeId() != null && 
            errorCodeRepository.existsById(errorCode.getErrorCodeId())) {
            throw new IllegalArgumentException("Error code with ID " + 
                errorCode.getErrorCodeId() + " already exists");
        }
        return errorCodeRepository.save(errorCode);
    }
}