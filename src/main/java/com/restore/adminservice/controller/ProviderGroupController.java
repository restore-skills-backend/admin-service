package com.restore.adminservice.controller;

import com.restore.adminservice.service.ProviderGroupService;
import com.restore.adminservice.service.UserService;
import com.restore.adminservice.service.IamService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.Provider;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/admin/provider-group")
public class ProviderGroupController extends AppController {

    private final ProviderGroupService providerGroupService;

    private final UserService userService;

    @Autowired
    public ProviderGroupController(final IamService iamService, final ProviderGroupService providerGroupService,
            UserService userService) {
        this.providerGroupService = providerGroupService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Response> createProviderGroup(@RequestBody @Valid ProviderGroup providerGroup)
            throws RestoreSkillsException, IOException {
        providerGroupService.add(providerGroup);
        return success(ResponseCode.CREATED, "Provider group created successfully");
    }

    @GetMapping({ "/{id}", "/auth/{id}" })
    public ResponseEntity<Response> getProviderGroupById(@PathVariable("id") UUID uuid)
            throws RestoreSkillsException, IOException {
        return data(providerGroupService.get(uuid));
    }

    // @GetMapping("/schema/{groupName}")
    // public ResponseEntity<Response>
    // getProviderGroupByName(@PathVariable("groupName") String groupName) throws
    // RestoreSkillsException {
    // return data(providerGroupService.getByName(groupName));
    // }

    @PutMapping
    public ResponseEntity<Response> updateProviderGroup(@RequestBody @Valid ProviderGroup providerGroup)
            throws RestoreSkillsException, IOException {
        providerGroupService.update(providerGroup);
        return success(ResponseCode.OK, "Provider group updated successfully");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response> deleteProviderGroup(@PathVariable("id") UUID uuid) throws RestoreSkillsException {
        providerGroupService.remove(uuid);
        return success(ResponseCode.OK, "Provider group deleted successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllProviderGroups(@RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "created") String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "desc") String sortDirection,
            @RequestParam(value = "searchString", required = false) String searchString,
            @RequestParam(value = "status", required = false) Boolean active,
            @RequestParam(value = "state", required = false) String state) {
        return data(providerGroupService.getAll(page, size, sortBy, sortDirection, searchString, active, state));
    }

    @PatchMapping("/{id}/active/{active}")
    public ResponseEntity<Response> updateStatus(@PathVariable("id") String id, @PathVariable boolean active)
            throws RestoreSkillsException {
        try {
            UUID uuid = UUID.fromString(id);
            providerGroupService.updateStatus(uuid, active);
            return success(ResponseCode.OK, "Status for provider group updated successfully");
        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, "Please check for id");
        }
    }

    @PutMapping("/{id}/sync")
    public ResponseEntity<Response> syncDatabaseSchema(@PathVariable("id") UUID uuid) throws RestoreSkillsException {
        providerGroupService.syncSchema(uuid);
        return success(ResponseCode.OK, "Database Schema sync completed successfully");
    }

    @PostMapping("/user/{providerGroupId}")
    public ResponseEntity<Response> createProviderGroupUser(@PathVariable UUID providerGroupId,
            @RequestBody @Valid Provider provider) throws RestoreSkillsException, IOException {
        providerGroupService.createProviderGroupUsers(provider, providerGroupId);
        return success(ResponseCode.CREATED, "Provider group users created successfully");
    }

    @PutMapping("/{providerGroupId}/update/{providerId}")
    public ResponseEntity<Response> updateProviderGroupUser(@PathVariable UUID providerGroupId,
            @PathVariable UUID providerId, @RequestBody @Valid Provider provider)
            throws RestoreSkillsException, IOException {
        providerGroupService.updateProviderGroupUsers(provider, providerGroupId, providerId);
        return success(ResponseCode.CREATED, "Provider group users updated successfully");
    }

    @GetMapping("/{providerGroupId}/{providerId}")
    public ResponseEntity<Response> getProvider(@PathVariable UUID providerGroupId, @PathVariable UUID providerId)
            throws RestoreSkillsException, IOException {
        return data(ResponseCode.OK, "Provider found Successfully.",
                providerGroupService.getProviderForProviderGroup(providerGroupId, providerId));
    }

    @GetMapping("/{providerGroupId}/all")
    public ResponseEntity<Response> getAllProviders(@PathVariable UUID providerGroupId, @RequestParam("page") int page,
            @RequestParam("size") int size) throws RestoreSkillsException, IOException {
        return data(ResponseCode.OK, "Providers Found Successfully.",
                providerGroupService.getAllProvidersForProviderGroup(providerGroupId, page, size));
    }

    @GetMapping("/auth/all")
    public ResponseEntity<Response> getAllAuthProviderGroups(@RequestParam("page") int page,
            @RequestParam("size") int size) throws RestoreSkillsException {
        return data(ResponseCode.OK, "Providers Found Successfully.",
                providerGroupService.getAllAuthProviderGroups(page, size));
    }

    @GetMapping({ "/subdomain/{subdomain}", "/auth/subdomain/{subdomain}" })
    public ResponseEntity<Response> getProviderGroupBySubdomain(@PathVariable("subdomain") String subdomain)
            throws RestoreSkillsException, IOException {
        return data(providerGroupService.getProviderGroupBySubdomain(subdomain));
    }

    @GetMapping("/{providerGroupId}/users")
    public ResponseEntity<Response> getProviderGroupUsers(@PathVariable UUID providerGroupId, @RequestParam int page,
            @RequestParam int size) throws RestoreSkillsException, IOException {
        return data(userService.getProviderGroupUsers(providerGroupId, page, size));
    }
}