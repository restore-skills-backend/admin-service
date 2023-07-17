package com.restore.adminservice.controller;

import com.restore.adminservice.service.MigrationService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequestMapping("/api/admin/migration/upload")
public class MigrationController extends AppController {
    @Autowired
    private MigrationService migrationService;
    @PostMapping("/lab-catalog")
    public ResponseEntity<Response> uploadLabCatalog(@RequestPart("file") MultipartFile file) throws RestoreSkillsException {
        try (InputStream inputStream = file.getInputStream()) {
            migrationService.uploadLabCatalog(inputStream);
            return success(ResponseCode.CREATED,"Lab catalog added succesfully");
        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/drug-catalog")
    public ResponseEntity<Response> uploadDrugCatalog(@RequestPart("file") MultipartFile file) throws RestoreSkillsException {
        try (InputStream inputStream = file.getInputStream()){
            migrationService.uploadDrugCatalog(inputStream);
            return success(ResponseCode.CREATED,"Drug catalog added successfully");
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/radiology-catalog")
    public ResponseEntity<Response> uploadRadiologyCatalog(@RequestPart("file") MultipartFile file) throws RestoreSkillsException {
        try (InputStream inputStream = file.getInputStream()){
            migrationService.uploadRadiologyCatalog(inputStream);
            return success(ResponseCode.CREATED,"Radiology catalog added succesfully");
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/diagnosis-code")
    public ResponseEntity<Response> uploadDiagnosisCode(@RequestPart("file") MultipartFile file) throws RestoreSkillsException {
        try(InputStream inputStream = file.getInputStream()) {
            migrationService.uploadDiagnosisCode(inputStream);
            return success(ResponseCode.CREATED,"Diagnosis code added succesfully");
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST,e.getMessage());
        }
    }

    @PostMapping("/procedural-code")
    public ResponseEntity<Response> uploadProceduralCode(@RequestPart("file") MultipartFile file) throws RestoreSkillsException {
        try (InputStream inputStream = file.getInputStream()){
            migrationService.uploadProceduralCode(inputStream);
            return success(ResponseCode.CREATED,"Procedural code added succesfully");
        }
        catch (Exception e){
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }
}
