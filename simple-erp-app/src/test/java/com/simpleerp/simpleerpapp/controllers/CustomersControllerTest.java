package com.simpleerp.simpleerpapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleerp.simpleerpapp.dtos.trade.CustomerData;
import com.simpleerp.simpleerpapp.models.Customer;
import com.simpleerp.simpleerpapp.security.WebSecurityConfig;
import com.simpleerp.simpleerpapp.security.jwt.AuthEntryPointJwt;
import com.simpleerp.simpleerpapp.security.jwt.JwtUtils;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsServiceI;
import com.simpleerp.simpleerpapp.services.CustomersService;
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

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomersController.class)
@Import(WebSecurityConfig.class)
class CustomersControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private CustomersService testCustomersService;

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
    void testGetCustomer() throws Exception {
        Long id = 1L;

        Customer customer = new Customer();
        CustomerData customerData = new CustomerData();

        Mockito.when(testCustomersService.getCustomer(id)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerData.class)).thenReturn(customerData);

        mockMvc.perform(get("/customers/customer/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testCustomersService).getCustomer(id);
    }
}