package com.restore.adminservice.repository;

import com.restore.core.entity.ProviderGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository

public interface ProviderGroupRepo extends JpaRepository<ProviderGroupEntity, Long> {

    Optional<ProviderGroupEntity> findByDbSchema(String schemaName);
    Optional<ProviderGroupEntity> findByUuid(UUID uuid);

    @Query(value = "SELECT pg FROM ProviderGroupEntity pg " +
            "LEFT JOIN pg.physicalAddress pa " +
            "LEFT JOIN pg.specialities s " +
            "WHERE (?1 IS NULL OR pg.active = ?1) " +
            "AND (?2 IS NULL OR LOWER(pa.state) = LOWER(?2)) " +
            "AND (?3 IS NULL OR LOWER(pg.name) LIKE LOWER(CONCAT('%', ?3, '%')) " +
            "OR LOWER(pg.contactNumber) LIKE LOWER(CONCAT('%', ?3, '%')) " +
            "OR LOWER(s.name) LIKE LOWER(CONCAT('%', ?3, '%'))) ")
    List<ProviderGroupEntity> findBySearchCriteria(
            @Param("active") Boolean active,
            @Param("state") String state,
            @Param("searchString") String searchString);

    Optional<ProviderGroupEntity> findBySubdomain(String subdomain);


    @Query(value = "SELECT pg FROM ProviderGroupEntity pg " +
            "LEFT JOIN pg.physicalAddress pa " +
            "LEFT JOIN pg.specialities s " +
            "WHERE (?1 IS NULL OR pg.active = ?1) " +
            "AND (?2 IS NULL OR LOWER(pa.state) = LOWER(?2)) " +
            "AND (?3 IS NULL OR LOWER(pg.name) LIKE LOWER(CONCAT('%', ?3, '%')) " +
            "OR LOWER(pg.contactNumber) LIKE LOWER(CONCAT('%', ?3, '%')) " +
            "OR LOWER(s.name) LIKE LOWER(CONCAT('%', ?3, '%')))")
    List<ProviderGroupEntity> countBySearchCriteria(
            Boolean active,
            String state,
            String searchString);
}
