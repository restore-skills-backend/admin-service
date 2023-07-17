package com.restore.adminservice.repository;

import com.restore.core.entity.DrugCatalogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrugCatalogRepo extends JpaRepository<DrugCatalogEntity,Long> {
}
