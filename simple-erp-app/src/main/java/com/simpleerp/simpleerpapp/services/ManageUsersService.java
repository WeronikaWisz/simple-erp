package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.*;
import com.simpleerp.simpleerpapp.enums.ERole;
import com.simpleerp.simpleerpapp.exception.ApiBadRequestException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.Role;
import com.simpleerp.simpleerpapp.models.Task;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.RoleRepository;
import com.simpleerp.simpleerpapp.repositories.TaskRepository;
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
    private final TaskRepository taskRepository;
    PasswordEncoder encoder;

    @Autowired
    public ManageUsersService(UserRepository userRepository, RoleRepository roleRepository,
                              TaskRepository taskRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
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

    public List<DefaultUser> loadDefaultUsers(int page, int size) {
        List<Task> taskList = taskRepository.findAll();
        int total = taskList.size();
        int start = page * size;
        int end = Math.min(start + size, total);
        List<DefaultUser> defaultUsers = new ArrayList<>();
        if(end >= start) {
            defaultUsers = taskListToDefaultUsersList(
                    taskList.stream().sorted(Comparator.comparing(Task::getId))
                            .collect(Collectors.toList()).subList(start, end));
        }
        return defaultUsers;
    }

    private List<DefaultUser> taskListToDefaultUsersList(List<Task> taskList){
        List<DefaultUser> defaultUsers = new ArrayList<>();
        for(Task task: taskList){
            DefaultUser defaultUser = new DefaultUser(task.getId(), task.getDefaultUser().getId(), task.getName().name(),
                    task.getDefaultUser().getName() + " " + task.getDefaultUser().getSurname());
            defaultUsers.add(defaultUser);
        }
        return defaultUsers;
    }

    public void updateDefaultUser(UpdateDefaultUserRequest updateDefaultUserRequest) {
        Task task = taskRepository.findById(updateDefaultUserRequest.getTaskId())
                .orElseThrow(() -> new ApiNotFoundException("exception.defaultUpdate"));
        User user = userRepository.findById(updateDefaultUserRequest.getEmployeeId())
                .orElseThrow(() -> new ApiNotFoundException("exception.defaultUpdate"));
        task.setDefaultUser(user);
        taskRepository.save(task);
    }

    //TODO do zmiany - tylko uzytkownicy z dobra rola
    public List<UserName> loadUsersName(Long id) {
        List<User> userList = userRepository.findAll();
        List<UserName> userNameList = new ArrayList<>();
        for (User user: userList){
            userNameList.add(new UserName(user.getId(), user.getName() + " " + user.getSurname()));
        }
        return userNameList;
    }
}
