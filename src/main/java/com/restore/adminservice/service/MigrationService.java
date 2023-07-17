package com.restore.adminservice.service;

import com.restore.core.exception.RestoreSkillsException;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MigrationService {
    void uploadLabCatalog(InputStream file) throws RestoreSkillsException;

    void uploadDrugCatalog(InputStream inputStream) throws RestoreSkillsException;

    void uploadRadiologyCatalog(InputStream inputStream) throws RestoreSkillsException;

    void uploadDiagnosisCode(InputStream inputStream) throws RestoreSkillsException;

    void uploadProceduralCode(InputStream inputStream) throws RestoreSkillsException;
}
