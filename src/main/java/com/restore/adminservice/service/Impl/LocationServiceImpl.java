package com.restore.adminservice.service.Impl;

//import com.restore.adminservice.client.AdminClient;

import com.restore.adminservice.client.ProviderClient;
import com.restore.adminservice.repository.LocationRepo;
import com.restore.adminservice.repository.ProviderGroupRepo;
import com.restore.adminservice.service.LocationService;
import com.restore.core.dto.app.Location;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.LocationEntity;
import com.restore.core.entity.ProviderGroupEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class LocationServiceImpl extends AppService implements LocationService {

    private final ModelMapper modelMapper;
    private final LocationRepo locationRepo;
    private final ProviderGroupRepo providerGroupRepo;
    private final ProviderClient providerClient;

    @Autowired
    public LocationServiceImpl(ModelMapper modelMapper, LocationRepo locationRepo, ProviderGroupRepo providerGroupRepo, ProviderClient providerClient) {
        this.modelMapper = modelMapper;
        this.locationRepo = locationRepo;
        this.providerGroupRepo = providerGroupRepo;
        this.providerClient = providerClient;
    }

    private Optional<LocationEntity> getLocationById(UUID uuid) {
        return locationRepo.findByUuid(uuid);
    }

    private LocationEntity getEntity(UUID uuid) throws RestoreSkillsException {
        Optional<LocationEntity> existing = getLocationById(uuid);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid Location ID : " + uuid);
        }

        return existing.get();
    }

    private Location toLocation(LocationEntity locationEntity){
        return modelMapper.map(locationEntity, Location.class);
    }

    @Override
    public void redirectAddLocation(UUID providerGroupUuid, Location location) throws RestoreSkillsException {
        Optional<ProviderGroupEntity> providerGroupEntity = providerGroupRepo.findByUuid(providerGroupUuid);
        try {
            providerClient.addLocation(providerGroupEntity.get().getDbSchema(), location);
        } catch (Exception ex) {
            throwError(ResponseCode.IAM_ERROR, ex.getMessage());
        }
    }

    @Override
    public Location getById(UUID locationId) throws RestoreSkillsException{
        return toLocation(getEntity(locationId));
    }

    @Override
    public ResponseEntity<Response> redirectToGetAllLocationsByProvider(UUID providerGroupUuid, Pageable page) throws RestoreSkillsException {
        Optional<ProviderGroupEntity> providerGroupEntity = providerGroupRepo.findByUuid(providerGroupUuid);
        return providerClient.getAllLocationsByProvider(providerGroupEntity.get().getDbSchema(), page);
    }
}
