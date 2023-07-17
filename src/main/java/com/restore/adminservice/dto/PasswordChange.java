package com.restore.adminservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChange {

    private UUID uuid;

    @NotBlank(message = "old password is mandatory")
    private String oldPassword;

    @NotBlank(message = "new password is mandatory")
    private String newPassword;


}
