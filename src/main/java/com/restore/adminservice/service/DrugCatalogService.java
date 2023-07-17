package com.restore.adminservice.service;

import com.restore.core.dto.app.DrugCatalog;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;

public interface DrugCatalogService {
    void add(DrugCatalog drugCatalog) throws RestoreSkillsException;

    DrugCatalog getDrugCatalog(Long id) throws RestoreSkillsException;

    Page<DrugCatalog> getAllDrugCatalog(int page, int size, String sortBy, String sortDirection);
    
    void updateDrugCatalog(DrugCatalog drugCatalog) throws RestoreSkillsException;

    void updateStatus(long id, boolean active) throws RestoreSkillsException;
}
