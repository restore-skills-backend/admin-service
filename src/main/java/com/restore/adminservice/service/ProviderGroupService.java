package com.restore.adminservice.service;

import com.restore.core.dto.app.Provider;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.entity.ProviderGroupEntity;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.util.UUID;

public interface ProviderGroupService {

    void add(ProviderGroup providerGroupDTO) throws RestoreSkillsException, IOException;

    ProviderGroup get(UUID uuid) throws RestoreSkillsException, IOException;

    ProviderGroup getByName(String groupName) throws RestoreSkillsException;

    void update(ProviderGroup providerGroup) throws RestoreSkillsException, IOException;

    void remove(UUID uuid) throws RestoreSkillsException;

    Page<ProviderGroup> getAll(int page, int size, String sortBy, String sortDirection, String searchString, Boolean active, String state);

    void updateStatus(UUID uuid, boolean active) throws RestoreSkillsException;

    void syncSchema(UUID uuid) throws RestoreSkillsException;

    void createProviderGroupUsers(Provider provider, UUID providerGroupId) throws RestoreSkillsException, IOException;

    void updateProviderGroupUsers(Provider provider, UUID providerGroupId, UUID providerId) throws RestoreSkillsException, IOException;

    Object getAllProvidersForProviderGroup(UUID providerGroupId, int page, int size) throws RestoreSkillsException, IOException;

    Object getProviderForProviderGroup(UUID providerGroupId, UUID providerId) throws RestoreSkillsException, IOException;

    ProviderGroup getProviderGroupBySubdomain(String subdomain) throws RestoreSkillsException, IOException;
    Page<ProviderGroupEntity> getAllAuthProviderGroups(int page, int size);
}
