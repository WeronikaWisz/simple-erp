package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.products.UpdateContractorRequest;
import com.simpleerp.simpleerpapp.dtos.trade.*;
import com.simpleerp.simpleerpapp.dtos.warehouse.AcceptanceDetails;
import com.simpleerp.simpleerpapp.dtos.warehouse.DelegatedTasksResponse;
import com.simpleerp.simpleerpapp.dtos.warehouse.ReleaseDetails;
import com.simpleerp.simpleerpapp.dtos.warehouse.ReleasesAcceptancesResponse;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.ETask;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.helpers.ExcelHelper;
import com.simpleerp.simpleerpapp.services.TradeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @GetMapping("/order-products")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadOrderProductList() {
        List<ProductCode> productCodeList = tradeService.loadOrderProductList();
        return ResponseEntity.ok(productCodeList);
    }

    @PostMapping("/order")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> addOrder(@RequestBody AddOrderRequest addOrderRequest) {
        tradeService.addOrder(addOrderRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.orderAdded", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/orders/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadOrders(@PathVariable("status") EStatus status,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        OrdersResponse ordersResponse = tradeService.loadOrders(status, page, size);
        return ResponseEntity.ok(ordersResponse);
    }

    @DeleteMapping("/order/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id) {
        tradeService.deleteOrder(id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.orderDeleted", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadUsers() {
        List<UserName> userNameList = tradeService.loadUsers();
        return ResponseEntity.ok(userNameList);
    }

    @PutMapping("/assigned-user")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> updateAssignedUser(@RequestBody UpdateAssignedUserRequest updateAssignedUserRequest) {
        tradeService.updateAssignedUser(updateAssignedUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.assignedUserUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/order/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> getOrder(@PathVariable("id") Long id) {
        UpdateOrderRequest order = tradeService.getOrder(id);
        return ResponseEntity.ok(order);
    }

    @PutMapping("/order")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> updateOrder(@RequestBody UpdateOrderRequest updateOrderRequest) {
        tradeService.updateOrder(updateOrderRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.orderUpdate", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/external-release")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> delegateExternalRelease(@RequestBody List<Long> ids) {
        tradeService.delegateExternalRelease(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.releaseDelegated", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/mark-received")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> markAsReceived(@RequestBody List<Long> ids) {
        tradeService.markOrderAsReceived(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markReceived", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/purchase-tasks/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadPurchaseTasks(@PathVariable("status") EStatus status,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        DelegatedTasksResponse delegatedTasksResponse = tradeService.loadPurchaseTasks(status, page, size);
        return ResponseEntity.ok(delegatedTasksResponse);
    }

    @PostMapping("/external-acceptance")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> delegateExternalAcceptance(@RequestBody DelegateExternalAcceptance delegateExternalAcceptance) {
        tradeService.delegateExternalAcceptance(delegateExternalAcceptance);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.acceptanceDelegated", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("purchases/mark-in-progress")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> markPurchaseAsInProgress(@RequestBody List<Long> ids) {
        tradeService.markPurchaseAsInProgress(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markPurchase", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("purchases/mark-done")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> markPurchaseAsDone(@RequestBody List<Long> ids) {
        tradeService.markPurchaseAsDone(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markPurchase", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/delegated-tasks/{task}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> loadDelegatedTasks(@PathVariable("task") ETask task,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = tradeService.loadDelegatedTasks(task, page, size);
        return ResponseEntity.ok(releasesAcceptancesResponse);
    }

    @GetMapping("/release/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> getRelease(@PathVariable("id") Long id) {
        ReleaseDetails release = tradeService.getRelease(id);
        return ResponseEntity.ok(release);
    }

    @GetMapping("/acceptance/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> getAcceptance(@PathVariable("id") Long id) {
        AcceptanceDetails acceptance = tradeService.getAcceptance(id);
        return ResponseEntity.ok(acceptance);
    }

    @GetMapping("/purchase/contractor/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> getContractor(@PathVariable("id") Long id) {
        UpdateContractorRequest contractor = tradeService.getContractor(id);
        return ResponseEntity.ok(contractor);
    }

    @PostMapping("/orders/import")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_TRADE')")
    public ResponseEntity<?> train(@RequestParam("file") MultipartFile file) {
        if(ExcelHelper.hasExcelFormat(file)) {
            tradeService.importOrders(file);
        } else {
            throw new ApiExpectationFailedException("exception.wrongFileFormat");
        }
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.import", null, LocaleContextHolder.getLocale())));
    }
}
