package com.simpleerp.simpleerpapp.controllers.manageusers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleerp.simpleerpapp.dtos.auth.LoginRequest;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.RoleRepository;
import com.simpleerp.simpleerpapp.repositories.UserRepository;
import com.simpleerp.simpleerpapp.security.WebSecurityConfig;
import com.simpleerp.simpleerpapp.security.jwt.AuthEntryPointJwt;
import com.simpleerp.simpleerpapp.security.jwt.JwtUtils;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsI;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsServiceI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private UserDetailsServiceI userDetailsServiceI;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    UserRepository testUserRepository;

    @MockBean
    RoleRepository testRoleRepository;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    PasswordEncoder encoder;

    @MockBean
    private JwtUtils jwtUtils;

    private MessageSource messageSource;

    @Autowired
    private DelegatingMessageSource delegatingMessageSource;

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
    void testAuthenticateUser() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);

        String username = "username";
        String password = "password";

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail("test1@gmail.com");
        user.setIsDeleted(false);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());

        UserDetailsI applicationUser = UserDetailsI.build(user);

        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(applicationUser);

        mockMvc.perform(post("/auth/signin")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(loginRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(authenticationManager).authenticate(authenticationToken);
        verify(jwtUtils).generateJwtToken(authentication);
    }
}