package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.products.ProductsResponse;
import com.simpleerp.simpleerpapp.dtos.supplies.SuppliesResponse;
import com.simpleerp.simpleerpapp.services.SuppliesService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplies")
@CrossOrigin("http://localhost:4200")
public class SuppliesController {

    private final SuppliesService suppliesService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public SuppliesController(SuppliesService suppliesService, MessageSource messageSource, ModelMapper modelMapper) {
        this.suppliesService = suppliesService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadProducts(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        SuppliesResponse suppliesResponse = suppliesService.loadSupplies(page, size);
        return ResponseEntity.ok(suppliesResponse);
    }

}
