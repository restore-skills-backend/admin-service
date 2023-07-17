package com.restore.adminservice.controller;

import com.restore.adminservice.service.LabCatalogService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.LabCatalog;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/lab-catalog")
public class LabCatalogController extends AppController {
    @Autowired
    private LabCatalogService labCatalogService;
    
    @PostMapping
    public ResponseEntity<Response> addLabCatalog(@Valid @RequestBody LabCatalog labCatalog) throws RestoreSkillsException {
        labCatalogService.add(labCatalog);
        return success(ResponseCode.CREATED,"Lab catalog added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getLabCatalog(@PathVariable Long id) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Lab catalog found successfully",labCatalogService.getLabCatalog(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllLabCatalog(@RequestParam(value = "page" ,defaultValue = "0") int page,
                                                     @RequestParam(value = "size" ,defaultValue = "20") int size,
                                                     @RequestParam(value = "sortBy" ,defaultValue = "id") String sortBy,
                                                     @RequestParam(value = "sortDirection" ,defaultValue = "desc") String sortDirection){
        return data(ResponseCode.OK,"Lab Catalogs found successfully",labCatalogService.getAllLabCatalog(page,size,sortBy,sortDirection));
    }

    @PutMapping
    public ResponseEntity<Response> updateLabCatalog(@RequestBody LabCatalog labCatalog) throws RestoreSkillsException {
        labCatalogService.updateLabCatalog(labCatalog);
        return success(ResponseCode.UPDATED,"lab Catalog updated succesfully");
    }

    @PatchMapping("/{id}/active/{active}")
    public ResponseEntity<Response> updateStatus(@PathVariable("id") long id, @PathVariable boolean active) throws RestoreSkillsException {
        try {
            labCatalogService.updateStatus(id, active);
            return success(ResponseCode.OK, "Status for lab catalog updated successfully");
        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }
}
