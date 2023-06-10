package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateDefaultUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateUserData;
import com.simpleerp.simpleerpapp.enums.ERole;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.ETask;
import com.simpleerp.simpleerpapp.exception.ApiBadRequestException;
import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.Role;
import com.simpleerp.simpleerpapp.models.Task;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ManageUsersServiceTest {

    @Mock
    private UserRepository testUserRepository;
    @Mock
    private RoleRepository testRoleRepository;
    @Mock
    private TaskRepository testTaskRepository;
    @Mock
    private PurchaseRepository testPurchaseRepository;
    @Mock
    private ProductionRepository testProductionRepository;
    @Mock
    private OrderRepository testOrderRepository;
    @Mock
    private ReleaseRepository testReleaseRepository;
    @Mock
    private AcceptanceRepository testAcceptanceRepository;
    @Mock
    PasswordEncoder encoder;

    private ManageUsersService testManageUsersService;

    @BeforeEach
    void setUp() {
        testManageUsersService = new ManageUsersService(testUserRepository, testRoleRepository, testTaskRepository,
                testPurchaseRepository, testProductionRepository, testOrderRepository, testReleaseRepository,
                testAcceptanceRepository, encoder);
    }

    @Test
    void testAddUser() {
        String email = "test2@email.com";
        String name = "test";
        String surname = "test";
        String phone = "123456789";
        String username = "username";

        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setUsername(username);
        addUserRequest.setEmail(email);
        addUserRequest.setPhone(phone);
        addUserRequest.setName(name);
        addUserRequest.setSurname(surname);

        Mockito.when(testUserRepository.existsByUsername(username)).thenReturn(false);
        Mockito.when(testUserRepository.existsByEmail(email)).thenReturn(false);

        testManageUsersService.addUser(addUserRequest);

        verify(testUserRepository).save(any(User.class));
    }

    @Test
    void testAddUserEmailExists() {
        String email = "test2@email.com";
        String name = "test";
        String surname = "test";
        String phone = "123456789";
        String username = "username";

        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setUsername(username);
        addUserRequest.setEmail(email);
        addUserRequest.setPhone(phone);
        addUserRequest.setName(name);
        addUserRequest.setSurname(surname);

        Mockito.when(testUserRepository.existsByUsername(username)).thenReturn(false);
        Mockito.when(testUserRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(ApiBadRequestException.class, () -> testManageUsersService.addUser(addUserRequest));
    }

    @Test
    void testAddUserUsernameExists() {
        String email = "test2@email.com";
        String name = "test";
        String surname = "test";
        String phone = "123456789";
        String username = "username";

        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setUsername(username);
        addUserRequest.setEmail(email);
        addUserRequest.setPhone(phone);
        addUserRequest.setName(name);
        addUserRequest.setSurname(surname);

        Mockito.when(testUserRepository.existsByUsername(username)).thenReturn(true);

        assertThrows(ApiBadRequestException.class, () -> testManageUsersService.addUser(addUserRequest));
    }

    @Test
    void testLoadUsers() {
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);
        userList.add(user);

        Mockito.when(testUserRepository.findByIsDeletedFalse()).thenReturn(userList);

        testManageUsersService.loadUsers(0,10);

        verify(testUserRepository).findByIsDeletedFalse();
    }

    @Test
    void testDeleteUser() {
        Long id = 1L;
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);

        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.of(user));
        Mockito.when(testRoleRepository.findByName(ERole.ROLE_ADMIN)).thenReturn(java.util.Optional.of(new Role()));
        Mockito.when(testTaskRepository.findAll()).thenReturn(Collections.emptyList());
        Mockito.when(testPurchaseRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))).thenReturn(Collections.emptyList());
        Mockito.when(testProductionRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))).thenReturn(Collections.emptyList());
        Mockito.when(testOrderRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))).thenReturn(Collections.emptyList());
        Mockito.when(testReleaseRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))).thenReturn(Collections.emptyList());
        Mockito.when(testAcceptanceRepository.findByStatusIn(List.of(EStatus.WAITING, EStatus.IN_PROGRESS))).thenReturn(Collections.emptyList());

        testManageUsersService.deleteUser(id);

        assertEquals(true, user.getIsDeleted());
    }

    @Test
    void testUpdateUser() {
        UpdateUserRequest updateUserData = new UpdateUserRequest();

        Long id = 1L;
        String email = "test2@email.com";
        String name = "test";
        String surname = "test";
        String phone = "123456789";

        updateUserData.setId(id);
        updateUserData.setEmail(email);
        updateUserData.setName(name);
        updateUserData.setSurname(surname);
        updateUserData.setPhone(phone);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);

        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.of(user));
        testManageUsersService.updateUser(updateUserData);

        assertEquals(email, user.getEmail());
        assertEquals(name, user.getName());
        assertEquals(surname, user.getSurname());
        assertEquals("+48" + phone, user.getPhone());
    }

    @Test
    void testGetUser() {
        Long id = 1L;
        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.of(new User()));
        testManageUsersService.getUser(id);
        verify(testUserRepository).findById(id);
    }

    @Test
    void testGetUserNotFound() {
        Long id = 1L;
        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testManageUsersService.getUser(id));
    }

    @Test
    void testLoadDefaultUsers() {
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);
        ETask eTask = ETask.TASK_SALE;

        Long id = 1L;
        Task task = new Task();
        task.setDefaultUser(user);
        task.setName(eTask);
        task.setId(id);

        List<Task> taskList = new ArrayList<>();

        Mockito.when(testTaskRepository.findAll()).thenReturn(taskList);
        testManageUsersService.loadDefaultUsers(0,10);
        verify(testTaskRepository).findAll();
    }

    @Test
    void testUpdateDefaultUser() {
        UpdateDefaultUserRequest updateUserData = new UpdateDefaultUserRequest();

        Long id = 1L;

        updateUserData.setEmployeeId(id);
        updateUserData.setTaskId(id);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);

        ETask eTask = ETask.TASK_SALE;

        Task task = new Task();
        task.setDefaultUser(user);
        task.setName(eTask);
        task.setId(id);

        Mockito.when(testTaskRepository.findById(id)).thenReturn(java.util.Optional.of(task));
        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.of(new User()));

        testManageUsersService.updateDefaultUser(updateUserData);

        verify(testTaskRepository).save(any(Task.class));
    }

    @Test
    void testLoadUserForTask() {
        List<User> userList = new ArrayList<>();
        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);
        userList.add(user);

        ETask eTask = ETask.TASK_SALE;

        Long id = 1L;
        Task task = new Task();
        task.setDefaultUser(user);
        task.setName(eTask);
        task.setId(id);

        Mockito.when(testTaskRepository.findById(id)).thenReturn(java.util.Optional.of(task));
        Mockito.when(testUserRepository.findByIsDeletedFalse()).thenReturn(userList);

        testManageUsersService.loadUserForTask(id);

        verify(testTaskRepository).findById(id);
    }
}