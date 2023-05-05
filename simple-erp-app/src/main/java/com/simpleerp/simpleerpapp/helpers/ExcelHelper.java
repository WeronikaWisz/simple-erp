package com.simpleerp.simpleerpapp.helpers;

import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingRecord;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingTrainingData;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingTrainingElement;
import com.simpleerp.simpleerpapp.dtos.trade.AddOrderRequest;
import com.simpleerp.simpleerpapp.dtos.trade.OrderProductQuantity;
import com.simpleerp.simpleerpapp.exception.ApiExpectationFailedException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class ExcelHelper {

    private static final String TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String TYPE_XLS = "application/vnd.ms-excel";
    private static final Integer CELL_INDEX_0 = 0;
    private static final Integer CELL_INDEX_1 = 1;
    private static final Integer CELL_INDEX_2 = 2;
    private static final Integer CELL_INDEX_3 = 3;
    private static final Integer CELL_INDEX_4 = 4;
    private static final Integer CELL_INDEX_5 = 5;
    private static final Integer CELL_INDEX_6 = 6;
    private static final Integer CELL_INDEX_7 = 7;
    private static final Integer CELL_INDEX_8 = 8;
    private static final Integer CELL_INDEX_9 = 9;
    private static final Integer CELL_INDEX_10 = 10;
    private static final Integer CELL_INDEX_11 = 11;
    private static final Integer CELL_INDEX_12 = 12;
    private static final Integer CELL_INDEX_13 = 13;
    private static final Integer CELL_INDEX_14 = 14;
    private static final Integer CELL_INDEX_15 = 15;
    private static final Integer CELL_INDEX_16 = 16;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelHelper.class);

    private ExcelHelper(){}

    public static boolean hasExcelFormat(MultipartFile file){
        return (TYPE_XLSX.equals(file.getContentType()) || TYPE_XLS.equals(file.getContentType()));
    }

    public static ForecastingTrainingData createTrainingFile(InputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);

        var sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        List<ForecastingRecord> forecastingRecords = new ArrayList<>();

        var rowNumber = 0;
        while (rows.hasNext()) {
            var currenRow = rows.next();

            if(rowNumber == 0){
                rowNumber ++;
                continue;
            }

            var forecastingRecord = new ForecastingRecord();
            fillForecastingRecord(currenRow, forecastingRecord);
            forecastingRecords.add(forecastingRecord);
        }

        workbook.close();

        LocalDateTime startDate = forecastingRecords.stream().min(Comparator.comparing(ForecastingRecord::getDate)).get().getDate();
        LocalDateTime endDate = forecastingRecords.stream().max(Comparator.comparing(ForecastingRecord::getDate)).get().getDate();

        LOGGER.info("Start date " + startDate);
        LOGGER.info("End date " + endDate);

        forecastingRecords.sort(Comparator.comparing(ForecastingRecord::getProductCode).thenComparing(ForecastingRecord::getDate));

        List<ForecastingTrainingElement> forecastingTrainingElements = new ArrayList<>();

        LocalDateTime previousDate = startDate;
        String previousProduct = "";

        long currentId = 0L;

        for(ForecastingRecord forecastingRecord: forecastingRecords){
            String currentProduct = forecastingRecord.getProductCode();
            LocalDateTime currenDate = forecastingRecord.getDate();
            if(!previousProduct.equals(currentProduct)){
                if(!previousProduct.equals("")){
                    long daysBetweenCurrentDateAndEnd = Duration.between(previousDate, endDate).toDays();
                    for(long i=0; i<daysBetweenCurrentDateAndEnd; i++){
                        String finalPreviousProduct = previousProduct;
                        forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(finalPreviousProduct))
                                .findFirst().get().getDayQuantity().add("NaN");
                    }
                }
                ForecastingTrainingElement forecastingTrainingElement = new ForecastingTrainingElement();
                forecastingTrainingElement.setId(currentId);
                forecastingTrainingElement.setItemId(currentProduct);
                long daysBetweenStartDateAndProduct = Duration.between(startDate, currenDate).toDays();
                for(long i=0; i<daysBetweenStartDateAndProduct; i++){
                    forecastingTrainingElement.getDayQuantity().add("NaN");
                }
                forecastingTrainingElement.getDayQuantity().add(forecastingRecord.getQuantity());
                forecastingTrainingElements.add(forecastingTrainingElement);
                currentId += 1;
            } else {
                if(previousDate.toLocalDate().equals(currenDate.toLocalDate())){
                    BigDecimal newQuantity = new BigDecimal(forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(currentProduct))
                            .findFirst().get().getDayQuantity().get(forecastingTrainingElements.stream()
                                    .filter(fr -> fr.getItemId().equals(currentProduct))
                                    .findFirst().get().getDayQuantity().size() - 1)).add(new BigDecimal(forecastingRecord.getQuantity()));
                    forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(currentProduct))
                            .findFirst().get().getDayQuantity().remove(forecastingTrainingElements.stream()
                                    .filter(fr -> fr.getItemId().equals(currentProduct))
                                    .findFirst().get().getDayQuantity().size() - 1);
                    forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(currentProduct))
                            .findFirst().get().getDayQuantity().add(newQuantity.toString());
                } else {
                    long daysBetweenCurrentDateAndPrevious = Duration.between(previousDate, currenDate).toDays();
                    for(long i=1; i<daysBetweenCurrentDateAndPrevious; i++){
                        forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(currentProduct))
                                .findFirst().get().getDayQuantity().add("NaN");
                    }
                    forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(currentProduct))
                            .findFirst().get().getDayQuantity().add(forecastingRecord.getQuantity());
                }
            }

            previousProduct = currentProduct;
            previousDate = currenDate;
        }

        long daysBetweenCurrentDateAndEnd = Duration.between(previousDate, endDate).toDays();
        for(long i=0; i<daysBetweenCurrentDateAndEnd; i++){
            String finalPreviousProduct = previousProduct;
            forecastingTrainingElements.stream().filter(fr -> fr.getItemId().equals(finalPreviousProduct))
                    .findFirst().get().getDayQuantity().add("NaN");
        }

        ForecastingTrainingData forecastingTrainingData = new ForecastingTrainingData();
        forecastingTrainingData.setForecastingTrainingElementList(forecastingTrainingElements);
        forecastingTrainingData.setStartDate(startDate);
        forecastingTrainingData.setEndDate(endDate);

        return forecastingTrainingData;
    }

    private static void fillForecastingRecord(Row currenRow, ForecastingRecord forecastingRecord) {
        if(currenRow.getCell(CELL_INDEX_0) != null){
            forecastingRecord.setDate(LocalDate.parse(currenRow.getCell(CELL_INDEX_0).getStringCellValue(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay());
        }
        if(currenRow.getCell(CELL_INDEX_1) != null){
            forecastingRecord.setProductCode(currenRow.getCell(CELL_INDEX_1).getStringCellValue());
        }
        if(currenRow.getCell(CELL_INDEX_2) != null){
            forecastingRecord.setQuantity(String.valueOf(currenRow.getCell(CELL_INDEX_2).getNumericCellValue()));
        }
    }

    public static List<AddOrderRequest> createOrders(InputStream inputStream) throws IOException {

        Workbook workbook = new XSSFWorkbook(inputStream);

        var sheet = workbook.getSheetAt(0);
        Iterator<Row> rows = sheet.iterator();

        Map<String, AddOrderRequest> addOrderRequests = new HashMap<>();

        var rowNumber = 0;
        while (rows.hasNext()) {
            var currenRow = rows.next();

            if (rowNumber == 0) {
                rowNumber++;
                continue;
            }

            String orderNumber;

            if(currenRow.getCell(CELL_INDEX_0) != null){
                if(currenRow.getCell(CELL_INDEX_0).getCellType() == CellType.NUMERIC) {
                    orderNumber = (String.valueOf(currenRow.getCell(CELL_INDEX_0).getNumericCellValue()));
                } else {
                    orderNumber = (currenRow.getCell(CELL_INDEX_0).getStringCellValue());
                }
            } else {
                throw new ApiExpectationFailedException("exception.importOrders");
            }

            if(!addOrderRequests.containsKey(orderNumber)) {
                var addOrderRequest = new AddOrderRequest();
                fillOrderRecord(currenRow, addOrderRequest);
                addOrderRequests.put(orderNumber, addOrderRequest);
            } else {
                addProductToOrder(currenRow, addOrderRequests.get(orderNumber));
            }
        }

        workbook.close();

        return addOrderRequests.values().stream().toList();
    }

    private static void fillOrderRecord(Row currenRow, AddOrderRequest addOrderRequest) {
        if(currenRow.getCell(CELL_INDEX_0) != null){
            if(currenRow.getCell(CELL_INDEX_0).getCellType() == CellType.NUMERIC) {
                addOrderRequest.setNumber(String.valueOf(currenRow.getCell(CELL_INDEX_0).getNumericCellValue()));
            } else {
                addOrderRequest.setNumber(currenRow.getCell(CELL_INDEX_0).getStringCellValue());
            }
        }
        if(currenRow.getCell(CELL_INDEX_1) != null){
            addOrderRequest.setOrderDate(currenRow.getCell(CELL_INDEX_1).getLocalDateTimeCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_2) != null){
            addOrderRequest.setDiscount(String.valueOf(currenRow.getCell(CELL_INDEX_2).getNumericCellValue()));
        }
        if(currenRow.getCell(CELL_INDEX_3) != null){
            addOrderRequest.setDelivery(String.valueOf(currenRow.getCell(CELL_INDEX_3).getNumericCellValue()));
        }
        if(currenRow.getCell(CELL_INDEX_4) != null){
            addOrderRequest.setPrice(String.valueOf(currenRow.getCell(CELL_INDEX_4).getNumericCellValue()));
        }
        if(currenRow.getCell(CELL_INDEX_5) != null){
            addOrderRequest.setName(currenRow.getCell(CELL_INDEX_5).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_6) != null){
            addOrderRequest.setSurname(currenRow.getCell(CELL_INDEX_6).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_7) != null){
            addOrderRequest.setEmail(currenRow.getCell(CELL_INDEX_7).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_8) != null){
            if(currenRow.getCell(CELL_INDEX_8).getCellType() == CellType.NUMERIC) {
                addOrderRequest.setPhone(String.valueOf(currenRow.getCell(CELL_INDEX_8).getNumericCellValue()));
            } else {
                addOrderRequest.setPhone(currenRow.getCell(CELL_INDEX_8).getStringCellValue());
            }
        }
        if(currenRow.getCell(CELL_INDEX_9) != null){
            addOrderRequest.setPostalCode(currenRow.getCell(CELL_INDEX_9).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_10) != null){
            addOrderRequest.setPost(currenRow.getCell(CELL_INDEX_10).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_11) != null){
            addOrderRequest.setCity(currenRow.getCell(CELL_INDEX_11).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_12) != null){
            addOrderRequest.setStreet(currenRow.getCell(CELL_INDEX_12).getStringCellValue());
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_13) != null){
            if(currenRow.getCell(CELL_INDEX_13).getCellType() == CellType.NUMERIC) {
                addOrderRequest.setBuildingNumber(String.valueOf(currenRow.getCell(CELL_INDEX_13).getNumericCellValue()));
            } else {
                addOrderRequest.setBuildingNumber(currenRow.getCell(CELL_INDEX_13).getStringCellValue());
            }
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_14) != null){
            if(currenRow.getCell(CELL_INDEX_14).getCellType() == CellType.NUMERIC) {
                addOrderRequest.setDoorNumber(String.valueOf(currenRow.getCell(CELL_INDEX_14).getNumericCellValue()));
            } else {
                addOrderRequest.setDoorNumber(currenRow.getCell(CELL_INDEX_14).getStringCellValue());
            }
        }
        OrderProductQuantity orderProductQuantity = new OrderProductQuantity();
        if(currenRow.getCell(CELL_INDEX_15) != null){
            if(currenRow.getCell(CELL_INDEX_15).getCellType() == CellType.NUMERIC) {
                orderProductQuantity.setProduct(String.valueOf(currenRow.getCell(CELL_INDEX_15).getNumericCellValue()));
            } else {
                orderProductQuantity.setProduct(currenRow.getCell(CELL_INDEX_15).getStringCellValue());
            }
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_16) != null){
            if(currenRow.getCell(CELL_INDEX_16).getCellType() == CellType.NUMERIC) {
                orderProductQuantity.setQuantity(String.valueOf(currenRow.getCell(CELL_INDEX_16).getNumericCellValue()));
            } else {
                orderProductQuantity.setQuantity(currenRow.getCell(CELL_INDEX_16).getStringCellValue());
            }
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        List<OrderProductQuantity> orderProductQuantityList = new ArrayList<>();
        orderProductQuantityList.add(orderProductQuantity);
        addOrderRequest.setProductSet(orderProductQuantityList);
    }

    private static void addProductToOrder(Row currenRow, AddOrderRequest addOrderRequest) {
        OrderProductQuantity orderProductQuantity = new OrderProductQuantity();
        if(currenRow.getCell(CELL_INDEX_15) != null){
            if(currenRow.getCell(CELL_INDEX_15).getCellType() == CellType.NUMERIC) {
                orderProductQuantity.setProduct(String.valueOf(currenRow.getCell(CELL_INDEX_15).getNumericCellValue()));
            } else {
                orderProductQuantity.setProduct(currenRow.getCell(CELL_INDEX_15).getStringCellValue());
            }
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        if(currenRow.getCell(CELL_INDEX_16) != null){
            if(currenRow.getCell(CELL_INDEX_16).getCellType() == CellType.NUMERIC) {
                orderProductQuantity.setQuantity(String.valueOf(currenRow.getCell(CELL_INDEX_16).getNumericCellValue()));
            } else {
                orderProductQuantity.setQuantity(currenRow.getCell(CELL_INDEX_16).getStringCellValue());
            }
        } else {
            throw new ApiExpectationFailedException("exception.importOrders");
        }
        addOrderRequest.getProductSet().add(orderProductQuantity);
    }
}
