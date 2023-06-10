package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.dtos.manageusers.ChangePassword;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateUserData;
import com.simpleerp.simpleerpapp.exception.ApiBadRequestException;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.UserRepository;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UsersServiceTest {

    @Mock
    private UserRepository testUserRepository;
    private UsersService testUserService;
    private User user;
    @Mock
    PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        testUserService = new UsersService(testUserRepository, encoder);
        user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);
    }

    @Nested
    class UserRequired {

        @BeforeEach
        void setUp() {
            UserDetailsI applicationUser = UserDetailsI.build(user);
            Authentication authentication = Mockito.mock(Authentication.class);
            SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
            SecurityContextHolder.setContext(securityContext);
            Mockito.when(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).thenReturn(applicationUser);
        }

        @Test
        void testGetUserProfileData() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            testUserService.getUserProfileData();
            verify(testUserRepository).findByUsername("username");
        }

        @Test
        void testGetUserProfileDataNotFound() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
            assertThrows(UsernameNotFoundException.class, () -> testUserService.getUserProfileData());
        }

        @Test
        void testUpdateUserProfileData() {
            UpdateUserData updateUserData = new UpdateUserData();

            String email = "test2@email.com";
            String name = "test";
            String surname = "test";
            String phone = "123456789";

            updateUserData.setEmail(email);
            updateUserData.setName(name);
            updateUserData.setSurname(surname);
            updateUserData.setPhone(phone);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            testUserService.updateUserProfileData(updateUserData);

            assertEquals(email, user.getEmail());
            assertEquals(name, user.getName());
            assertEquals(surname, user.getSurname());
            assertEquals("+48" + phone, user.getPhone());
        }

        @Test
        void testUpdateUserProfileDataUserNotFound() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
            UpdateUserData updateUserData = new UpdateUserData();
            assertThrows(UsernameNotFoundException.class, () -> testUserService.updateUserProfileData(updateUserData));
        }

        @Test
        void testUpdateUserProfileDataExistByEmail() {
            String email = "test2@email.com";
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(testUserRepository.existsByEmail("test2@email.com")).thenReturn(true);
            UpdateUserData updateUserData = new UpdateUserData();
            updateUserData.setEmail(email);
            assertThrows(ApiBadRequestException.class, () -> testUserService.updateUserProfileData(updateUserData));
        }

        @Test
        void testUpdateUserProfileDataEmptyPhoneNumber() {
            UpdateUserData updateUserData = new UpdateUserData();
            updateUserData.setPhone("");

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            testUserService.updateUserProfileData(updateUserData);

            assertEquals("", user.getPhone());
        }

        @Test
        void testChangePassword() {
            String oldPassword = "password";
            String newPassword = "passwordNew";
            ChangePassword changePassword = new ChangePassword();
            changePassword.setOldPassword(oldPassword);
            changePassword.setNewPassword(newPassword);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(encoder.matches(oldPassword, user.getPassword())).thenReturn(true);
            Mockito.when(encoder.matches(newPassword, user.getPassword())).thenReturn(false);
            Mockito.when(encoder.encode(changePassword.getNewPassword())).thenReturn(newPassword);

            testUserService.changePassword(changePassword);

            assertEquals(newPassword, user.getPassword());
        }

        @Test
        void testChangePasswordSameNewOld() {
            String oldPassword = "password";
            String newPassword = "password";
            ChangePassword changePassword = new ChangePassword();
            changePassword.setOldPassword(oldPassword);
            changePassword.setNewPassword(newPassword);

            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            Mockito.when(encoder.matches(oldPassword, user.getPassword())).thenReturn(true);

            assertThrows(ApiBadRequestException.class, () -> testUserService.changePassword(changePassword));
        }

        @Test
        void testChangePasswordUserNotFound() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.empty());
            ChangePassword changePassword = new ChangePassword();
            assertThrows(UsernameNotFoundException.class, () -> testUserService.changePassword(changePassword));
        }

        @Test
        void testChangePasswordWrongOld() {
            Mockito.when(testUserRepository.findByUsername("username")).thenReturn(java.util.Optional.of(user));
            String newPassword = "passwordNew";
            ChangePassword changePassword = new ChangePassword();
            changePassword.setOldPassword("passwordWrong");
            changePassword.setNewPassword(newPassword);
            assertThrows(ApiBadRequestException.class, () -> testUserService.changePassword(changePassword));
        }
    }

    @Test
    void testLoadAssignedUser() {
        Long id = 1L;
        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(user));
        testUserService.loadAssignedUser(id);
        verify(testUserRepository).findById(id);
    }

    @Test
    void testLoadAssignedUserNotFound() {
        Long id = 1L;
        Mockito.when(testUserRepository.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> testUserService.loadAssignedUser(id));
    }
}