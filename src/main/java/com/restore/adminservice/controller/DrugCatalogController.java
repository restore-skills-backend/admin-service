package com.restore.adminservice.controller;

import com.restore.adminservice.service.DrugCatalogService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.DrugCatalog;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/drug-catalog")
public class DrugCatalogController extends AppController {
    @Autowired
    private DrugCatalogService drugCatalogService;

    @PostMapping
    public ResponseEntity<Response> addDrugCatalog(@Valid @RequestBody DrugCatalog drugCatalog) throws RestoreSkillsException {
        drugCatalogService.add(drugCatalog);
        return success(ResponseCode.CREATED,"Drug catalog added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getDrugCatalog(@PathVariable Long id) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Drug catalog found successfully",drugCatalogService.getDrugCatalog(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllDrugCatalog(@RequestParam(value = "page" ,defaultValue = "0") int page,
                                                     @RequestParam(value = "size" ,defaultValue = "20") int size,
                                                     @RequestParam(value = "sortBy" ,defaultValue = "id") String sortBy,
                                                     @RequestParam(value = "sortDirection" ,defaultValue = "desc") String sortDirection){
        return data(ResponseCode.OK,"Drug Catalogs found successfully",drugCatalogService.getAllDrugCatalog(page,size,sortBy,sortDirection));
    }

    @PutMapping
    public ResponseEntity<Response> updateDrugCatalog(@RequestBody DrugCatalog drugCatalog) throws RestoreSkillsException {
        drugCatalogService.updateDrugCatalog(drugCatalog);
        return success(ResponseCode.UPDATED,"Drug Catalog updated succesfully");
    }

    @PatchMapping("/{id}/active/{active}")
    public ResponseEntity<Response> updateStatus(@PathVariable("id") long id, @PathVariable boolean active) throws RestoreSkillsException {
        try {
            drugCatalogService.updateStatus(id, active);
            return success(ResponseCode.OK, "Status for drug catalog updated successfully");
        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }
}
