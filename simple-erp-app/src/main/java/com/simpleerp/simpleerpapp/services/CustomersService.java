package com.simpleerp.simpleerpapp.services;

import com.simpleerp.simpleerpapp.exception.ApiNotFoundException;
import com.simpleerp.simpleerpapp.models.Customer;
import com.simpleerp.simpleerpapp.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomersService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomersService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ApiNotFoundException("exception.customerNotFound"));
    }
}
