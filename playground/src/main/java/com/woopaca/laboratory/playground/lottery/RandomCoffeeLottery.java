package com.woopaca.laboratory.playground.lottery;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class RandomCoffeeLottery {

    public static final String FILE_LOCATION = "/Users/jcw1031/Develop/Test/laboratory/playground/src/main/resources/static/taxipod-survey-result.xlsx";

    public static void main(String[] args) {
        try (FileInputStream inputStream = new FileInputStream(FILE_LOCATION)) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            List<Surveyor> surveyors = new ArrayList<>();
            for (Row row : sheet) {
                Surveyor surveyor = Surveyor.fromExcelRow(row);

                if (surveyor.name().equals("이름") || surveyor.name().isBlank()
                        || surveyor.phone().length() != 11 || !surveyor.phone().startsWith("010")) {
                    continue;
                }
                surveyors.add(surveyor);
            }

            Collections.shuffle(surveyors);
            List<Surveyor> winners = surveyors.subList(0, 5);
            log.info("winners!! = {}", winners);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

}
