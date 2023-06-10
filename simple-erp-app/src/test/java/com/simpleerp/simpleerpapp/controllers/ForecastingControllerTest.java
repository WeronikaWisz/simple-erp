package com.simpleerp.simpleerpapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingActive;
import com.simpleerp.simpleerpapp.dtos.forecasting.TrainingEvaluationData;
import com.simpleerp.simpleerpapp.dtos.products.ProductCode;
import com.simpleerp.simpleerpapp.security.WebSecurityConfig;
import com.simpleerp.simpleerpapp.security.jwt.AuthEntryPointJwt;
import com.simpleerp.simpleerpapp.security.jwt.JwtUtils;
import com.simpleerp.simpleerpapp.security.userdetails.UserDetailsServiceI;
import com.simpleerp.simpleerpapp.services.ForecastingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ForecastingController.class)
@Import(WebSecurityConfig.class)
class ForecastingControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    private ForecastingService testForecastingService;

    @MockBean
    private ModelMapper modelMapper;

    private MessageSource messageSource;

    @Autowired
    private DelegatingMessageSource delegatingMessageSource;

    @MockBean
    private UserDetailsServiceI userDetailsServiceI;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    private JwtUtils jwtUtils;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
        messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), any(Object[].class),any(Locale.class))).thenReturn("");
        delegatingMessageSource.setParentMessageSource(messageSource);
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testCheckForecastingState() throws Exception {
        ForecastingActive forecastingActive = new ForecastingActive();

        Mockito.when(testForecastingService.checkForecastingState()).thenReturn(forecastingActive);

        mockMvc.perform(get("/forecasting/active")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testForecastingService).checkForecastingState();
    }

    @Test
    @WithMockUser()
    void testCheckForecastingStateNotAdmin() throws Exception {
        mockMvc.perform(get("/forecasting/active")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

//    @Test
//    @WithMockUser(roles = {"ADMIN"})
//    void testTrain() throws Exception {
//
//        MockMultipartFile file = new MockMultipartFile("file", new byte[1]);
//
//        Mockito.doNothing().when(testForecastingService).train(file);
//
//        MockedStatic<ExcelHelper> testExcelHelper = Mockito.mockStatic(ExcelHelper.class);
//        testExcelHelper.when(() -> ExcelHelper.hasExcelFormat(file))
//                    .thenReturn(true);
//
////        ExcelHelper testExcelHelper = mock(ExcelHelper.class);
////        when(testExcelHelper.hasExcelFormat(file)).thenReturn(true);
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/forecasting/training")
//                        .file(file)
//                        .with(csrf())
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        verify(testForecastingService).train(file);
//    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testGetTrainingEvaluation() throws Exception {
        String productCode = "code";
        TrainingEvaluationData trainingEvaluationData = new TrainingEvaluationData();

        Mockito.when(testForecastingService.getTrainingEvaluation(productCode)).thenReturn(trainingEvaluationData);

        mockMvc.perform(get("/forecasting/evaluation/{code}", productCode)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testForecastingService).getTrainingEvaluation(productCode);
    }

    @Test
    @WithMockUser()
    void testGetTrainingEvaluationNotAdmin() throws Exception {
        String productCode = "code";
        mockMvc.perform(get("/forecasting/evaluation/{code}", productCode)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void testLoadForecastProductList() throws Exception {
        List<ProductCode> productCodeList = new ArrayList<>();

        Mockito.when(testForecastingService.loadForecastProductList()).thenReturn(productCodeList);

        mockMvc.perform(get("/forecasting/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(testForecastingService).loadForecastProductList();
    }

    @Test
    @WithMockUser()
    void testLoadForecastProductListNotAdmin() throws Exception {
        mockMvc.perform(get("/forecasting/products")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn();
    }
}