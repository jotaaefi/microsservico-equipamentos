package com.equipamento.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column; 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor 
@Getter
@Setter
@Entity
@Table (name = "tranca")
public class Tranca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer numero; 
    private String localizacao; 
    private String anoDeFabricacao; 
    private String modelo;
   
    @Enumerated(EnumType.STRING)
    private StatusTranca statusTranca;
   
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bicicleta_id")
    private Bicicleta bicicleta;

    
    
    // E colocamos apenas o ID no lugar
    @Column(name = "totem_id")
    private Integer totemId;
    
    // ... construtores ...
    public Tranca(Integer numero, String localizacao, String anoDeFabricacao, String modelo, StatusTranca statusTranca) {
        this.numero = numero;
        this.localizacao = localizacao;
        this.anoDeFabricacao = anoDeFabricacao;
        this.modelo = modelo;
        this.statusTranca = statusTranca;
    }
    
    public Tranca(Integer numero, String localizacao, String anoDeFabricacao, String modelo, StatusTranca statusTranca, Bicicleta bicicleta) {
        this(numero, localizacao, anoDeFabricacao, modelo, statusTranca);
        this.bicicleta = bicicleta;
    }
}