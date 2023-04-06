package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.products.AddProductRequest;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.trade.AddOrderRequest;
import com.simpleerp.simpleerpapp.services.TradeService;
import com.simpleerp.simpleerpapp.services.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trade")
@CrossOrigin("http://localhost:4200")
public class TradeController {

    private final TradeService tradeService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public TradeController(TradeService tradeService, MessageSource messageSource, ModelMapper modelMapper) {
        this.tradeService = tradeService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadProductList() {
        List<ProductCode> productCodeList = tradeService.loadProductList();
        return ResponseEntity.ok(productCodeList);
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> addOrder(@RequestBody AddOrderRequest addOrderRequest) {
        tradeService.addOrder(addOrderRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.orderAdded", null, LocaleContextHolder.getLocale())));
    }

}
