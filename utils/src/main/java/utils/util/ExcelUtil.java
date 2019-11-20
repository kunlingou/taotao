 package utils.util;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
    
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        File file = new File("D:/test.xlsx");
        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file);
            XSSFSheet sheetAt = xssfWorkbook.getSheetAt(0);
            System.out.println(sheetAt);
        } catch (InvalidFormatException e) {
             e.printStackTrace();
        } catch (IOException e) {
             e.printStackTrace();
        }
    }
}

 
// List<List<Object>> list = new LinkedList<>();
// Workbook workbook = null;
// try {
//     workbook = WorkbookFactory.create(inputStream);
//     int sheetsNumber = workbook.getNumberOfSheets();
//     for (int n = 0; n < sheetsNumber; n++) {
//         Sheet sheet = workbook.getSheetAt(n);
//         Object value = null;
//         Row row = null;
//         Cell cell = null;
//         for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getPhysicalNumberOfRows(); i++) { // 从第二行开始读取
//             row = sheet.getRow(i);
//             if (StringUtils.isEmpty(row)) {
//                 continue;
//             }
//             List<Object> linked = new LinkedList<>();
//             for (int j = row.getFirstCellNum(); j <= row.getLastCellNum(); j++) {
//                 cell = row.getCell(j);
//                 if (StringUtils.isEmpty(cell)) {
//                     continue;
//                 }
//                 value = getCellValue(cell);
//                 linked.add(value);
//             }
//             list.add(linked);
//         }
//     }
// } catch (Exception e) {
//     e.printStackTrace();
// } finally {
//     IOUtils.closeQuietly(workbook);
//     IOUtils.closeQuietly(inputStream);
// }
// return list;