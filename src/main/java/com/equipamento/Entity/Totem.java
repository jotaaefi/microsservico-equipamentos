package com.equipamento.Entity;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn; 
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

    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "totem_id") // Indica que a tabela 'tranca' tem uma coluna 'totem_id'
    private List<Tranca> trancasNaRede = new ArrayList<>();
   
    
    public void addTranca(Tranca tranca) {
        trancasNaRede.add(tranca);
        tranca.setTotemId(this.id);
    }

    public void removeTranca(Tranca tranca) {
        trancasNaRede.removeIf(t -> t.getId().equals(tranca.getId()));
        tranca.setTotemId(null);
    }

    public Totem(String localizacao, String descricao) {
        this.localizacao = localizacao;
        this.descricao = descricao;
    }
}