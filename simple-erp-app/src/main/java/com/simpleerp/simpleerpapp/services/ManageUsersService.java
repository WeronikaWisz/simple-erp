package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.UserListItem;
import com.simpleerp.simpleerpapp.dtos.manageusers.UsersResponse;
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
import java.util.stream.Collectors;

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

    public UsersResponse loadUsers(Integer page, Integer size){
        List<User> userList = userRepository.findAll();
        int total = userList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        UsersResponse usersResponse = new UsersResponse();
        if(end >= start) {
            usersResponse.setUserList(userListToUserListItem(userList.stream()
                    .sorted(Comparator.comparing(User::getCreationDate).reversed()).collect(Collectors.toList())
                    .subList(start, end)));
        }
        usersResponse.setTotalUsersLength(total);
        return usersResponse;
    }

    private List<UserListItem> userListToUserListItem(List<User> userList){
        List<UserListItem> userListItems = new ArrayList<>();
        for(User user: userList){
            UserListItem userListItem = new UserListItem(user.getId(), user.getUsername(), user.getEmail(),
                    user.getName(), user.getSurname());
            if(user.getPhone() != null){
                userListItem.setPhone(user.getPhone());
            }
            List<String> roles = new ArrayList<>();
            for(Role role: user.getRoles()){
                roles.add(role.getName().name());
            }
            userListItem.setRoles(roles);
            userListItems.add(userListItem);
        }
        return userListItems;
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.userDeleted"));
        userRepository.delete(user);
    }

    public void updateUser(UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(updateUserRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userDeleted"));

        if(!Objects.equals(updateUserRequest.getUsername(), user.getUsername())){
            if (userRepository.existsByUsername(updateUserRequest.getUsername())) {
                throw new ApiBadRequestException("exception.usernameUsed");
            }
            user.setUsername(updateUserRequest.getUsername());
        }

        if(!Objects.equals(updateUserRequest.getEmail(), user.getEmail())){
            if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
                throw new ApiBadRequestException("exception.emailUsed");
            }
            user.setEmail(updateUserRequest.getEmail());
        }

        user.setName(updateUserRequest.getName());
        user.setSurname(updateUserRequest.getSurname());

        String phoneNumber = updateUserRequest.getPhone();
        if(!Objects.equals(phoneNumber, "")) {
            phoneNumber = phoneNumber.replaceAll("\\s+", "");
            if (!phoneNumber.startsWith("+48")) {
                phoneNumber = "+48" + phoneNumber;
            }
        }

        user.setPhone(phoneNumber);

        if(updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()){
            user.setPassword(encoder.encode(updateUserRequest.getPassword()));
        }

        user.setUpdateDate(LocalDateTime.now());

        Set<Role> roles = new HashSet<>();

        for(String name: updateUserRequest.getRoles()){
            Role role = getRole(name);
            roles.add(role);
        }

        user.setRoles(roles);
        userRepository.save(user);
    }

    public UserListItem getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.userDeleted"));
        return userListToUserListItem(List.of(user)).get(0);
    }
}
