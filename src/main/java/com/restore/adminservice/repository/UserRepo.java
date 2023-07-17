package com.restore.adminservice.repository;

import com.restore.core.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByIamId(String iamId);

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByUuid(UUID uuid);
}
