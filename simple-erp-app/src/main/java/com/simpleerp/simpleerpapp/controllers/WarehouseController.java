package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingActive;
import com.simpleerp.simpleerpapp.dtos.manageusers.UserName;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.dtos.products.ProductQuantity;
import com.simpleerp.simpleerpapp.dtos.trade.UpdateAssignedUserRequest;
import com.simpleerp.simpleerpapp.dtos.warehouse.*;
import com.simpleerp.simpleerpapp.enums.EDirection;
import com.simpleerp.simpleerpapp.enums.EStatus;
import com.simpleerp.simpleerpapp.enums.EType;
import com.simpleerp.simpleerpapp.services.WarehouseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
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
                "success.purchaseTaskCreated", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("/delegate-production")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> delegateProductionTask(@RequestBody PurchaseTaskRequest purchaseTaskRequest) {
        warehouseService.delegateProductionTask(purchaseTaskRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.productionTaskCreated", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/delegated-tasks/{type}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadDelegatedTasks(@PathVariable("type") EType type,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        DelegatedTasksResponse delegatedTasksResponse = warehouseService.loadDelegatedTasks(type, page, size);
        return ResponseEntity.ok(delegatedTasksResponse);
    }

    @PutMapping("/purchase-task")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> updatePurchaseTask(@RequestBody PurchaseTaskRequest purchaseTaskRequest) {
        warehouseService.updatePurchaseTask(purchaseTaskRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.taskUpdated", null, LocaleContextHolder.getLocale())));
    }

    @PutMapping("/production-task")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> updateProductionTask(@RequestBody PurchaseTaskRequest purchaseTaskRequest) {
        warehouseService.updateProductionTask(purchaseTaskRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.taskUpdated", null, LocaleContextHolder.getLocale())));
    }

    @DeleteMapping("/task/{type}/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> deleteTask(@PathVariable("type") EType type, @PathVariable("id") Long id) {
        warehouseService.deleteTask(type, id);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.taskDeleted", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/releases/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadReleases(@PathVariable("status") EStatus status,
                                        @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = warehouseService.loadReleases(status, page, size);
        return ResponseEntity.ok(releasesAcceptancesResponse);
    }

    @GetMapping("/releases/{status}/{direction}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadReleases(@PathVariable("status") EStatus status,
                                          @PathVariable("direction")EDirection direction,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = warehouseService.loadReleases(status, direction, page, size);
        return ResponseEntity.ok(releasesAcceptancesResponse);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadUsers() {
        List<UserName> userNameList = warehouseService.loadUsers();
        return ResponseEntity.ok(userNameList);
    }

    @PutMapping("/assigned-user")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> updateReleaseAssignedUsers(@RequestBody UpdateAssignedUserRequest updateAssignedUserRequest) {
        warehouseService.updateAssignedUsers(updateAssignedUserRequest);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.assignedUserUpdate", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("releases/mark-in-progress")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> markReleaseAsInProgress(@RequestBody List<Long> ids) {
        warehouseService.markReleaseAsInProgress(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markRelease", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("releases/mark-done")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> markReleaseAsDone(@RequestBody List<Long> ids) {
        warehouseService.markReleaseAsDone(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markRelease", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/products")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadProductList() {
        List<ProductCode> productCodeList = warehouseService.loadProductList();
        return ResponseEntity.ok(productCodeList);
    }

    @GetMapping("/release/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> getRelease(@PathVariable("id") Long id) {
        ReleaseDetails release = warehouseService.getRelease(id);
        return ResponseEntity.ok(release);
    }

    @GetMapping("/acceptances/{status}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadAcceptances(@PathVariable("status") EStatus status,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = warehouseService.loadAcceptances(status, page, size);
        return ResponseEntity.ok(releasesAcceptancesResponse);
    }

    @GetMapping("/acceptances/{status}/{direction}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> loadAcceptances(@PathVariable("status") EStatus status,
                                          @PathVariable("direction") EDirection direction,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        ReleasesAcceptancesResponse releasesAcceptancesResponse = warehouseService.loadAcceptances(status, direction, page, size);
        return ResponseEntity.ok(releasesAcceptancesResponse);
    }

    @PostMapping("acceptances/mark-in-progress")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> markAcceptanceAsInProgress(@RequestBody List<Long> ids) {
        warehouseService.markAcceptanceAsInProgress(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markAcceptance", null, LocaleContextHolder.getLocale())));
    }

    @PostMapping("acceptances/mark-done")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> markAcceptanceAsDone(@RequestBody List<Long> ids) {
        warehouseService.markAcceptanceAsDone(ids);
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.markAcceptance", null, LocaleContextHolder.getLocale())));
    }

    @GetMapping("/acceptance/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> getAcceptance(@PathVariable("id") Long id) {
        AcceptanceDetails acceptance = warehouseService.getAcceptance(id);
        return ResponseEntity.ok(acceptance);
    }

    @GetMapping("/forecast/active/product/stock/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> checkProductForecastingState(@PathVariable("id") Long id) {
        ForecastingActive forecastingActive = warehouseService.checkProductForecastingState(id);
        return ResponseEntity.ok(forecastingActive);
    }

    @GetMapping("/forecast/active/product/task/{type}/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> checkTaskForecastingState(@PathVariable("type") EType type,
            @PathVariable("id") Long id) {
        ForecastingActive forecastingActive = warehouseService.checkTaskForecastingState(type, id);
        return ResponseEntity.ok(forecastingActive);
    }


    @GetMapping("/forecast/quantity/product/stock/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> suggestProductStockQuantity(@PathVariable("id") Long id,
                                                         @RequestParam(defaultValue = "0") int days) {
        ProductQuantity productQuantity = warehouseService.suggestProductStockQuantity(id, days);
        return ResponseEntity.ok(productQuantity);
    }

    @GetMapping("/forecast/quantity/product/task/{type}/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_WAREHOUSE')")
    public ResponseEntity<?> suggestProductTaskQuantity(@PathVariable("type") EType type,
                                                        @PathVariable("id") Long id,
                                                        @RequestParam(defaultValue = "0") int days) {
        ProductQuantity productQuantity = warehouseService.suggestProductTaskQuantity(type, id, days);
        return ResponseEntity.ok(productQuantity);
    }

}
