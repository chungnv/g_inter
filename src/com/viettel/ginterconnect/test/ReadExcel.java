package com.viettel.ginterconnect.test;

import com.aerospike.client.Bin;
import com.viettel.ginterconnect.util.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;

/**
 * Created by sinhhv on 8/1/2017.
 */
public class ReadExcel {

    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        File folder = new File("../data");
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                System.out.println("File: " + file.getName());
                if (file.getName().endsWith("xlsx")) {
                    try {
                        System.out.println("Run file: " + file.getName());
                        FileInputStream inputStream = new FileInputStream(file);
                        Workbook workbook = new XSSFWorkbook(inputStream);
                        Sheet sheet = workbook.getSheetAt(0);

                        String tableName = sheet.getRow(1).getCell(1).getStringCellValue();
//                        GIClient.getInstance().deleteSet(tableName, SystemParam.getAerospikeNamespace());

                        Row rowHeader = sheet.getRow(2);
                        for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                            Row row = sheet.getRow(i);
                            String key = "";
                            if (row != null) {
                                Bin[] set = new Bin[rowHeader.getLastCellNum() - rowHeader.getFirstCellNum() - 1];
                                int index = 0;
                                Cell cellKey = row.getCell(rowHeader.getFirstCellNum());
                                if (cellKey == null) {
                                    continue;
                                }
                                cellKey.setCellType(CellType.STRING);
                                key = cellKey.getStringCellValue();
                                for (int j = rowHeader.getFirstCellNum() + 1; j < rowHeader.getLastCellNum(); j++) {
                                    String value = "";
                                    Cell cell = row.getCell(j);
                                    if (cell != null) {
                                        if (cell.getCellTypeEnum() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                                            value = sdf.format(cell.getDateCellValue());
                                        } else {
                                            cell.setCellType(CellType.STRING);
                                            value = cell.getStringCellValue();
                                        }
                                    }
                                    if (value == null) {
                                        continue;
                                    }
//                                    System.out.println("j = " + j + " value: " + value);
                                    System.out.println("j = " + j + " value: " + value);
                                    if (j == 6) {
                                        System.out.println("x");
                                    }
//                                    if (rowHeader.getCell(j) == null) {
//                                        continue;
//                                    }
                                    Bin bin = new Bin(rowHeader.getCell(j).getStringCellValue(), value.trim());
                                    set[index] = bin;
                                    index++;

                                }
                                GIClient.getInstance().insertToSet(SystemParam.NAMESPACE, tableName, key, set);
                            }
                        }
                    } catch (Exception ex) {
                        System.out.println("loi: " + file);
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
}
