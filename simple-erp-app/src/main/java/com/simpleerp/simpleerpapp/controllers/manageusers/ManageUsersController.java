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
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest addUserRequest) {
        manageUsersService.addUser(addUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.userRegister", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/users")
    public ResponseEntity<?> loadUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        UsersResponse usersResponse = manageUsersService.loadUsers(page, size);
        return ResponseEntity.ok(usersResponse);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        manageUsersService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.userDeleted", null, LocaleContextHolder.getLocale())));
    }

    @PutMapping("/user")
    public ResponseEntity<?> editUser(@RequestBody UpdateUserRequest updateUserRequest) {
        manageUsersService.updateUser(updateUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.profileUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") Long id) {
        UserListItem user = manageUsersService.getUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/default-users")
    public ResponseEntity<?> loadDefaultUsers(@RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        List<DefaultUser> defaultUsers = manageUsersService.loadDefaultUsers(page, size);
        return ResponseEntity.ok(defaultUsers);
    }

    @PutMapping("/default-user")
    public ResponseEntity<?> updateDefaultUser(@RequestBody UpdateDefaultUserRequest updateDefaultUserRequest) {
        manageUsersService.updateDefaultUser(updateDefaultUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.defaultUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/users-task/{id}")
    public ResponseEntity<?> loadUserNames(@PathVariable("id") Long id) {
        List<UserName> userNameList = manageUsersService.loadUsersName(id);
        return ResponseEntity.ok(userNameList);
    }
}
