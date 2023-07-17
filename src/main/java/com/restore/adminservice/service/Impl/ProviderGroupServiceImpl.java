package com.restore.adminservice.service.Impl;

import com.restore.adminservice.client.ProviderClient;
import com.restore.adminservice.client.UserClient;
import com.restore.adminservice.repository.ProviderGroupRepo;
import com.restore.adminservice.repository.SpecialityRepo;
import com.restore.adminservice.service.ProviderGroupService;
import com.restore.adminservice.service.SchemaService;
import com.restore.adminservice.service.IamService;
import com.restore.core.dto.app.PracticeHour;
import com.restore.core.dto.app.Provider;
import com.restore.core.dto.app.ProviderGroup;
import com.restore.core.dto.app.Speciality;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.AddressEntity;
import com.restore.core.entity.PracticeHoursEntity;
import com.restore.core.entity.ProviderGroupEntity;
import com.restore.core.entity.SpecialityEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import com.restore.core.service.AwsService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProviderGroupServiceImpl extends AppService implements ProviderGroupService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private IamService iamService;

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private ProviderGroupRepo providerGroupRepo;

    @Autowired
    private SpecialityRepo specialityRepo;

    @Autowired
    private S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    private AwsService awsService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private ProviderClient providerClient;

    private Optional<ProviderGroupEntity> getById(UUID uuid) {
        return providerGroupRepo.findByUuid(uuid);
    }

    private Optional<ProviderGroupEntity> getBySubdomain(String subdomain){
        return providerGroupRepo.findBySubdomain(subdomain);
    }

    private ProviderGroupEntity getEntityBySubdomain(String subdomain) throws RestoreSkillsException {
        Optional<ProviderGroupEntity> existing = getBySubdomain(subdomain);
        if (!existing.isPresent()){
            throwError(ResponseCode.BAD_REQUEST, "Invalid Provider Group subdomain : " + subdomain);
        }
        return existing.get();
    }

    private ProviderGroupEntity getEntity(UUID uuid) throws RestoreSkillsException {
        Optional<ProviderGroupEntity> existing = getById(uuid);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid Provider Group ID : " + uuid);
        }

        return existing.get();
    }

    private ProviderGroupEntity getEntityByGroupName(String groupName) throws RestoreSkillsException {
        Optional<ProviderGroupEntity> existing = providerGroupRepo.findByDbSchema(groupName);

        if (existing.isEmpty()) {
            throwError(ResponseCode.BAD_REQUEST, "Invalid Provider Group Name : " + groupName);
        }

        return existing.get();
    }

    private void save(ProviderGroupEntity providerGroup) {
        providerGroupRepo.save(providerGroup);
    }

    private String getTenantKey(ProviderGroup providerGroup) {
        return providerGroup.getName().replaceAll("[\\s\\p{P}.]", "").toLowerCase();
    }

    private Set<PracticeHoursEntity> toPracticeHoursEntity(Set<PracticeHour> practiceHours) {
        return practiceHours.stream().map(practiceHour -> modelMapper.map(practiceHour, PracticeHoursEntity.class)).collect(Collectors.toSet());
    }

    private Set<SpecialityEntity> toSpecialityEntity(Set<Speciality> specialities){
        return specialities.stream().map(speciality ->
                specialityRepo.findByName(speciality.getName())).collect(Collectors.toSet());
    }

    private ProviderGroup toProviderGroup(ProviderGroupEntity providerGroup) {
        return modelMapper.map(providerGroup, ProviderGroup.class);
    }

    @Override
    public void add(ProviderGroup providerGroup) throws RestoreSkillsException, IOException {
        String tenantKey = getTenantKey(providerGroup);

        ProviderGroupEntity providerGroupEntity = modelMapper.map(providerGroup, ProviderGroupEntity.class);

        providerGroupEntity.setDbSchema(tenantKey);
        providerGroupEntity.setIamGroup(tenantKey);
        providerGroupEntity.setSubdomain(tenantKey);

        providerGroupEntity.setSpecialities(toSpecialityEntity(providerGroup.getSpecialities()));
        providerGroupEntity.setPracticeHours(toPracticeHoursEntity(providerGroup.getPracticeHours()));

       try {
            // TODO : Refactor to make it Atomic function
            // Step 1 : Save to Database
            providerGroupEntity.setCreatedBy(getCurrentUser().getIamId());
            providerGroupEntity.setModifiedBy(getCurrentUser().getIamId());
            providerGroupEntity.setUuid(UUID.randomUUID());
            String profilePhotoKey=null;
            if (Objects.nonNull(providerGroup.getAvatar())) {
                profilePhotoKey = awsService.uploadProviderGroupsProfilePhoto(providerGroup.getName(),providerGroup.getAvatar(),bucketName,null);
                providerGroupEntity.setAvatar(profilePhotoKey);
            }
            save(providerGroupEntity);
            // Step 2 : Save to IAM
            iamService.createIAMGroup(tenantKey);

            // Step 3 : Create Schema per Tenant
            schemaService.createSchemaAndTables(tenantKey);

            //Step 4 : Insert provider group into particular schema
//            createProviderGroupInUser(tenantKey,providerGroup);


       } catch (Exception e) {
           e.printStackTrace();
           throwError(ResponseCode.DB_ERROR, "Failed to add Provider Group " + providerGroup.getName());
       }
    }

