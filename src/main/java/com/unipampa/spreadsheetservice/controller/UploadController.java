package com.unipampa.spreadsheetservice.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.unipampa.spreadsheetservice.model.Acao;
import com.unipampa.spreadsheetservice.model.Amostra;
import com.unipampa.spreadsheetservice.model.AmostraAcao;
import com.unipampa.spreadsheetservice.model.AmostraExame;
import com.unipampa.spreadsheetservice.model.AmostraSintoma;
import com.unipampa.spreadsheetservice.model.Cao;
import com.unipampa.spreadsheetservice.model.Exame;
import com.unipampa.spreadsheetservice.model.Localizacao;
import com.unipampa.spreadsheetservice.model.Proprietario;
import com.unipampa.spreadsheetservice.model.Sintoma;
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
    ArrayList<String> headers = new ArrayList<String>();
    HashMap<String, ArrayList<String>> errors = new HashMap<String, ArrayList<String>>();
    ArrayList<String> unprocessableSheets = new ArrayList<String>();

    HashMap<String, String> grausIntensidade = new HashMap<>();
    grausIntensidade.put("leve", "1");
    grausIntensidade.put("severo", "2");
    grausIntensidade.put("severa", "2");
    grausIntensidade.put("normocoradas", "0");
    grausIntensidade.put("hipocoradas", "2");
    grausIntensidade.put("hiperemicas", "2");
    for (int i = 0; i <= 20; i++) {
      grausIntensidade.put(i + ".0", i + "");
    }

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
        Cao cao = new Cao();
        Proprietario p = new Proprietario();
        Set<Localizacao> locSet = new HashSet<Localizacao>();
        Localizacao loc = new Localizacao();
        Set<AmostraSintoma> sintSet = new HashSet<AmostraSintoma>();
        Set<AmostraAcao> acaoSet = new HashSet<AmostraAcao>();
        Set<AmostraExame> exameSet = new HashSet<AmostraExame>();
        for (Cell cell : row) {
          AmostraSintoma amostra_sint = new AmostraSintoma();
          AmostraAcao amostra_acao = new AmostraAcao();
          AmostraExame amostra_exame = new AmostraExame();
          String h = parse(headerRow.getCell(cell.getColumnIndex()).toString());
          if (!headers.contains(h) && h != null) {
            headers.add(h);
          }
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
                  a.setData(parseDate(str));
                }
                break;
              case "n":
                a.setNumero(Double.parseDouble(str));
                break;
              case "lvc":
              case "lv":
                a.setLvc(str == "1");
                break;
              case "morreu":
              case "morte":
                a.setMorreu(str == "1");
                break;
              // cão
              case "nome":
              case "nome_animal":
                cao.setNome(str);
                break;
              case "raca":
                cao.setRaca(str);
                break;
              case "sexo":
                HashMap<String, String> sexo = new HashMap<String, String>();
                sexo.put("M", "M");
                sexo.put("1", "M");
                sexo.put("F", "F");
                sexo.put("2", "F");
                cao.setSexo(sexo.get(str));
                break;
              case "idade":
                try {
                  cao.setIdade(Double.parseDouble(str));
                } catch (Exception e) {
                  Double idade = Double.parseDouble(str.replaceAll("[\\D]", ""));
                  idade = idade >= 17.0 ? idade - 10 : idade;
                  cao.setIdade(idade);
                }
                break;
              case "vacina":
                cao.setVacina(str == "1");
                break;
              case "usa_coleira":
                cao.setUsaColeira(str == "1" || str == "sim");
                break;
              // proprietario
              case "proprietario":
                p.setNome(str);
                break;
              case "endereco":
                loc.setEndereco(str);
                break;
              case "complemento":
                loc.setComplemento(str);
                break;
              case "bairro":
                loc.setBairro(str);
                break;
              case "area":
                loc.setArea(str);
                break;
              case "lat":
                loc.setLatitude(Double.parseDouble(str));
                break;
              case "long":
                loc.setLongitude(Double.parseDouble(str));
                break;
              // acao tomada
              case "tratamento":
              case "eutanasia":
                Acao acao = new Acao();
                acao.setNome(h);
                amostra_acao.setAcao(acao);
                acaoSet.add(amostra_acao);
                break;
              case "data_eutanasia":
                amostra_acao.setData(str);
                break;
              case "obs":
                amostra_acao.setObs(str);
                break;
              // exames
              case "tr":
              case "elisa":
                Exame e = new Exame();
                e.setNome(h);
                amostra_exame.setExame(e);
                exameSet.add(amostra_exame);
                break;
              case "data_res._tr":
              case "data_resultado_elisa":
                amostra_exame.setData(str);
                break;
              // campos não modelados
              case "outros_animais":
              case "cod._bairro":
              case "estado_geral":
              case "estado":
              case "extravio":
              case "ifi":
              case "rifi":
              case "":
                break;
              default:
                // sintomas
                Sintoma sint = new Sintoma();
                sint.setNome(h);
                amostra_sint.setSintoma(sint);
                amostra_sint.setIntensidade(Double.parseDouble(grausIntensidade.get(str)));
                sintSet.add(amostra_sint);
                break;
            }
          }
        }
        a.setAmostraAcao(acaoSet);
        a.setAmostraExame(exameSet);
        a.setAmostraSintoma(sintSet);
        locSet.add(loc);
        p.setLocalizacao(locSet);
        cao.setProprietario(p);
        a.setCao(cao);
        amostras.add(a);
      }
    }
    if (!unprocessableSheets.isEmpty()) {
      errors.put("Não foi possível processar a página", unprocessableSheets);
    }

    return new ResponseEntity<>(Arrays.asList(amostras, headers, errors), HttpStatus.OK);
  }

  public String parse(String s) {
    return s.toLowerCase().replaceAll("[ãáàâ]", "a").replaceAll("[éê]", "e").replaceAll("[úü]", "u")
        .replaceAll("[ç]", "c").replaceAll("[í]", "i").replaceAll("[õóô]", "o")
        .replaceAll("[ñ]", "n").replaceAll(" ", "_").replaceAll("\\*", "");
  }

  public LocalDate parseDate(String s) {
    return LocalDate.parse(s, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
  }
}
