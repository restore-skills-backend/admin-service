package com.restore.adminservice.controller;

import com.restore.adminservice.service.LocationService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.Location;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/provider-group/location")
public class LocationController extends AppController {

    private final LocationService locationService;

    @Autowired
    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @PostMapping("/provider-group-uuid/{providerGroupUuid}")
    public ResponseEntity<Response> addLocation(@PathVariable UUID providerGroupUuid, @RequestBody @Valid Location location) throws RestoreSkillsException {
        locationService.redirectAddLocation(providerGroupUuid, location);
        return success(ResponseCode.OK,"Location group is added successfully");
    }

    @GetMapping("/provider-group-uuid/{providerGroupUuid}")
    public ResponseEntity<Response> getAllLocationsByProvider(@PathVariable UUID providerGroupUuid, Pageable page) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Successfully fetched Locations for current provider", locationService.redirectToGetAllLocationsByProvider(providerGroupUuid, page).getBody().getData());
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<Response> getById(@PathVariable("locationId") UUID locationId) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Successfully fetched Location By Id.", locationService.getById(locationId));
    }

}
