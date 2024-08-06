package com.unipampa.spreadsheetservice.dto;

import com.unipampa.spreadsheetservice.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JsonDTO {

    private Long amostra;
    //    private Date data;
    private Long numero;
    private Boolean lvc;
    private Boolean morreu;
    private Cao cao;
    private Proprietario proprietario;
    private List<Localizacao> localizacoes;
    private List<Sintoma> sintomas;
    private List<Exame> exames;
    private Acao acao;

}
