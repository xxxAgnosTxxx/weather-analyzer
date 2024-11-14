package com.prokhorovgm.weather_analyzer.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataReader {
    private static final String DEFAULT_FILE = "src/main/resources/static/data.xlsx";
    private static final int SHEET_NUM = 0;
    private static final Sheet SHEET;

    static {
        try(FileInputStream fis = new FileInputStream(DEFAULT_FILE)) {
            Workbook workbook = new XSSFWorkbook(fis);
            SHEET = workbook.getSheetAt(SHEET_NUM);
            SHEET.removeRow(SHEET.getRow(SHEET_NUM));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(String.format("File not found by the path: %s", DEFAULT_FILE));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Can`t read excelFile by the path: %s", DEFAULT_FILE));
        }
    }

    public List<String> getValues(int cellNumber) {
        return getRows().stream()
            .map(row -> row.getCell(cellNumber))
            .map(Cell::getRichStringCellValue)
            .map(RichTextString::getString)
            .toList();
    }

    public List<Row> getRows() {
        List<Row> rows = new ArrayList<>(SHEET.getLastRowNum());
        for (Row row : SHEET)   rows.add(row);
        return rows;
    }
}
