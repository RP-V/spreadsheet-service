package com.unipampa.spreadsheetservice.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
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
import com.unipampa.spreadsheetservice.model.Cao;
import com.unipampa.spreadsheetservice.model.Exame;
import com.unipampa.spreadsheetservice.model.Localizacao;
import com.unipampa.spreadsheetservice.model.Proprietario;
import com.unipampa.spreadsheetservice.model.Sintoma;
import com.unipampa.spreadsheetservice.sender.AmostraSender;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
  private AmostraSender sender;
  @Autowired
  private RabbitTemplate rabbitTemplate;

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
    HashMap<String, String> sexo = new HashMap<>();
    sexo.put("M", "M");
    sexo.put("1.0", "M");
    sexo.put("F", "F");
    sexo.put("2.0", "F");

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
        //instanciação dos objetos ----------------------------
//        Amostra a = new Amostra();
//        Cao cao = new Cao();
//        Proprietario p = new Proprietario();
//        Set<Localizacao> locSet = new HashSet<Localizacao>();
//        Localizacao loc = new Localizacao();
//        Set<AmostraSintoma> sintSet = new HashSet<AmostraSintoma>();
//        Set<AmostraAcao> acaoSet = new HashSet<AmostraAcao>();
//        Set<AmostraExame> exameSet = new HashSet<AmostraExame>();
//        AmostraExame amostra_exame_tr = new AmostraExame();
//        AmostraExame amostra_exame_elisa = new AmostraExame();
        for (Cell cell : row) {
//          AmostraSintoma amostra_sint = new AmostraSintoma();
//          AmostraAcao amostra_acao = new AmostraAcao();
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
                a.setData(str);
                break;
              case "n":
                a.setNumero(Double.parseDouble(str));
                break;
              case "lvc":
              case "lv":
                Double lvc = Double.parseDouble(str);
                a.setLvc(lvc == 1);
                break;
              case "morreu":
              case "morte":
                Double morreu = Double.parseDouble(str);
                a.setMorreu(morreu == 1);
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
                Double vacina = Double.parseDouble(str);
                cao.setVacina(vacina >= 1);
                break;
              case "usa_coleira":
                try {
                  Double usaColeira = Double.parseDouble(str);
                  cao.setUsaColeira(usaColeira >= 1);
                } catch (Exception e) {
                  cao.setUsaColeira(str == "sim");
                }
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
                Double res_tr = Double.parseDouble(str);
                if (res_tr != null) {
                  Exame e = new Exame();
                  e.setNome(h);
                  amostra_exame_tr.setResultado(res_tr >= 1);
                  amostra_exame_tr.setExame(e);
                }
                break;
              case "data_res._tr":
                amostra_exame_tr.setData(str);
                break;
              case "elisa":
                Double res_elisa = Double.parseDouble(str);
                if (res_elisa != null) {
                  Exame e = new Exame();
                  e.setNome(h);
                  amostra_exame_elisa.setResultado(res_elisa >= 1);
                  amostra_exame_elisa.setExame(e);
                }
                break;
              case "data_resultado_elisa":
                amostra_exame_elisa.setData(str);
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
                Double intensidade = Double.parseDouble(grausIntensidade.get(str));
                if (intensidade != 0) {
                  Sintoma sint = new Sintoma();
                  sint.setNome(h);
                  amostra_sint.setSintoma(sint);
                  amostra_sint.setIntensidade(intensidade);
                  sintSet.add(amostra_sint);
                }
                break;
            }
          }
        }
        exameSet.add(amostra_exame_tr);
        exameSet.add(amostra_exame_elisa);
        if (cao.getUsaColeira() == null)
          cao.setUsaColeira(false);
        if (cao.getVacina() == null)
          cao.setVacina(false);
        if (a.getMorreu() == null)
          a.setMorreu(false);

        a.setAmostraAcao(acaoSet);
        a.setAmostraExame(exameSet);
        a.setAmostraSintoma(sintSet);
        locSet.add(loc);
        p.setLocalizacao(locSet);
        cao.setProprietario(p);
        a.setCao(cao);
        amostras.add(a);
        sender.sendMessage(rabbitTemplate, a);
      }
    }
    if (!unprocessableSheets.isEmpty()) {
      errors.put("Não foi possível processar a página", unprocessableSheets);
    }
    return new ResponseEntity<>(Arrays.asList(amostras, headers, errors), HttpStatus.OK);
  }

  public String one(String s) {
    return s.replaceAll("[^1]", "");
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
