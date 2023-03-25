package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.enums.ERole;
import com.simpleerp.simpleerpapp.models.Role;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.services.ManageUsersService;
import com.simpleerp.simpleerpapp.services.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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

    @PostMapping("/add-user")
    public ResponseEntity<?> addUser(@RequestBody AddUserRequest addUserRequest) {
        manageUsersService.addUser(addUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.userRegister", null, LocaleContextHolder.getLocale())));
    }
}
