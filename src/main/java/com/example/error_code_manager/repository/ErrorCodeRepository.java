package com.example.error_code_manager.repository;

import com.example.error_code_manager.entity.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ErrorCodeRepository extends JpaRepository<ErrorCode, Integer> {
    List<ErrorCode> findByConditionId(String conditionId);
    List<ErrorCode> findByComponent(String component);
    List<ErrorCode> findByIdValue(String idValue);
    List<ErrorCode> findByVersionsVersionId(Integer versionId);
}