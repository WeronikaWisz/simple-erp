package com.simpleerp.simpleerpapp.forecasting;

import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingRecord;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingTrainingData;
import com.simpleerp.simpleerpapp.dtos.forecasting.ForecastingTrainingElement;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
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
}
