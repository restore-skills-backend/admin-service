package com.restore.adminservice.service.Impl;

import com.restore.adminservice.service.IamService;
import com.restore.core.dto.app.User;
import com.restore.core.dto.app.enums.Roles;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IamServiceImpl extends AppService implements IamService {

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private Keycloak keycloak;

    private UsersResource usersResource = null;
    private RolesResource rolesResource = null;

    @PostConstruct()
    private void init() {
        try {
            RealmResource realmResource = this.keycloak.realm(realm);
            usersResource = realmResource.users();
            rolesResource = realmResource.roles();
        } catch (Exception e) {
            log.error("Error while initiating IAM Configuration", e);
        }
    }

    @Override
    public void createIAMGroup(final String tenantKey) throws RestoreSkillsException {

        GroupRepresentation group = new GroupRepresentation();
        group.setName(tenantKey);

        Response response = keycloak.realm(realm).groups().add(group);
        if(response.getStatus() != 201) {
            throwError(ResponseCode.IAM_ERROR, "Failed to create IAM group with key "+tenantKey);
        }
    }

    @Override
    public String updateUser(User user) throws RestoreSkillsException {

        Optional<UserResource> optUserResource = getUser(user.getIamId());

        if (!optUserResource.isPresent())
            throwError(ResponseCode.IAM_ERROR, "Invalid User Id");

        UserResource userResource = optUserResource.get();

        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setFirstName(user.getFirstName());
        userRepresentation.setLastName(user.getLastName());
        userRepresentation.setAttributes(Map.of("phone", List.of(user.getPhone())));
        userRepresentation.setEnabled(user.isActive());

        userResource.update(userRepresentation);
        return userRepresentation.getId();

    }

    @Override
    public Boolean resetPassword(String iamId, String newPassword) throws RestoreSkillsException {

        Optional<UserResource> optUserResource = getUser(iamId);

        if (!optUserResource.isPresent())
            throwError(ResponseCode.IAM_ERROR, "Invalid User realm name");

        UserResource userResource = optUserResource.get();

        // Define password credential
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(newPassword);
        userResource.resetPassword(passwordCred);
        return true;
    }

    private Optional<UserResource> getUser(String iamId) {
        RealmResource realmResource = keycloak.realm(realm);
        return Optional.of(realmResource.users().get(iamId));
    }

    public String addUser(User user) throws RestoreSkillsException {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        RolesResource rolesResource = realmResource.roles();

        String userId = null;
        UserRepresentation iamUser = new UserRepresentation();
        iamUser.setUsername(user.getEmail());
        iamUser.setFirstName(user.getFirstName());
        iamUser.setLastName(user.getLastName());
        iamUser.setEmail(user.getEmail());
        iamUser.setEmailVerified(false);
        iamUser.setEnabled(true);
        iamUser.setAttributes(Map.of("phone", List.of(user.getPhone())));

        // Create user (requires manage-users role)
        Response response = null;
        try {
            response = usersResource.create(iamUser);
        } catch (Exception e) {
            throwError(ResponseCode.IAM_ERROR, e.getMessage());
        }

        log.info("User Create Response : {}", response.getStatusInfo());

        if (response.getStatus() == 201) {
            userId = CreatedResponseUtil.getCreatedId(response);
            log.info("Created IAM User with ID : {}", userId);

            UserResource userResource = usersResource.get(userId);

            // Assign Role to User
            try {
                RoleRepresentation realmRole = realmResource.roles().get(user.getRole().name()).toRepresentation();
                userResource.roles().realmLevel().add(Arrays.asList(realmRole));
            } catch (Exception e) {
                throwError(ResponseCode.IAM_ERROR, e.getMessage());
            }
        }
        return userId;
    }


    public void changePassword(String userId, String password) throws RestoreSkillsException {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);

        UserResource userResource = findByUserId(userId);

        try {
            userResource.resetPassword(passwordCred);
        } catch (Exception e) {
            throwError(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    private UserResource findByUserId(String userId) throws RestoreSkillsException {
        UserResource existingUser = null;
        try {
            existingUser = usersResource.get(userId);
        } catch (Exception e) {
            throwError(ResponseCode.IAM_ERROR, e.getMessage());
        }

        return Optional.of(existingUser).orElseThrow(
                () -> new RestoreSkillsException(ResponseCode.BAD_REQUEST, "Invalid User ID for IAM."));
    }

    @Override
    public Optional<User> findByEmail(String email) throws RestoreSkillsException {
        Optional<UserRepresentation> existingUser = Optional.empty();
        try {
            existingUser = usersResource.search(email).stream().findFirst();
        } catch (Exception e) {
            throwError(ResponseCode.IAM_ERROR, e.getMessage());
        }

        return existingUser.isPresent() ? existingUser.map(this::mapToUser) : Optional.empty();
    }

    @Override
    public User findById(String iamId) throws RestoreSkillsException {
        UserRepresentation userRepresentation = findByUserId(iamId).toRepresentation();
        return mapToUser(userRepresentation);
    }

    private User mapToUser(UserRepresentation existingUser) {
        return User.builder()
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .iamId(existingUser.getId())
                .active(existingUser.isEnabled())
                .email(existingUser.getEmail())
                .lastLogin(getLastLogin(existingUser.getId()))
                .phone((existingUser.getAttributes() != null && existingUser.getAttributes().containsKey("phone")) ? existingUser.firstAttribute("phone") : null)
                .build();
    }

    @Override
    public void enableUser(String userId, boolean status) throws RestoreSkillsException {
        UserResource userResource = findByUserId(userId);
        UserRepresentation userRepresentation = userResource.toRepresentation();
        userRepresentation.setEnabled(status);
        try {
            userResource.update(userRepresentation);
        } catch (Exception e) {
            throwError(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }


    public List<User> getAllUsersByRole(String roleName, int page, int size) {
        List<User> userList = new ArrayList<>();
        List<UserRepresentation> userRepresentationList = getAllUserByRole(roleName, page, size);
        for (UserRepresentation keycloakUser : userRepresentationList) {
            User user = mapToUser(keycloakUser);
            user.setRole(Roles.valueOf(roleName));
            userList.add(user);
        }
        return userList;
    }

    private List<UserRepresentation> getAllUserByRole(String roleName, int page, int size) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
        List<UserRepresentation> userRepresentations = usersResource.list().stream()
                .filter(user -> hasRole(usersResource, user, role.getId()))
                .collect(Collectors.toList());

        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, userRepresentations.size());
        if (startIndex >= userRepresentations.size()) {
            return new ArrayList<>();
        } else {
            return userRepresentations.subList(startIndex, endIndex);
        }
    }

    private boolean hasRole(UsersResource usersResource, UserRepresentation user, String roleId) {
        return usersResource.get(user.getId()).roles().realmLevel().listEffective().stream()
                .anyMatch(role -> role.getId().equals(roleId));
    }

    public long getAllUserSizeByRole(String roleName) {
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();

        RolesResource rolesResource = realmResource.roles();
        RoleRepresentation role = rolesResource.get(roleName).toRepresentation();
        List<UserRepresentation> userRepresentations = usersResource.list().stream()
                .filter(user -> hasRole(usersResource, user, role.getId()))
                .collect(Collectors.toList());

        return userRepresentations.size();
    }

    private Long getLastLogin(String iamId) {
        List<String> list = Collections.singletonList("LOGIN");
        List<EventRepresentation> events = keycloak.realm(realm).getEvents(list, null, iamId, null, null, null, null, null);
        if (!events.isEmpty()){
           return events.get(0).getTime();
        }
        return null;
    }
}
