package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.simpleerp.simpleerpapp.dtos.auth.ChangePassword;
import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@CrossOrigin("http://localhost:4200")
public class UsersController {
    private final UsersService usersService;
    private MessageSource messageSource;

    @Autowired
    public UsersController(UsersService usersService, MessageSource messageSource) {
        this.usersService = usersService;
        this.messageSource = messageSource;
    }

    @PutMapping(path = "/user/password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword changePassword) {
        usersService.changePassword(changePassword);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.passwordChange", null, LocaleContextHolder.getLocale())));
    }

}
