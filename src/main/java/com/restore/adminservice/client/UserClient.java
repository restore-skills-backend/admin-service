package com.restore.adminservice.client;

import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.app.User;
import com.restore.core.dto.response.Response;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "restore-user-service", path = "/api/user")
public interface UserClient {

    @PostMapping("/create/{providerGroupId}")
    ResponseEntity<Response> signup(@RequestHeader(name = "X-TENANT-ID") String requester, @Valid @RequestBody User user, @PathVariable UUID providerGroupId) throws RestoreSkillsException;

    @PostMapping("/provider-group")
    ResponseEntity<Response> createProviderGroup(@RequestHeader(name = "X-TENANT-ID") String requester,@Valid @RequestBody ProviderGroup providerGroup);

    @GetMapping("/all")
    ResponseEntity<Response> getAllUsers(@RequestHeader(name = "X-TENANT-ID") String requester, @RequestParam int page, @RequestParam int size);


}
