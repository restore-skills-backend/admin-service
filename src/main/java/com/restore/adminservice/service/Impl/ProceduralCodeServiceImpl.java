package com.restore.adminservice.service.Impl;

import com.restore.adminservice.repository.ProceduralCodeRepo;
import com.restore.adminservice.service.ProceduralCodeService;
import com.restore.core.dto.app.ProceduralCode;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.ProceduralCodeEntity;
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
public class ProceduralCodeServiceImpl extends AppService implements ProceduralCodeService {
    @Autowired
    private ProceduralCodeRepo proceduralCodeRepo;

    @Autowired
    private ModelMapper modelMapper;

    private ProceduralCodeEntity getById(Long id) throws RestoreSkillsException {
        return proceduralCodeRepo.findById(id).orElseThrow(()-> new RestoreSkillsException(ResponseCode.IAM_ERROR,"Canoot find procedural code with id : "+id));
    }
    private ProceduralCode toProceduralCode(ProceduralCodeEntity proceduralCodeEntity) {
        return modelMapper.map(proceduralCodeEntity, ProceduralCode.class);
    }

    @Override
    public void add(ProceduralCode proceduralCode) throws RestoreSkillsException {
        try {
            proceduralCodeRepo.save(modelMapper.map(proceduralCode, ProceduralCodeEntity.class));
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public ProceduralCode getProceduralCode(Long id) throws RestoreSkillsException {
//        Optional<LabCatalogEntity> labCatalogEntity = labCatalogRepo.findById(id);
//        if (labCatalogEntity.isEmpty()) {
//            throwError(ResponseCode.BAD_REQUEST, "Cannot find Lab Catalog with ID : " + id);
//        }
//        return modelMapper.map(labCatalogEntity, LabCatalog.class);
        return toProceduralCode(getById(id));
    }

    @Override
    public void updateProceduralCode(ProceduralCode proceduralCode) throws RestoreSkillsException {
        ProceduralCodeEntity proceduralCodeEntity = getById(proceduralCode.getId());

        proceduralCodeEntity.setProceduralCode(proceduralCode.getProceduralCode());
        proceduralCodeEntity.setDescription(proceduralCode.getDescription());
        proceduralCodeEntity.setActive(proceduralCode.isActive());

        proceduralCodeRepo.save(proceduralCodeEntity);
    }
    @Override
    public Page<ProceduralCode> getAllProceduralCode(int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = Sort.Direction.ASC;
        if (Objects.nonNull(sortDirection) && sortDirection.equalsIgnoreCase("desc")) {
            direction = Sort.Direction.DESC;
        }
        Pageable pageable = PageRequest.of(page, size,Sort.by(direction,sortBy));
        Page<ProceduralCodeEntity> proceduralCodeEntityPage = proceduralCodeRepo.findAll(pageable);
        return proceduralCodeEntityPage.map(this::toProceduralCode);
    }

    @Override
    public void updateStatus(long id, boolean active) throws RestoreSkillsException {
        ProceduralCodeEntity proceduralCodeEntity = getById(id);
        proceduralCodeEntity.setActive(active);
        proceduralCodeRepo.save(proceduralCodeEntity);
    }
}
