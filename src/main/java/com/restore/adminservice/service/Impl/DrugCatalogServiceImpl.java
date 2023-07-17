package com.restore.adminservice.service.Impl;

import com.restore.adminservice.repository.DrugCatalogRepo;
import com.restore.adminservice.service.DrugCatalogService;
import com.restore.core.dto.app.DrugCatalog;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.DrugCatalogEntity;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class DrugCatalogServiceImpl extends AppService implements DrugCatalogService {
    @Autowired
    private DrugCatalogRepo drugCatalogRepo;

    @Autowired
    private ModelMapper modelMapper;

    private DrugCatalogEntity getById(Long id) throws RestoreSkillsException {
        return drugCatalogRepo.findById(id).orElseThrow(()-> new RestoreSkillsException(ResponseCode.IAM_ERROR,"Canoot find drug catalog with id : "+id));
    }
    private DrugCatalog toDrugCatalog(DrugCatalogEntity drugCatalogEntity) {
        return modelMapper.map(drugCatalogEntity, DrugCatalog.class);
    }

    @Override
    public void add(DrugCatalog drugCatalog) throws RestoreSkillsException {
        try {
            drugCatalogRepo.save(modelMapper.map(drugCatalog, DrugCatalogEntity.class));
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public DrugCatalog getDrugCatalog(Long id) throws RestoreSkillsException {
//        Optional<LabCatalogEntity> labCatalogEntity = labCatalogRepo.findById(id);
//        if (labCatalogEntity.isEmpty()) {
//            throwError(ResponseCode.BAD_REQUEST, "Cannot find Lab Catalog with ID : " + id);
//        }
//        return modelMapper.map(labCatalogEntity, LabCatalog.class);
        return toDrugCatalog(getById(id));
    }

    @Override
    public void updateDrugCatalog(DrugCatalog drugCatalog) throws RestoreSkillsException {
        DrugCatalogEntity drugCatalogEntity = getById(drugCatalog.getId());

        drugCatalogEntity.setSpeciality(drugCatalog.getSpeciality());
        drugCatalogEntity.setType(drugCatalog.getType());
        drugCatalogEntity.setMedicine(drugCatalog.getMedicine());
        drugCatalogEntity.setDose(drugCatalog.getDose());
        drugCatalogEntity.setWhenField(drugCatalog.getWhenField());
        drugCatalogEntity.setWhereField(drugCatalog.getWhereField());
        drugCatalogEntity.setDuration(drugCatalog.getDuration());
        drugCatalogEntity.setFrequency(drugCatalog.getFrequency());
        drugCatalogEntity.setQuantity(drugCatalog.getQuantity());
        drugCatalogEntity.setNotes(drugCatalog.getNotes());
        drugCatalogEntity.setActive(drugCatalog.isActive());

        drugCatalogRepo.save(drugCatalogEntity);
    }
    @Override
    public Page<DrugCatalog> getAllDrugCatalog(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (Objects.nonNull(sortDirection) && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size,Sort.by(direction,sortBy));
        Page<DrugCatalogEntity> drugCatalogEntities = drugCatalogRepo.findAll(pageable);
        return drugCatalogEntities.map(this::toDrugCatalog);
    }

    @Override
    public void updateStatus(long id, boolean active) throws RestoreSkillsException {
        DrugCatalogEntity drugCatalogEntity = getById(id);
        drugCatalogEntity.setActive(active);
        drugCatalogRepo.save(drugCatalogEntity);
    }
}
