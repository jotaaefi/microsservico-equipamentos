package com.equipamento.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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

    /*Numero da tranca, localizacao, ano de fabricacao e modelo */
    private Integer numero; 
    private String localizacao; 
    private String anoDeFabricacao; 
    private String modelo;
   
    
    /*Status da tranca */
    @Enumerated(EnumType.STRING)
    private StatusTranca statusTranca;

   
   
    /*Uma tranca so pode estar associada a uma bike */
    @OneToOne(fetch = FetchType.LAZY) // Carrega a bicicleta apenas quando necessário
    @JoinColumn(name = "bicicleta_id") // Nome da coluna de chave estrangeira na tabela 'trancas'
    private Bicicleta bicicleta;


    @ManyToOne(fetch = FetchType.LAZY) // Carrega o totem apenas quando necessário
    @JoinColumn(name = "totem_id") // Nome da coluna de chave estrangeira na tabela 'trancas'
    private Totem totem;

     // Construtor com atributos básicos (sem bicicleta e totem)
    public Tranca(Integer numero, String localizacao, String anoDeFabricacao, String modelo, StatusTranca statusTranca) {
        this.numero = numero;
        this.localizacao = localizacao;
        this.anoDeFabricacao = anoDeFabricacao;
        this.modelo = modelo;
        this.statusTranca = statusTranca;
    }
    
    
    
    public Tranca(Integer numero, String localizacao, String anoDeFabricacao, String modelo, StatusTranca statusTranca, Bicicleta bicicleta) {
        this(numero, localizacao, anoDeFabricacao, modelo, statusTranca); // Chama o construtor acima
        this.bicicleta = bicicleta;
    }

    // Construtor com atributos básicos, bicicleta e totem (se precisar de inicialização completa)
    public Tranca(Integer numero, String localizacao, String anoDeFabricacao, String modelo, StatusTranca statusTranca, Bicicleta bicicleta, Totem totem) {
        this(numero, localizacao, anoDeFabricacao, modelo, statusTranca, bicicleta); // Chama o construtor acima
        this.totem = totem;
    }
    

  
}
