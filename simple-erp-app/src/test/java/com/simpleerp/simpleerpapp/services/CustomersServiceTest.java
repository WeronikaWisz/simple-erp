package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.Customer;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.repositories.CustomerRepository;
import com.simpleerp.simpleerpapp.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomersServiceTest {

    @Mock
    private CustomerRepository testCustomerRepository;
    private CustomersService testCustomersService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        testCustomersService = new CustomersService(testCustomerRepository);
        customer = new Customer();
    }

    @Test
    void testGetCustomer() {
        Long id = 1L;
        Mockito.when(testCustomerRepository.findById(id)).thenReturn(java.util.Optional.ofNullable(customer));
        testCustomersService.getCustomer(id);
        verify(testCustomerRepository).findById(id);
    }

    @Test
    void testGetCustomerNotFound() {
        Long id = 1L;
        Mockito.when(testCustomerRepository.findById(id)).thenReturn(java.util.Optional.empty());
        assertThrows(ApiNotFoundException.class, () -> testCustomersService.getCustomer(id));
    }
}