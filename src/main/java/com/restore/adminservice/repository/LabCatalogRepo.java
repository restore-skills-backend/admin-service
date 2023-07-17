package com.restore.adminservice.repository;

import com.restore.core.entity.LabCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabCatalogRepo extends JpaRepository<LabCatalogEntity,Long> {
}
