package com.unipampa.spreadsheetservice.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(value = {"id", "amostraSintoma"})
@Data
@Entity
public class Sintoma {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nome;

  @OneToMany(mappedBy = "sintoma", cascade = CascadeType.ALL)
  private Set<AmostraSintoma> amostraSintoma;
}
