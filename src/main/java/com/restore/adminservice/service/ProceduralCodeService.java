package com.restore.adminservice.service;

import com.restore.core.dto.app.ProceduralCode;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.data.domain.Page;

public interface ProceduralCodeService {
    void add(ProceduralCode proceduralCode) throws RestoreSkillsException;

    ProceduralCode getProceduralCode(Long id) throws RestoreSkillsException;

    Page<ProceduralCode> getAllProceduralCode(int page, int size, String sortBy, String sortDirection);

    void updateProceduralCode(ProceduralCode proceduralCode) throws RestoreSkillsException;

    void updateStatus(long id, boolean active) throws RestoreSkillsException;
}
