package com.restore.adminservice.dto;

import com.restore.core.dto.app.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedUserResponse {

    private List<User> users;

    private int currentPage;

    private int totalPages;

    private long totalUsers;
}
