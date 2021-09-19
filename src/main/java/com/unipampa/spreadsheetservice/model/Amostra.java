package com.unipampa.spreadsheetservice.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(value = {"id"})
@Data
@Entity
public class Amostra {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Double amostra;
  private String data;
  private Double numero;
  private Boolean lvc;
  private Boolean morreu;

  @ManyToOne
  private Cao cao;

  @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL)
  private Set<AmostraSintoma> amostraSintoma;

  @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL)
  private Set<AmostraAcao> amostraAcao;

  @OneToMany(mappedBy = "amostra", cascade = CascadeType.ALL)
  private Set<AmostraExame> amostraExame;

}
