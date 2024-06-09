package com.woopaca.laboratory.playground;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

@Slf4j
public record Surveyor(String name, String phone, String cafe) {

    public static Surveyor fromExcelRow(Row row) {
        String name = row.getCell(13)
                .getStringCellValue();
        String phone = row.getCell(14)
                .getStringCellValue();
        String cafe = row.getCell(15)
                .getStringCellValue();

        return new Surveyor(name, phone, cafe);
    }
}
