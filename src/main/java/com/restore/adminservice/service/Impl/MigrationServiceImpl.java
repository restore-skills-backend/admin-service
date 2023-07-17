package com.restore.adminservice.service.Impl;

import com.restore.adminservice.repository.*;
import com.restore.adminservice.repository.*;
import com.restore.adminservice.service.MigrationService;
import com.restore.core.dto.app.*;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.entity.*;
import com.restore.core.exception.RestoreSkillsException;
import com.restore.core.service.AppService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MigrationServiceImpl extends AppService implements MigrationService {
    @Autowired
    private RadiologyCatalogRepo radiologyCatalogRepo;
    @Autowired
    private LabCatalogRepo labCatalogRepo;
    @Autowired
    private DrugCatalogRepo drugCatalogRepo;
    @Autowired
    private DiagnosisCodeRepo diagnosisCodeRepo;
    @Autowired
    private ProceduralCodeRepo proceduralCodeRepo;
    @Autowired
    private ModelMapper modelMapper;

    private CSVParser csvParser(InputStream file) throws IOException {
        Reader reader = new BufferedReader(new InputStreamReader(file));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());
        return csvParser;
    }
    @Transactional
    public void uploadLabCatalog(InputStream file) throws RestoreSkillsException {
        try {
            CSVParser csvData = csvParser(file);
            List<LabCatalog> labCatalogs = new ArrayList<>();
            for (CSVRecord record : csvData) {
                LabCatalog labCatalog = new LabCatalog();
                labCatalog.setLoincNum(record.get("LOINC_NUM"));
                labCatalog.setDescription(record.get("Description"));
//                labCatalog.setActive(Boolean.parseBoolean(record.get("active")));

                labCatalogs.add(labCatalog);
            }
            labCatalogRepo.saveAll(labCatalogs.stream().map(labCatalog -> modelMapper.map(labCatalog, LabCatalogEntity.class)).collect(Collectors.toList()));
        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public void uploadDrugCatalog(InputStream inputStream) throws RestoreSkillsException {
        try {
            CSVParser csvData = csvParser(inputStream);
            List<DrugCatalog> drugCatalogs = new ArrayList<>();

            for (CSVRecord record : csvData) {
                DrugCatalog drugCatalog = new DrugCatalog();
                drugCatalog.setSpeciality(record.get("Speciality"));
                drugCatalog.setType(record.get("Medicine Type"));
                drugCatalog.setMedicine(record.get("Medicine Name"));
                drugCatalog.setDose(record.get("Recommendable Dose"));
                drugCatalog.setWhenField(record.get("When")); // `when` column name escaped
                drugCatalog.setWhereField(record.get("Where"));
                drugCatalog.setFrequency(record.get("Frequency"));
                drugCatalog.setDuration(record.get("Duration"));
                drugCatalog.setQuantity(record.get("Total Qty"));
                drugCatalog.setNotes(record.get("Note/Instruction"));
//                drugCatalog.setActive(Boolean.parseBoolean(record.get("active")));

                drugCatalogs.add(drugCatalog);
            }
            drugCatalogRepo.saveAll(drugCatalogs.stream().map(drugCatalog -> modelMapper.map(drugCatalog, DrugCatalogEntity.class)).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public void uploadRadiologyCatalog(InputStream inputStream) throws RestoreSkillsException {
        try {
            CSVParser csvData = csvParser(inputStream);
            List<RadiologyCatalog> radiologyCatalogs = new ArrayList<>();
            for (CSVRecord record : csvData){
                RadiologyCatalog radiologyCatalog = new RadiologyCatalog();
                radiologyCatalog.setName(record.get("LOINC NUM"));
                radiologyCatalog.setDescription(record.get("Description"));
//                radiologyCatalog.setActive(Boolean.parseBoolean(record.get("active")));

                radiologyCatalogs.add(radiologyCatalog);
            }
            radiologyCatalogRepo.saveAll(radiologyCatalogs.stream().map(radiologyCatalog -> modelMapper.map(radiologyCatalog, RadiologyCatalogEntity.class)).collect(Collectors.toList()));

        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public void uploadDiagnosisCode(InputStream inputStream) throws RestoreSkillsException {
        try {
            CSVParser csvData = csvParser(inputStream);
            List<DiagnosisCode> diagnosisCodes = new ArrayList<>();
            for(CSVRecord record : csvData){
                DiagnosisCode diagnosisCode = new DiagnosisCode();
                diagnosisCode.setDiagnosisCode(record.get("code"));
                diagnosisCode.setDescription(record.get("DESCRIPTION"));
//                diagnosisCode.setNote(record.get("note"));
//                diagnosisCode.setActive(Boolean.parseBoolean(record.get("active")));

                diagnosisCodes.add(diagnosisCode);
            }
            diagnosisCodeRepo.saveAll(diagnosisCodes.stream().map(diagnosisCode -> modelMapper.map(diagnosisCode, DiagnosisCodeEntity.class)).collect(Collectors.toList()));
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }

    @Override
    public void uploadProceduralCode(InputStream inputStream) throws RestoreSkillsException {

        try {
            CSVParser csvData = csvParser(inputStream);
            List<ProceduralCode> proceduralCodes = new ArrayList<>();
            for (CSVRecord record : csvData){
                ProceduralCode proceduralCode = new ProceduralCode();
//                proceduralCode.setType(record.get("type"));
                proceduralCode.setProceduralCode(record.get("CPT CODE"));
                proceduralCode.setDescription(record.get("DESCRIPTION"));
//                proceduralCode.setNotes(record.get("notes"));
//                proceduralCode.setActive(Boolean.parseBoolean(record.get("active")));

                proceduralCodes.add(proceduralCode);
            }
            proceduralCodeRepo.saveAll(proceduralCodes.stream().map(proceduralCode -> modelMapper.map(proceduralCode, ProceduralCodeEntity.class)).collect(Collectors.toList()));
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.IAM_ERROR, e.getMessage());
        }
    }
}
