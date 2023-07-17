package com.restore.adminservice.service;

import com.restore.adminservice.dto.PaginatedUserResponse;
import com.restore.adminservice.dto.PasswordChange;
import com.restore.core.dto.app.Speciality;
import com.restore.core.dto.app.User;
import com.restore.core.exception.RestoreSkillsException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface UserService {

    User getProfile() throws RestoreSkillsException, IOException;

    String updateProfile(User user) throws RestoreSkillsException;

    Boolean changePassword(PasswordChange passwordChange) throws RestoreSkillsException;

    void addUser(User user) throws RestoreSkillsException, IOException;

    PaginatedUserResponse getUsers(int page, int size) throws RestoreSkillsException, IOException;

    void activateUser(String email,boolean status) throws RestoreSkillsException;

    List<Speciality> getAllSpecialities();

    User getUser(String email) throws RestoreSkillsException;

    User getUserById(UUID id) throws RestoreSkillsException;

    List<User> getProviderGroupUsers(UUID providerGroupId, int page, int size) throws RestoreSkillsException, IOException;
}
