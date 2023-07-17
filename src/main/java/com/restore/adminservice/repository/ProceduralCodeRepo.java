package com.restore.adminservice.repository;

import com.restore.core.entity.ProceduralCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProceduralCodeRepo extends JpaRepository<ProceduralCodeEntity,Long> {
}
