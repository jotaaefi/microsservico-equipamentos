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

    @OneToMany(mappedBy = "totem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tranca> trancasNaRede = new ArrayList<>(); // Conforme UC14 (Opção para listar trancas)


   
    public void addTranca(Tranca tranca) {
        trancasNaRede.add(tranca);
        tranca.setTotem(this); // Configura o lado ManyToOne na Tranca
    }

    public void removeTranca(Tranca tranca) {
        trancasNaRede.remove(tranca);
        tranca.setTotem(null); // Remove a associação no lado ManyToOne
    }

    public Totem(String localizacao, String descricao) {
        this.localizacao = localizacao;
        this.descricao = descricao;
    }
}
       