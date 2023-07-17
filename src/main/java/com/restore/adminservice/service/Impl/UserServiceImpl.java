package com.restore.adminservice.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restore.adminservice.client.CommunicationClient;
import com.restore.adminservice.client.UserClient;
import com.restore.adminservice.dto.PaginatedUserResponse;
import com.restore.adminservice.dto.PasswordChange;
import com.restore.adminservice.dto.SetPasswordEmailRequest;
import com.restore.adminservice.repository.SpecialityRepo;
import com.restore.adminservice.repository.UserRepo;
import com.restore.adminservice.service.ProviderGroupService;
import com.restore.adminservice.service.UserService;
import com.restore.adminservice.service.IamService;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.app.Speciality;
import com.restore.core.dto.app.User;
import com.restore.core.dto.app.enums.Roles;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.UserEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.core.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl extends AppService implements UserService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private SpecialityRepo specialityRepo;

    @Autowired
    private IamService iamService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CommunicationClient communicationClient;

    @Autowired
    private ProviderGroupService providerGroupService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AwsService awsService;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private Optional<UserEntity> getById(UUID uuid) {
        return userRepo.findByUuid(uuid);
    }

    private Optional<UserEntity> getByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    private UserEntity getEntity(UUID uuid) throws RestoreSkillsException {
        Optional<UserEntity> existing = getById(uuid);
        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User ID : " + uuid);
        }
        return existing.get();
    }

    private User getUserByEmail(String email) throws RestoreSkillsException {
        Optional<UserEntity> existing = getByEmail(email);
        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User Email : " + email);
        }
        return toUser(existing.get());
    }

    private UserEntity getEntityByEmail(String email) throws RestoreSkillsException {
        Optional<UserEntity> existing = getByEmail(email);
        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User email : " + email);
        }
        return existing.get();
    }

    private User getIamUserByEmail(String email) throws RestoreSkillsException {
        Optional<User> optEmail = iamService.findByEmail(email);
        if (!optEmail.isPresent()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid User email : " + email);
        }
        return optEmail.get();
    }

    private User getIamUserById(String id) throws RestoreSkillsException {
        return iamService.findById(id);
    }

    @Override
    public User getProfile() throws RestoreSkillsException, IOException {
        User existingUser = getCurrentUser();
        User iamUser = getIamUserByEmail(existingUser.getEmail());
        User user = getUserByEmail(existingUser.getEmail());
        if (Objects.nonNull(user.getAvatar())){
            String preSignedUrl = awsService.getPreSignedUrl(user.getAvatar());
            user.setAvatar(preSignedUrl);
        }
        iamUser.setUuid(user.getUuid());
        return iamUser;
    }

    @Override
    public String updateProfile(User user) throws RestoreSkillsException {
        UserEntity existingUserEntity = getEntity(user.getUuid());
        user.setIamId(existingUserEntity.getIamId());

        if(Objects.nonNull(user.getAvatar())  && Objects.nonNull(user.getNewAvatar())){
            awsService.deleteProfilePhoto(existingUserEntity.getAvatar(),bucketName);
            existingUserEntity.setAvatar(awsService.uploadAdminProfilePhoto(user.getNewAvatar(), existingUserEntity.getAvatar()));
        } else if (Objects.isNull(user.getAvatar()) && Objects.nonNull(user.getNewAvatar())) {
            existingUserEntity.setAvatar(awsService.uploadAdminProfilePhoto(user.getNewAvatar(), existingUserEntity.getAvatar()));
        } else if (Objects.isNull(user.getAvatar())  && Objects.isNull(user.getNewAvatar())) {
            awsService.deleteProfilePhoto(existingUserEntity.getAvatar(),bucketName);
            existingUserEntity.setAvatar(null);
        }
        saveUser(existingUserEntity);
        return iamService.updateUser(user);
    }


    @Override
    public Boolean changePassword(PasswordChange passwordChange) throws RestoreSkillsException {
        UserEntity userEntity = getEntity(passwordChange.getUuid());
        return iamService.resetPassword(userEntity.getIamId(), passwordChange.getNewPassword());
    }

    @Override
    public void addUser(User user) throws RestoreSkillsException, IOException {
        log.info("Received request for creating User : {}", user.getEmail());

//         Validate User
        if (getByEmail(user.getEmail()).isPresent())
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, "User with this Email ID already exist");

