package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.warehouse.PurchaseTaskRequest;
import com.simpleerp.simpleerpapp.dtos.warehouse.SuppliesResponse;
import com.simpleerp.simpleerpapp.dtos.warehouse.UpdateSuppliesRequest;
import com.simpleerp.simpleerpapp.services.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplies")
@CrossOrigin("http://localhost:4200")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, MessageSource messageSource, ModelMapper modelMapper) {
        this.warehouseService = warehouseService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadSupplies(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        SuppliesResponse suppliesResponse = warehouseService.loadSupplies(page, size);
        return ResponseEntity.ok(suppliesResponse);
    }

    @PutMapping("/supplies")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> updateSupplies(@RequestBody UpdateSuppliesRequest updateSuppliesRequest) {
        warehouseService.updateSupplies(updateSuppliesRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.suppliesUpdate", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/delegate-purchase")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> delegatePurchaseTask(@RequestBody PurchaseTaskRequest purchaseTaskRequest) {
        warehouseService.delegatePurchaseTask(purchaseTaskRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.purchaseTypeCreated", null, LocaleContextHolder.getLocale())));
    }

}
