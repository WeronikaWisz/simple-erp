package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.production.ProductProductionInfo;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.trade.DelegateExternalAcceptance;
import com.simpleerp.simpleerpapp.dtos.trade.UpdateAssignedUserRequest;
import com.simpleerp.simpleerpapp.dtos.warehouse.AcceptanceDetails;
import com.simpleerp.simpleerpapp.dtos.warehouse.DelegatedTasksResponse;
import com.simpleerp.simpleerpapp.dtos.warehouse.ReleaseDetails;
import com.simpleerp.simpleerpapp.dtos.warehouse.ReleasesAcceptancesResponse;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.ETask;
import com.simpleerp.simpleerpapp.services.ProductionService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/production")
@CrossOrigin("http://localhost:4200")
public class ProductionController {

    private final ProductionService productionService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public ProductionController(ProductionService productionService, MessageSource messageSource, ModelMapper modelMapper) {
        this.productionService = productionService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/production-tasks/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> loadProductionTasks(@PathVariable("status") EStatus status,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "10") int size) {
        DelegatedTasksResponse delegatedTasksResponse = productionService.loadProductionTasks(status, page, size);
        return ResponseEntity.ok(delegatedTasksResponse);
    }

    @PostMapping("productions/mark-in-progress")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> markProductionAsInProgress(@RequestBody List<Long> ids) {
        productionService.markProductionAsInProgress(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markProduction", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("productions/mark-done")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> markProductionAsDone(@RequestBody List<Long> ids) {
        productionService.markProductionAsDone(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markProduction", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/internal-acceptance")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> delegateInternalAcceptance(@RequestBody List<Long> ids) {
        productionService.delegateInternalAcceptance(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.acceptanceDelegated", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/internal-release")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> delegateInternalRelease(@RequestBody List<Long> ids) {
        productionService.delegateInternalRelease(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.releaseDelegated", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> loadUsers() {
        List<UserName> userNameList = productionService.loadUsers();
        return ResponseEntity.ok(userNameList);
    }

    @PutMapping("/assigned-user")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> updateAssignedUser(@RequestBody UpdateAssignedUserRequest updateAssignedUserRequest) {
        productionService.updateAssignedUser(updateAssignedUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.assignedUserUpdate", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/delegated-tasks/{task}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> loadDelegatedTasks(@PathVariable("task") ETask task,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = productionService.loadDelegatedTasks(task, page, size);
        return ResponseEntity.ok(releasesAcceptancesResponse);
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> loadProductList() {
        List<ProductCode> productCodeList = productionService.loadProductList();
        return ResponseEntity.ok(productCodeList);
    }

    @GetMapping("/release/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> getRelease(@PathVariable("id") Long id) {
        ReleaseDetails release = productionService.getRelease(id);
        return ResponseEntity.ok(release);
    }

    @GetMapping("/acceptance/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> getAcceptance(@PathVariable("id") Long id) {
        AcceptanceDetails acceptance = productionService.getAcceptance(id);
        return ResponseEntity.ok(acceptance);
    }

    @GetMapping("/product-info/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_PRODUCTION')")
    public ResponseEntity<?> getProductProduction(@PathVariable("id") Long id) {
        ProductProductionInfo productProductionInfo = productionService.getProductProduction(id);
        return ResponseEntity.ok(productProductionInfo);
    }

}
