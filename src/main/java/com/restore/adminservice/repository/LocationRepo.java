package com.restore.adminservice.repository;

import com.restore.core.entity.LocationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocationRepo extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findByUuid(UUID uuid);
}
