package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.simpleerp.simpleerpapp.dtos.manageusers.ChangePassword;
import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.manageusers.ProfileData;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateUserData;
import com.simpleerp.simpleerpapp.dtos.warehouse.AssignedUser;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.services.UsersService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:4200")
public class UsersController {

    private final UsersService usersService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public UsersController(UsersService usersService, MessageSource messageSource, ModelMapper modelMapper) {
        this.usersService = usersService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping(path = "/user")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE') or hasRole('ROLE_PRODUCTION') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> getUserProfileData() {
        ProfileData profileData = mapUserToProfileData(usersService.getUserProfileData());
        return ResponseEntity.ok(profileData);
    }

    @PutMapping(path = "/user")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE') or hasRole('ROLE_PRODUCTION') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> updateUserProfileData(@RequestBody UpdateUserData updateUserData) {
        boolean dataChanged = usersService.updateUserProfileData(updateUserData);
        String message;
        if(dataChanged) {
            message = messageSource.getMessage(
                    "success.profileUpdate", null, LocaleContextHolder.getLocale());
            return ResponseEntity.ok(new MessageResponse(message));
        } else {
            message = messageSource.getMessage(
                    "exception.profileSameData", null, LocaleContextHolder.getLocale());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new MessageResponse(message));
        }
    }

    @PutMapping(path = "/user/password")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE') or hasRole('ROLE_PRODUCTION') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        usersService.changePassword(changePassword);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.passwordChange", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/assigned-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE') or hasRole('ROLE_PRODUCTION') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadAssignedUser(@PathVariable("id") Long id) {
        AssignedUser assignedUser = usersService.loadAssignedUser(id);
        return ResponseEntity.ok(assignedUser);
    }

    private ProfileData mapUserToProfileData(User user){
        return modelMapper.map(user, ProfileData.class);
    }

}
