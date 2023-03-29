package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.*;
import com.simpleerp.simpleerpapp.services.ManageUsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manage-users")
@CrossOrigin("http://localhost:4200")
public class ManageUsersController {

    private final ManageUsersService manageUsersService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public ManageUsersController(ManageUsersService manageUsersService, MessageSource messageSource, ModelMapper modelMapper) {
        this.manageUsersService = manageUsersService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest addUserRequest) {
        manageUsersService.addUser(addUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.userRegister", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        UsersResponse usersResponse = manageUsersService.loadUsers(page, size);
        return ResponseEntity.ok(usersResponse);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        manageUsersService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.userDeleted", null, LocaleContextHolder.getLocale())));
    }

    @PutMapping("/user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        manageUsersService.updateUser(updateUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.profileUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        UserListItem user = manageUsersService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/default-users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadDefaultUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        List<DefaultUser> defaultUsers = manageUsersService.loadDefaultUsers(page, size);
        return ResponseEntity.ok(defaultUsers);
    }

    @PutMapping("/default-user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateDefaultUser(@RequestBody UpdateDefaultUserRequest updateDefaultUserRequest) {
        manageUsersService.updateDefaultUser(updateDefaultUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.defaultUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/users-task/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> loadUserForTask(@PathVariable("id") Long id) {
        List<UserName> userNameList = manageUsersService.loadUserForTask(id);
        return ResponseEntity.ok(userNameList);
    }
}
