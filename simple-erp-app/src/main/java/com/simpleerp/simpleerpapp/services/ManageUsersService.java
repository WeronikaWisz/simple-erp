package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.enums.ERole;
import com.simpleerp.simpleerpapp.exception.ApiBadRequestException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.Role;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.RoleRepository;
import com.simpleerp.simpleerpapp.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ManageUsersService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    PasswordEncoder encoder;

    @Autowired
    public ManageUsersService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
    }

    public void addUser(AddUserRequest addUserRequest){
        if (userRepository.existsByUsername(addUserRequest.getUsername())) {
            throw new ApiBadRequestException("exception.usernameUsed");
        }

        if (userRepository.existsByEmail(addUserRequest.getEmail())) {
            throw new ApiBadRequestException("exception.emailUsed");
        }

        String phoneNumber = addUserRequest.getPhone();
        if(!Objects.equals(phoneNumber, "")) {
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!phoneNumber.startsWith("+48")) {
                phoneNumber = "+48" + phoneNumber;
            }
        }

        User user = new User(addUserRequest.getName(), addUserRequest.getSurname(), addUserRequest.getUsername(),
                encoder.encode(addUserRequest.getPassword()), addUserRequest.getEmail(), phoneNumber);

        user.setCreationDate(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();

        for(String name: addUserRequest.getRoles()){
            Role role = getRole(name);
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    private Role getRole(String name){
        Optional<Role> roleOpt = roleRepository
                .findByName(ERole.valueOf(name));
        if(roleOpt.isPresent()){
            return roleOpt.get();
        } else {
            throw new ApiNotFoundException("exception.userRoleMissing");
        }
    }

}
