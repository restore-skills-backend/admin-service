package com.restore.adminservice.client;


import com.restore.adminservice.dto.SetPasswordEmailRequest;

import com.restore.core.dto.response.Response;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "restore-notification-service", path = "/api/notification")
public interface CommunicationClient {

    @PostMapping("/email/set-password")
    ResponseEntity<Response> sendEmailForSetPassword(@RequestHeader(name = "X-TENANT-ID") String requester, @Valid @RequestBody SetPasswordEmailRequest setPasswordEmailRequest);

}
