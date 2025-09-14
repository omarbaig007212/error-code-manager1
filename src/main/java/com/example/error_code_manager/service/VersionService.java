package com.example.error_code_manager.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.error_code_manager.entity.Product;
import com.example.error_code_manager.entity.Version;
import com.example.error_code_manager.repository.VersionRepository;

@Service
public class VersionService {
    private final VersionRepository versionRepository;

    public VersionService(VersionRepository versionRepository){
        this.versionRepository = versionRepository;
    }

    public List<Version> getAllVersions(){
        return versionRepository.findAll();
    }

    public Optional<Version> getVersionById(Integer versionId){
        return versionRepository.findById(versionId);
    }

    public List<Version> getVersionsByProduct(Product product){
        return versionRepository.findByProduct(product);
    }

    public List<Version> getVersionsByProductId(Integer productId){
        return versionRepository.findByProductProductId(productId);
    }
    
    public Version createVersion(Version version){
        return versionRepository.save(version);
    }

    public void deleteVersion(Integer versionId){
        versionRepository.deleteById(versionId);
    }
}
