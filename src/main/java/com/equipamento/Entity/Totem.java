package com.equipamento.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference; 

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor 
@Getter
@Setter
@Entity
@Table (name = "totens")
public class Totem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    private String localizacao;
    private String descricao;   

    @JsonManagedReference 
    @OneToMany(mappedBy = "totem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tranca> trancasNaRede = new ArrayList<>();


   
    public void addTranca(Tranca tranca) {
        trancasNaRede.add(tranca);
        tranca.setTotem(this);
    }

    public void removeTranca(Tranca tranca) {
        trancasNaRede.remove(tranca);
        tranca.setTotem(null);
    }

    public Totem(String localizacao, String descricao) {
        this.localizacao = localizacao;
        this.descricao = descricao;
    }
}