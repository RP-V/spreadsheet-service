package com.unipampa.spreadsheetservice.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(value = {"id", "amostra"})
@Data
@Entity
public class Cao {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String nome;
  private String raca;
  private String sexo;
  private Double idade;
  private Boolean vacina;
  private Boolean usaColeira;

  @OneToMany(mappedBy = "cao", cascade = CascadeType.ALL)
  private Set<Amostra> amostra;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "proprietario_id")
  private Proprietario proprietario;
}
