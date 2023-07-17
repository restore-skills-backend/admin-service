package com.restore.adminservice.service;

import com.restore.core.dto.app.Location;
import com.restore.core.dto.response.Response;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface LocationService {
    void redirectAddLocation(UUID providerGroupUuid, Location location) throws RestoreSkillsException;
    Location getById(UUID locationId) throws RestoreSkillsException;
    ResponseEntity<Response> redirectToGetAllLocationsByProvider(UUID providerGroupUuid, Pageable page) throws RestoreSkillsException;
}
