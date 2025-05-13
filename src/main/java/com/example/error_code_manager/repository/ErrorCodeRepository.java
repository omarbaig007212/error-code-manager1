package com.example.error_code_manager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.error_code_manager.entity.ErrorCode;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Integer> {  // Changed from String to Integer
    Optional<ErrorCode> findByErrorCodeId(String errorCodeId);  // Changed to Optional
    List<ErrorCode> findAllByErrorCodeId(String errorCodeId);  // For getting all matches
    List<ErrorCode> findByConditionId(String conditionId);
    List<ErrorCode> findByComponent(String component);
    List<ErrorCode> findByVersionsVersionId(Integer versionId);
}