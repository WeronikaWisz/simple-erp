package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.*;
import com.simpleerp.simpleerpapp.enums.ERole;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.exception.ApiBadRequestException;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.*;
import com.simpleerp.simpleerpapp.repositories.*;
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
    private final PurchaseRepository purchaseRepository;
    private final ProductionRepository productionRepository;
    private final OrderRepository orderRepository;
    private final ReleaseRepository releaseRepository;
    private final AcceptanceRepository acceptanceRepository;
    PasswordEncoder encoder;

    @Autowired
    public ManageUsersService(UserRepository userRepository, RoleRepository roleRepository,
                              TaskRepository taskRepository, PurchaseRepository purchaseRepository,
                              ProductionRepository productionRepository, OrderRepository orderRepository,
                              ReleaseRepository releaseRepository, AcceptanceRepository acceptanceRepository,
                              PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
        this.purchaseRepository = purchaseRepository;
        this.productionRepository = productionRepository;
        this.orderRepository = orderRepository;
        this.releaseRepository = releaseRepository;
        this.acceptanceRepository = acceptanceRepository;
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
        user.setIsDeleted(false);
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
        List<User> userList = userRepository.findByIsDeletedFalse();
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
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
        if(user.getIsDeleted()){
            throw new ApiExpectationFailedException("exception.userDeleted");
        }
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new ApiNotFoundException("exception.userRoleMissing"));
        if(user.getRoles().contains(adminRole)){
            throw new ApiExpectationFailedException("exception.cannotDeleteAdmin");
        }
        if(taskRepository.findAll().stream().anyMatch(t -> t.getDefaultUser().equals(user))){
            throw new ApiExpectationFailedException("exception.userHasDefaultTask");
        }
        List<Purchase> purchaseList = purchaseRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .stream()
                .filter(t -> t.getRequestingUser().equals(user)
                        || t.getAssignedUser().equals(user)).collect(Collectors.toList());
        List<Production> productionList = productionRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .stream()
                .filter(t -> t.getRequestingUser().equals(user)
                        || t.getAssignedUser().equals(user)).collect(Collectors.toList());
        List<Order> orderList = orderRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .stream()
                .filter(t -> t.getRequestingUser().equals(user)
                        || t.getAssignedUser().equals(user)).collect(Collectors.toList());
        List<Release> releaseList = releaseRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .stream()
                .filter(t -> t.getRequestingUser().equals(user)
                        || t.getAssignedUser().equals(user)).collect(Collectors.toList());
        List<Acceptance> acceptanceList = acceptanceRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))
                .stream()
                .filter(t -> t.getRequestingUser().equals(user)
                        || t.getAssignedUser().equals(user)).collect(Collectors.toList());

        if(!purchaseList.isEmpty() || !productionList.isEmpty() || !orderList.isEmpty() || !releaseList.isEmpty()
                || !acceptanceList.isEmpty()){
            throw new ApiExpectationFailedException("exception.userHasCurrentTask");
        }

        user.setIsDeleted(true);
        user.setDeleteDate(LocalDateTime.now());
        userRepository.save(user);
    }

    public void updateUser(UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(updateUserRequest.getId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));

        if(user.getIsDeleted()){
            throw new ApiExpectationFailedException("exception.userDeleted");
        }

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
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
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
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        User user = userRepository.findById(updateDefaultUserRequest.getEmployeeId())
                .orElseThrow(() -> new ApiNotFoundException("exception.userNotFound"));
        task.setDefaultUser(user);
        taskRepository.save(task);
    }

    public List<UserName> loadUserForTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.taskNotFound"));
        List<User> userList = userRepository.findByIsDeletedFalse()
                .stream().filter(user -> user.getRoles().contains(task.getRole())).collect(Collectors.toList());
        Optional<User> admin = userRepository.findByUsername("admin");
        admin.ifPresent(userList::add);
        List<UserName> userNameList = new ArrayList<>();
        for (User user: userList){
            userNameList.add(new UserName(user.getId(), user.getName() + " " + user.getSurname()));
        }
        return userNameList;
    }
}
