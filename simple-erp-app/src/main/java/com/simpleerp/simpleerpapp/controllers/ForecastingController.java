package com.simpleerp.simpleerpapp.controllers;

import com.simpleerp.simpleerpapp.dtos.auth.MessageResponse;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingActive;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import com.simpleerp.simpleerpapp.helpers.ExcelHelper;
import com.simpleerp.simpleerpapp.services.ForecastingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/forecasting")
@CrossOrigin("http://localhost:4200")
public class ForecastingController {

    private final ForecastingService forecastingService;
    private MessageSource messageSource;
    private ModelMapper modelMapper;

    @Autowired
    public ForecastingController(ForecastingService forecastingService, MessageSource messageSource, ModelMapper modelMapper) {
        this.forecastingService = forecastingService;
        this.messageSource = messageSource;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> checkForecastingState() {
        ForecastingActive forecastingActive = forecastingService.checkForecastingState();
        return ResponseEntity.ok(forecastingActive);
    }

    @PostMapping("/training")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> train(@RequestParam("file")MultipartFile file) {
        if(ExcelHelper.hasExcelFormat(file)) {
            forecastingService.train(file);
        } else {
            throw new ApiExpectationFailedException("exception.wrongFileFormat");
        }
        return ResponseEntity.ok(new MessageResponse(messageSource.getMessage(
                "success.training", null, LocaleContextHolder.getLocale())));
    }
}