//         Add User to IAM and Database
        Optional<User> optExistingUser = iamService.findByEmail(user.getEmail());
        User userInfo = null;
        if (optExistingUser.isPresent()) {
            // User present in IAM, but not in Database. Add User in Database
            User iamUser = optExistingUser.get();
            if (Objects.nonNull(user.getAvatar())){
                String avatarKey = awsService.uploadAdminProfilePhoto(user.getAvatar(),null);
                iamUser.setAvatar(avatarKey);
            }
            userInfo = saveUser(toUserEntity(iamUser));
        } else {
            // Add User to IAM
            String iamId = iamService.addUser(user);
            log.info("IAM Id : {}", iamId);
            if (StringUtils.isNotBlank(iamId)) {
                user.setIamId(iamId);
                if (Objects.nonNull(user.getAvatar())){
                    String avatarKey = awsService.uploadAdminProfilePhoto( user.getAvatar(),null);
                    user.setAvatar(avatarKey);
                }
                userInfo = saveUser(toUserEntity(user));
            }
        }

        // Send Invitation Email for set password
        SetPasswordEmailRequest setPasswordEmailRequest = SetPasswordEmailRequest.builder()
                .email(user.getEmail())
                .name(user.getFirstName() + " " + user.getLastName())
                .uuid(userInfo.getUuid())
                .build();
        sendEmailForSetPassword(setPasswordEmailRequest);
    }


    private void sendEmailForSetPassword(SetPasswordEmailRequest setPasswordEmailRequest) throws RestoreSkillsException {
        try {
            communicationClient.sendEmailForSetPassword("public", setPasswordEmailRequest);
        } catch (Exception exception) {
            throwError(ResponseCode.BAD_REQUEST, "Failed to send email ");
        }
    }

    @Override
    public PaginatedUserResponse getUsers(int page, int size) throws RestoreSkillsException, IOException {
        List<User> userList = iamService.getAllUsersByRole(Roles.ADMIN_USER.name(),page,size);
        List<User> users = new ArrayList<>();
        for (User user : userList) {
            User userInfo = getUserByEmail(user.getEmail());
            user.setUuid(userInfo.getUuid());
            if (Objects.nonNull(userInfo.getAvatar())){
                String preSignedUrl = awsService.getPreSignedUrl(userInfo.getAvatar());
                user.setAvatar(preSignedUrl);
            }
            users.add(user);
        }
        long totalUsers =  iamService.getAllUserSizeByRole(Roles.ADMIN_USER.name());
        int totalPages = (int) Math.ceil((double) totalUsers / size);
        return new PaginatedUserResponse(users, page, totalPages, totalUsers);
    }

    @Override
    public void activateUser(String email, boolean status) throws RestoreSkillsException {
        UserEntity userEntity = getEntityByEmail(email);
        iamService.enableUser(userEntity.getIamId(), status);
    }

    private User toUser(UserEntity userEntity) {
        return modelMapper.map(userEntity, User.class);
    }

    private UserEntity toUserEntity(User user) throws RestoreSkillsException {
        User currentUser = getCurrentUser();
        return UserEntity.builder()
                .email(user.getEmail())
                .iamId(user.getIamId())
                .avatar(user.getAvatar())
                .createdBy(currentUser.getIamId())
                .modifiedBy(currentUser.getIamId())
                .build();
    }

    private User saveUser(UserEntity userEntity) {
        return toUser(userRepo.save(userEntity));
    }

    @Override
    public List<Speciality> getAllSpecialities() {
        return specialityRepo.findAll().stream().map(specialityType -> modelMapper.map(specialityType, Speciality.class) ).collect(Collectors.toList());
    }

    @Override
    public User getUser(String email) throws RestoreSkillsException {
        User user = getUserByEmail(email);
        User userInfo = getIamUserById(user.getIamId());
        userInfo.setUuid(user.getUuid());
        return userInfo;
    }

    @Override
    public User getUserById(UUID id) throws RestoreSkillsException {
        UserEntity userEntity = getEntity(id);
        User userInfo = getIamUserById(userEntity.getIamId());
        userInfo.setUuid(userEntity.getUuid());
        return userInfo;
    }

    @Override
    public List<User> getProviderGroupUsers(UUID providerGroupId, int page, int size) throws RestoreSkillsException, IOException {
        ProviderGroup providerGroup = providerGroupService.get(providerGroupId);
        List<User> userList = new ArrayList<>();
        ResponseEntity<Response> response = userClient.getAllUsers(providerGroup.getDbSchema(), page, size);

        if (!response.getBody().getCode().equals(ResponseCode.OK))
            throwError(ResponseCode.DB_ERROR, "Error occur while fetching all the users from database!");


        List<UserEntity> yourList = List.of(objectMapper.convertValue(response.getBody().getData(), UserEntity[].class));

        for (UserEntity userEntity : yourList) {
            User user = iamService.findById(userEntity.getIamId());
            userList.add(user);
        }

        return userList;
    }



}
