package com.unipampa.spreadsheetservice.model;

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
public class Proprietario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nome;
  @OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL)
  private Set<Localizacao> localizacao;
  @OneToMany(mappedBy = "proprietario", cascade = CascadeType.ALL)
  private Set<Cao> cao;
}
