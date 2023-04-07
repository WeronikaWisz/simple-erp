package com.simpleerp.simpleerpapp.controllers;


import com.simpleerp.simpleerpapp.dtos.manageusers.ProfileData;
import com.simpleerp.simpleerpapp.dtos.products.ProductListItem;
import com.simpleerp.simpleerpapp.dtos.trade.CustomerData;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.models.Customer;
import com.simpleerp.simpleerpapp.models.User;
import com.simpleerp.simpleerpapp.services.CustomersService;
import com.simpleerp.simpleerpapp.services.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
@CrossOrigin("http://localhost:4200")
public class CustomersController {

    private final CustomersService customersService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public CustomersController(CustomersService customersService, MessageSource messageSource, ModelMapper modelMapper) {
        this.customersService = customersService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE') or hasRole('ROLE_PRODUCTION') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> getCustomer(@PathVariable("id") Long id) {
        CustomerData customerData = mapUserToProfileData(customersService.getCustomer(id));
        return ResponseEntity.ok(customerData);
    }

    private CustomerData mapUserToProfileData(Customer customer){
        return modelMapper.map(customer, CustomerData.class);
    }

}
