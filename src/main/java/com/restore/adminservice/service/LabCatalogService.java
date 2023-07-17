package com.restore.adminservice.service;

import com.restore.core.dto.app.LabCatalog;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;

public interface LabCatalogService {
    void add(LabCatalog labCatalog) throws RestoreSkillsException;

    LabCatalog getLabCatalog(Long id) throws RestoreSkillsException;

    void updateLabCatalog(LabCatalog labCatalog) throws RestoreSkillsException;

    Page<LabCatalog> getAllLabCatalog(int page, int size, String sortBy, String sortDirection);

    void updateStatus(Long id, boolean active) throws RestoreSkillsException;
}
