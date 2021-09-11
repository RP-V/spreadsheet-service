package com.unipampa.spreadsheetservice.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import com.unipampa.spreadsheetservice.model.Amostra;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

  @PostMapping("/new")
  public ResponseEntity<?> handleUpload(@RequestParam("file") MultipartFile file)
      throws IOException {
    File javaFile = File.createTempFile("spreadsheet-", "");
    file.transferTo(javaFile);

    ArrayList<Amostra> amostras = new ArrayList<Amostra>();
    HashMap<String, ArrayList<String>> errors = new HashMap<String, ArrayList<String>>();
    ArrayList<String> unprocessableSheets = new ArrayList<String>();

    Workbook wb = WorkbookFactory.create(javaFile);
    Iterator<Sheet> sheets = wb.sheetIterator();

    while (sheets.hasNext()) {
      Sheet s = sheets.next();
      // primeira row começa com números
      if (CellUtil.getCell(CellUtil.getRow(0, s), 0).getCellType() == CellType.NUMERIC) {
        unprocessableSheets.add(s.getSheetName());
        continue;
      }
      Row headerRow = s.getRow(s.getTopRow());
      for (Row row : s) {
        if (row.getRowNum() == 0)
          continue;
        if (row.getCell(0) == null)
          continue;
        else if (row.getCell(0).toString() == "")
          continue;
        if (row.getCell(1) == null)
          continue;
        else if (row.getCell(1).toString() == "")
          continue;
        Amostra a = new Amostra();
        for (Cell cell : row) {
          String h = parse(headerRow.getCell(cell.getColumnIndex()).toString());
          String str = parse(cell.toString());
          if (str != "") {
            switch (h) {
              case "amostra":
                a.setAmostra(Double.parseDouble(str));
                break;
              case "data":
                try {
                  a.setData(cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault())
                      .toLocalDate());
                } catch (Exception e) {
                  a.setData(LocalDate.parse(cell.getStringCellValue(),
                      DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                }
                break;
              case "n":
                a.setNumero(Double.parseDouble(parse(cell.toString())));
                break;
              case "lvc":
              case "lv":
                a.setLvc(cell.getNumericCellValue() == 1);
                break;
              case "morreu":
              case "morte":
                a.setMorreu(cell.getNumericCellValue() == 1);
                break;
              default:
                break;
            }
          }
        }
        amostras.add(a);
      }
    }
    if (!unprocessableSheets.isEmpty()) {
      errors.put("Não foi possível processar a página", unprocessableSheets);
    }

    return new ResponseEntity<>(Arrays.asList(amostras, unprocessableSheets), HttpStatus.OK);
  }

  public String parse(String s) {
    return s.toLowerCase().replaceAll("[ãáàâ]", "a").replaceAll("[éê]", "e").replaceAll("[úü]", "u")
        .replaceAll("[ç]", "c").replaceAll("[í]", "i").replaceAll("[õóô]", "o")
        .replaceAll("[ñ]", "n").replaceAll(" ", "_").replaceAll("\\*", "");
  }
}
