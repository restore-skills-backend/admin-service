package com.restore.adminservice.service;

import com.restore.core.dto.app.User;
import com.restore.core.exception.RestoreSkillsException;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Optional;

public interface IamService {

    void createIAMGroup(String tenantKey) throws RestoreSkillsException;

    String updateUser(User user) throws RestoreSkillsException;

    Boolean resetPassword(String iamId, String newPassword) throws RestoreSkillsException;

    Optional<User> findByEmail(String email) throws RestoreSkillsException;

    User findById(String iamId) throws RestoreSkillsException;

    void enableUser(String userId, boolean status) throws RestoreSkillsException;

    String addUser(User user) throws RestoreSkillsException;

    List<User> getAllUsersByRole(String role,int page,int size);

    long  getAllUserSizeByRole(String roleName);
}
