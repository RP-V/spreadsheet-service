package com.unipampa.spreadsheetservice.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class Amostra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long amostra;
  private Date data;
  private Long numero;
  private Boolean lvc;
  private Boolean morreu;

  @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL)
  private Set<AmostraSintoma> amostraSintoma;

  @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL)
  private Set<AmostraAcao> amostraAcao;

  @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL)
  private Set<AmostraExame> amostraExame;

}