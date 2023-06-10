package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleerp.simpleerpapp.dtos.manageusers.ChangePassword;
import com.simpleerp.simpleerpapp.dtos.manageusers.ProfileData;
import com.simpleerp.simpleerpapp.dtos.manageusers.UpdateUserData;
import com.simpleerp.simpleerpapp.dtos.warehouse.AssignedUser;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.security.WebSecurityConfig;
import com.simpleerp.simpleerpapp.security.jwt.AuthEntryPointJwt;
import com.simpleerp.simpleerpapp.security.jwt.JwtUtils;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsServiceI;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersController.class)
@Import(WebSecurityConfig.class)
class UsersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UsersService testUsersService;

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
    @WithMockUser(username = "username", roles = {"ADMIN"})
    void testGetUserProfileData() throws Exception {

        User user = new User();
        user.setUsername("username");

        ProfileData profileData = new ProfileData();
        profileData.setUsername("username");

        Mockito.when(testUsersService.getUserProfileData()).thenReturn(user);
        when(modelMapper.map(user, ProfileData.class)).thenReturn(profileData);

        mockMvc.perform(get("/users/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testUsersService).getUserProfileData();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateUserProfileData() throws Exception {
        UpdateUserData updateUserData = new UpdateUserData();

        Mockito.when(testUsersService.updateUserProfileData(updateUserData)).thenReturn(true);

        mockMvc.perform(put("/users/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserData)))
                .andExpect(status().isOk());

        verify(testUsersService).updateUserProfileData(updateUserData);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testUpdateUserProfileDataError() throws Exception {
        UpdateUserData updateUserData = new UpdateUserData();

        Mockito.when(testUsersService.updateUserProfileData(updateUserData)).thenReturn(false);

        mockMvc.perform(put("/users/user")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserData)))
                .andExpect(status().isExpectationFailed());

        verify(testUsersService).updateUserProfileData(updateUserData);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testChangePassword() throws Exception {
        ChangePassword changePassword = new ChangePassword();
        changePassword.setOldPassword("password");
        changePassword.setNewPassword("newPassword");

        Mockito.doNothing().when(testUsersService).changePassword(changePassword);

        mockMvc.perform(put("/users/user/password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changePassword)))
                .andExpect(status().isOk());

        verify(testUsersService).changePassword(changePassword);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testLoadAssignedUser() throws Exception {
        Long id = 1L;

        AssignedUser assignedUser = new AssignedUser();
        assignedUser.setId(id);

        Mockito.when(testUsersService.loadAssignedUser(id)).thenReturn(assignedUser);

        MvcResult result = mockMvc.perform(get("/users/assigned-user/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(objectMapper.writeValueAsString(assignedUser), result.getResponse().getContentAsString());
        verify(testUsersService).loadAssignedUser(id);
    }
}