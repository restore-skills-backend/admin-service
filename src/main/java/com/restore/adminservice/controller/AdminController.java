package com.restore.adminservice.controller;

import com.restore.adminservice.dto.PasswordChange;
import com.restore.adminservice.service.UserService;
import com.restore.core.controller.AppController;
import com.restore.core.dto.app.User;
import com.restore.core.dto.response.Response;
import com.restore.core.dto.response.ResponseCode;
import com.restore.core.exception.RestoreSkillsException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;


@RestController
@RequestMapping("/api/admin")
public class AdminController extends AppController {

    @Autowired
    private UserService userService;

    @GetMapping("/specialities")
    public ResponseEntity<Response> getSpecialities(){
        return data(userService.getAllSpecialities());
    }

    @GetMapping("/profile")
    public ResponseEntity<Response> getProfile() throws RestoreSkillsException, IOException {
        return data(ResponseCode.OK, "Profile found successfully", userService.getProfile());
    }

    @PutMapping("/profile")
    public ResponseEntity<Response> updateProfile(@Valid @RequestBody User user) throws RestoreSkillsException {
        userService.updateProfile(user);
        return success(ResponseCode.OK, "Profile update successfully");
    }

    @PostMapping("/change-password")
    public ResponseEntity<Response> changePassword(@Valid @RequestBody PasswordChange passwordChange) throws RestoreSkillsException {
        return data(ResponseCode.OK, "Password changed successfully", userService.changePassword(passwordChange));
    }

    @PostMapping("/add-user")
    public ResponseEntity<Response> addUser(@Valid @RequestBody User user) throws RestoreSkillsException, IOException {
        userService.addUser(user);
        return success(ResponseCode.OK, "user added successfully");
    }

    @GetMapping("/get-users")
    public ResponseEntity<Response> getUsers(@RequestParam("page") int page, @RequestParam("size") int size) throws RestoreSkillsException, IOException {
        return data(ResponseCode.OK, "users get successfully", userService.getUsers(page,size));
    }

    @PatchMapping("/user/{email}/active/{active}")
    public ResponseEntity<Response> activateUser(@PathVariable String email, @PathVariable boolean active) throws RestoreSkillsException {
        userService.activateUser(email, active);
        return success(ResponseCode.OK, "Updated user "+email+" active status to " + active);
    }

    @GetMapping({"/user/{email}","/auth/user/{email}"})
    public ResponseEntity<Response> getUser(@PathVariable String email) throws RestoreSkillsException {
        return data(ResponseCode.OK, "User found successfully", userService.getUser(email));
    }

    @GetMapping({"/user-id/{id}","/auth/user-id/{id}"})
    public ResponseEntity<Response> getUserById(@PathVariable UUID id) throws RestoreSkillsException {
        return data(ResponseCode.OK, "User found successfully", userService.getUserById(id));
    }



}
