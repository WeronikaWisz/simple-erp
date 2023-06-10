package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleerp.simpleerpapp.dtos.auth.AddUserRequest;
import com.simpleerp.simpleerpapp.dtos.auth.UpdateUserRequest;
import com.simpleerp.simpleerpapp.dtos.manageusers.*;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.security.WebSecurityConfig;
import com.simpleerp.simpleerpapp.security.jwt.AuthEntryPointJwt;
import com.simpleerp.simpleerpapp.security.jwt.JwtUtils;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsServiceI;
import com.simpleerp.simpleerpapp.services.ManageUsersService;
import com.simpleerp.simpleerpapp.services.UsersService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ManageUsersController.class)
@Import(WebSecurityConfig.class)
class ManageUsersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ManageUsersService testManageUsersService;

    @MockBean
    private ModelMapper modelMapper;

    private MessageSource messageSource;

    @Autowired
    private DelegatingMessageSource delegatingMessageSource;

    @MockBean
    private UserDetailsServiceI userDetailsServiceI;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
        messageSource = Mockito.mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), any(Object[].class),any(Locale.class))).thenReturn("");
        delegatingMessageSource.setParentMessageSource(messageSource);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testAddUser() throws Exception {
        AddUserRequest addUserRequest = new AddUserRequest();
        addUserRequest.setName("username");

        Mockito.doNothing().when(testManageUsersService).addUser(addUserRequest);

        mockMvc.perform(post("/manage-users/user")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(addUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testManageUsersService).addUser(addUserRequest);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testLoadUsers() throws Exception {
        UsersResponse usersResponse = new UsersResponse();

        Mockito.when(testManageUsersService.loadUsers(0,10)).thenReturn(usersResponse);

        mockMvc.perform(get("/manage-users/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testManageUsersService).loadUsers(0,10);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        Long id = 1L;
        Mockito.doNothing().when(testManageUsersService).deleteUser(id);

        mockMvc.perform(delete("/manage-users/user/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testManageUsersService).deleteUser(id);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateUser() throws Exception {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        Mockito.doNothing().when(testManageUsersService).updateUser(updateUserRequest);

        mockMvc.perform(put("/manage-users/user")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testManageUsersService).updateUser(updateUserRequest);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetUser() throws Exception {
        Long id = 1L;

        UserListItem userListItem = new UserListItem();

        Mockito.when(testManageUsersService.getUser(id)).thenReturn(userListItem);

        mockMvc.perform(get("/manage-users/user/{id}",id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testManageUsersService).getUser(id);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testLoadDefaultUsers() throws Exception {
        DefaultUser defaultUser = new DefaultUser();
        List<DefaultUser> defaultUserList = new ArrayList<>();
        defaultUserList.add(defaultUser);

        Mockito.when(testManageUsersService.loadDefaultUsers(0,10)).thenReturn(defaultUserList);

        mockMvc.perform(get("/manage-users/default-users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testManageUsersService).loadDefaultUsers(0,10);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateDefaultUser() throws Exception {
        UpdateDefaultUserRequest updateDefaultUserRequest = new UpdateDefaultUserRequest();

        Mockito.doNothing().when(testManageUsersService).updateDefaultUser(updateDefaultUserRequest);

        mockMvc.perform(put("/manage-users/default-user")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updateDefaultUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(testManageUsersService).updateDefaultUser(updateDefaultUserRequest);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testLoadUserForTask() throws Exception {
        Long id = 1L;
        List<UserName> userNameList = new ArrayList<>();

        Mockito.when(testManageUsersService.loadUserForTask(id)).thenReturn(userNameList);

        mockMvc.perform(get("/manage-users/users-task/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testManageUsersService).loadUserForTask(id);
    }
}