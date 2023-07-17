package com.restore.adminservice.repository;

import com.restore.core.entity.DiagnosisCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosisCodeRepo extends JpaRepository<DiagnosisCodeEntity,Long> {
}
