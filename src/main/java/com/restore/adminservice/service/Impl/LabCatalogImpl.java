package com.restore.adminservice.service.Impl;

import com.restore.adminservice.repository.LabCatalogRepo;
import com.restore.adminservice.service.LabCatalogService;
import com.restore.core.dto.app.LabCatalog;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.LabCatalogEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class LabCatalogImpl extends AppService implements LabCatalogService {
    @Autowired
    private LabCatalogRepo labCatalogRepo;

    @Autowired
    private ModelMapper modelMapper;

    private LabCatalogEntity getById(Long id) throws RestoreSkillsException {
        return labCatalogRepo.findById(id).orElseThrow(()-> new RestoreSkillsException(ResponseCode.IAM_ERROR,"Canoot fin lob catalog with id : "+id));
    }
    private LabCatalog toLabCatalog(LabCatalogEntity labCatalogEntity) {
        return modelMapper.map(labCatalogEntity, LabCatalog.class);
    }

    @Override
    public void add(LabCatalog labCatalog) throws RestoreSkillsException {
        try {
            labCatalogRepo.save(modelMapper.map(labCatalog, LabCatalogEntity.class));
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public LabCatalog getLabCatalog(Long id) throws RestoreSkillsException {
//        Optional<LabCatalogEntity> labCatalogEntity = labCatalogRepo.findById(id);
//        if (labCatalogEntity.isEmpty()) {
//            throwError(ResponseCode.BAD_REQUEST, "Cannot find Lab Catalog with ID : " + id);
//        }
//        return modelMapper.map(labCatalogEntity, LabCatalog.class);
        return toLabCatalog(getById(id));
    }

    @Override
    public void updateLabCatalog(LabCatalog labCatalog) throws RestoreSkillsException {
        LabCatalogEntity labCatalogEntity = getById(labCatalog.getId());

        labCatalogEntity.setLoincNum(labCatalog.getLoincNum());
        labCatalogEntity.setDescription(labCatalog.getDescription());
        labCatalogEntity.setActive(labCatalog.isActive());

        labCatalogRepo.save(labCatalogEntity);
    }

    @Override
    public Page<LabCatalog> getAllLabCatalog(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (Objects.nonNull(sortDirection) && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size,Sort.by(direction,sortBy));
        Page<LabCatalogEntity> labCatalogPage = labCatalogRepo.findAll(pageable);
        return labCatalogPage.map(this::toLabCatalog);
    }

    @Override
    public void updateStatus(Long id, boolean active) throws RestoreSkillsException {
        LabCatalogEntity labCatalogEntity = getById(id);
        labCatalogEntity.setActive(active);
        labCatalogRepo.save(labCatalogEntity);
    }
}
