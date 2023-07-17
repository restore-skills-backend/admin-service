package com.restore.adminservice.client;


import com.restore.core.dto.app.Location;
import com.restore.core.dto.app.Provider;
import com.restore.core.dto.response.Response;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "restore-provider-service", path = "/api/provider")
public interface ProviderClient {

    @PostMapping
    public ResponseEntity<Response> createProvider(@RequestHeader(name = "X-TENANT-ID") String requester, @RequestParam(value = "providerGroupId", required = false) UUID providerGroupId, @RequestBody @Valid Provider provider);

    @PutMapping("/{providerId}")
    ResponseEntity<Response> update(@RequestHeader(name = "X-TENANT-ID") String requester, @RequestBody Provider provider, @PathVariable UUID providerId) throws RestoreSkillsException;

    @GetMapping("/all")
    ResponseEntity<Response> getAllProviders(@RequestHeader(name = "X-TENANT-ID") String requester, @RequestParam("page") int page, @RequestParam("size") int size) throws RestoreSkillsException;

    @GetMapping("/{providerId}")
    ResponseEntity<Response> get(@RequestHeader(name = "X-TENANT-ID") String requester, @PathVariable UUID providerId) throws RestoreSkillsException;

    @PostMapping("/location/provider-group")
    ResponseEntity<Response> addLocation(@RequestHeader(name = "X-TENANT-ID") String requester, @RequestBody @Valid Location location) throws RestoreSkillsException;

    @GetMapping("/location/provider-group")
    ResponseEntity<Response> getAllLocationsByProvider(@RequestHeader(name = "X-TENANT-ID") String requester, Pageable page) throws RestoreSkillsException;

}
