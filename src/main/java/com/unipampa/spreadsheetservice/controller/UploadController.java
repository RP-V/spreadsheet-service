package com.unipampa.spreadsheetservice.controller;

import java.io.File;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
  public ResponseEntity<?> handleUpload(@RequestParam("file") MultipartFile file) throws IOException {
    File javaFile = File.createTempFile("spreadsheet-", "");
    file.transferTo(javaFile);

    Workbook wb = WorkbookFactory.create(javaFile);

    return new ResponseEntity<>(wb.getAllNames(), HttpStatus.OK);
  }
}