//    private void createProviderGroupInUser(String tenantKey, ProviderGroup providerGroup) throws RestoreSkillsException {
//        ResponseEntity<Response> response = userClient.createProviderGroup(tenantKey,providerGroup);
//        if(!response.getBody().getCode().equals(ResponseCode.CREATED)){
//            throwError(ResponseCode.BAD_REQUEST,"Cannot create user");
//        }
//    }

    @Override
    public ProviderGroup get(UUID uuid) throws RestoreSkillsException, IOException {
//        User user = getCurrentUser();
        ProviderGroup providerGroup = toProviderGroup(getEntity(uuid));
        if(Objects.nonNull(providerGroup.getAvatar())){
            providerGroup.setAvatar(awsService.getPreSignedUrl(providerGroup.getAvatar()));
        }
        return providerGroup;
    }

    @Override
    public ProviderGroup getByName(String groupName) throws RestoreSkillsException {
        return toProviderGroup(getEntityByGroupName(groupName));
    }

    @Override
    public void update(ProviderGroup providerGroup) throws RestoreSkillsException, IOException {

        ProviderGroupEntity existingProviderGroupEntity = getEntity(providerGroup.getUuid());

        existingProviderGroupEntity.setName(providerGroup.getName());
        existingProviderGroupEntity.setContactNumber(providerGroup.getContactNumber());
        existingProviderGroupEntity.setSpecialities(toSpecialityEntity(providerGroup.getSpecialities()));
        existingProviderGroupEntity.setPracticeHours(toPracticeHoursEntity(providerGroup.getPracticeHours()));
        existingProviderGroupEntity.setNpiNumber(providerGroup.getNpiNumber());
        existingProviderGroupEntity.setEmail(providerGroup.getEmail());
        existingProviderGroupEntity.setWebsite(providerGroup.getWebsite());
        existingProviderGroupEntity.setFax(providerGroup.getFax());
        existingProviderGroupEntity.setDescription(providerGroup.getDescription());
        existingProviderGroupEntity.setPhysicalAddress(modelMapper.map(providerGroup.getPhysicalAddress(), AddressEntity.class));
        existingProviderGroupEntity.setBillingAddress(modelMapper.map(providerGroup.getBillingAddress(), AddressEntity.class));

        if(Objects.nonNull(providerGroup.getAvatar())  && Objects.nonNull(providerGroup.getNewAvatar())){
            awsService.deleteProfilePhoto(existingProviderGroupEntity.getAvatar(),bucketName);
            existingProviderGroupEntity.setAvatar(awsService.uploadProviderGroupsProfilePhoto(existingProviderGroupEntity.getName(),providerGroup.getNewAvatar(),bucketName, existingProviderGroupEntity.getAvatar()));
        } else if (Objects.isNull(providerGroup.getAvatar()) && Objects.nonNull(providerGroup.getNewAvatar())) {
            existingProviderGroupEntity.setAvatar(awsService.uploadProviderGroupsProfilePhoto(existingProviderGroupEntity.getName(),providerGroup.getNewAvatar(),bucketName, existingProviderGroupEntity.getAvatar()));
        } else if (Objects.isNull(providerGroup.getAvatar()) && Objects.nonNull(existingProviderGroupEntity.getAvatar())) {
            awsService.deleteProfilePhoto(existingProviderGroupEntity.getAvatar(),bucketName);
            existingProviderGroupEntity.setAvatar(null);
        }
        try {
            save(existingProviderGroupEntity);
        } catch (Exception e) {
            throwError(ResponseCode.DB_ERROR, "Failed to update Provider Group " + providerGroup.getName());
        }

    }

    @Override
    public void remove(UUID uuid) throws RestoreSkillsException {
        ProviderGroupEntity providerGroupEntity = getEntity(uuid);
        providerGroupEntity.setActive(true);
        providerGroupEntity.setArchive(true);
        providerGroupRepo.save(providerGroupEntity);
    }


    @Override
    public Page<ProviderGroup> getAll(int page, int size , String sortBy , String sortDirection, String searchString, Boolean active, String state) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (Objects.nonNull(sortDirection) && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size);

        List<ProviderGroup> providerGroupPage = null;
        {
//            Pageable queryPagination = PageRequest.of(page, size * 20, Sort.by(direction, sortBy));
            providerGroupPage = providerGroupRepo.findBySearchCriteria(active, state, searchString).stream().map(this::toProviderGroup).toList();
            providerGroupPage.stream()
                    .filter(providerGroup -> Objects.nonNull(providerGroup.getAvatar()))
                    .forEach(providerGroup -> {
                        try {
                            providerGroup.setAvatar(awsService.getPreSignedUrl(providerGroup.getAvatar()));
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage());
                        }
                    });
        }

        final int start = (int) pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), providerGroupPage.size());

        return new PageImpl<>(providerGroupPage.subList(start, end), pageable, providerGroupPage.size());
    }

    @Override
    public void updateStatus(UUID uuid, boolean active) throws RestoreSkillsException {
        ProviderGroupEntity providerGroupEntity = getEntity(uuid);
        providerGroupEntity.setActive(active);
        providerGroupRepo.save(providerGroupEntity);
    }

    @Override
    public void syncSchema(UUID uuid) throws RestoreSkillsException {
        ProviderGroupEntity providerGroupEntity = getEntity(uuid);
        schemaService.createSchemaAndTables(providerGroupEntity.getDbSchema());
    }

    @Override
    public void createProviderGroupUsers(Provider provider, UUID providerGroupId) throws RestoreSkillsException, IOException {
        ProviderGroup providerGroup = get(providerGroupId);
        ResponseEntity<Response> response = providerClient.createProvider(providerGroup.getDbSchema(), providerGroupId, provider);

        if (!response.getBody().getCode().equals(ResponseCode.CREATED))
            throwError(ResponseCode.BAD_REQUEST, "Problem occurred while creating the user");
    }

    @Override
    public void updateProviderGroupUsers(Provider provider, UUID providerGroupId, UUID providerId) throws RestoreSkillsException, IOException {
        ProviderGroup providerGroup = get(providerGroupId);
        ResponseEntity<Response> response = providerClient.update(providerGroup.getDbSchema(), provider, providerId);

        if (!response.getBody().getCode().equals(ResponseCode.OK))
            throwError(ResponseCode.BAD_REQUEST, "Problem occurred while updating the user");
    }

    @Override
    public Object getAllProvidersForProviderGroup(UUID providerGroupId, int page, int size) throws RestoreSkillsException, IOException {
        ProviderGroup providerGroup = get(providerGroupId);
        ResponseEntity<Response> response = providerClient.getAllProviders(providerGroup.getDbSchema(), page, size);

        if (!response.getBody().getCode().equals(ResponseCode.OK))
            throwError(ResponseCode.BAD_REQUEST, "Problem occurred while updating the user");

        return response.getBody().getData();
    }

    @Override
    public Object getProviderForProviderGroup(UUID providerGroupId, UUID providerId) throws RestoreSkillsException, IOException {
        ProviderGroup providerGroup = get(providerGroupId);
        ResponseEntity<Response> response = providerClient.get(providerGroup.getDbSchema(), providerId);

        if (!response.getBody().getCode().equals(ResponseCode.OK))
            throwError(ResponseCode.BAD_REQUEST, "Problem occurred while updating the user");

        return response.getBody().getData();
    }

    @Override
    public ProviderGroup getProviderGroupBySubdomain(String subdomain) throws RestoreSkillsException, IOException {
        ProviderGroup providerGroup = toProviderGroup(getEntityBySubdomain(subdomain));
        if(Objects.nonNull(providerGroup.getAvatar())){
            providerGroup.setAvatar(awsService.getPreSignedUrl(providerGroup.getAvatar()));
        }
        return providerGroup;
    }

    public Page<ProviderGroupEntity> getAllAuthProviderGroups(int page, int size){
        return providerGroupRepo.findAll(PageRequest.of(page, size));
    }

}