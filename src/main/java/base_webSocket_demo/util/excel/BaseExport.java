package base_webSocket_demo.util.excel;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class BaseExport<T> {

    private final XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final List<T> listData;

    public BaseExport(List<T> listData) {
        this.listData = listData;
        this.workbook = new XSSFWorkbook();
    }

    public BaseExport<T> writeHeaderLine(String[] headers) {
        sheet = workbook.createSheet("Data export");

        Row row = sheet.createRow(0);

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(13);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            createCell(row, i, headers[i], style);
        }

        return this;
    }

    public BaseExport<T> writeDataLines(String[] fields, Class<T> clazz) {
        int rowCount = 1;

        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(12);
        font.setFontName("Times New Roman");
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        for (T data : this.listData) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            for (String fieldName : fields) {
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Object value = field.get(data);
                    createCell(row, columnCount++, value, style);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    log.error("Export field error: {}", fieldName, e);
                    createCell(row, columnCount++, "ERROR", style);
                }
            }
        }

        return this;
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style) {
        sheet.autoSizeColumn(columnCount);
        Cell cell = row.createCell(columnCount);

        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Double) {
            cell.setCellValue((Double) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            cell.setCellValue(sdf.format((Date) value));
        } else {
            cell.setCellValue(value != null ? value.toString() : "");
        }

        cell.setCellStyle(style);
    }

    public void export(HttpServletResponse response, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        }

        workbook.close();
    }

    public void exportBase64(HttpServletResponse response) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            workbook.close();

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String base64EncodedString = Base64.getEncoder().encodeToString(byteArray);

            response.setContentType("text/plain");
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                outputStream.write(base64EncodedString.getBytes());
            }
        }
    }
}
