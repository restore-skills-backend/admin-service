package com.restore.adminservice.controller;

import com.restore.adminservice.service.ProceduralCodeService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.ProceduralCode;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/procedural-code")
public class ProceduralCodeController extends AppController {
    @Autowired
    private ProceduralCodeService proceduralCodeService;

    @PostMapping
    public ResponseEntity<Response> addProceduralCode(@Valid @RequestBody ProceduralCode proceduralCode) throws RestoreSkillsException {
        proceduralCodeService.add(proceduralCode);
        return success(ResponseCode.CREATED,"Procedural code added successfully");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response> getProceduralCode(@PathVariable Long id) throws RestoreSkillsException {
        return data(ResponseCode.OK,"Procedural code found successfully",proceduralCodeService.getProceduralCode(id));
    }

    @GetMapping("/all")
    public ResponseEntity<Response> getAllProceduralCode(@RequestParam(value = "page" ,defaultValue = "0") int page,
                                                     @RequestParam(value = "size" ,defaultValue = "20") int size,
                                                     @RequestParam(value = "sortBy" ,defaultValue = "id") String sortBy,
                                                     @RequestParam(value = "sortDirection" ,defaultValue = "desc") String sortDirection){
        return data(ResponseCode.OK,"Procedural codes found successfully",proceduralCodeService.getAllProceduralCode(page,size,sortBy,sortDirection));
    }

    @PutMapping
    public ResponseEntity<Response> updateProceduralCode(@RequestBody ProceduralCode proceduralCode) throws RestoreSkillsException {
        proceduralCodeService.updateProceduralCode(proceduralCode);
        return success(ResponseCode.UPDATED,"Procedural code updated succesfully");
    }

    @PatchMapping("/{id}/active/{active}")
    public ResponseEntity<Response> updateStatus(@PathVariable("id") long id, @PathVariable boolean active) throws RestoreSkillsException {
        try {
            proceduralCodeService.updateStatus(id, active);
            return success(ResponseCode.OK, "Status for procedural code updated successfully");
        } catch (Exception e) {
            throw new RestoreSkillsException(ResponseCode.BAD_REQUEST, e.getMessage());
        }
    }
}
