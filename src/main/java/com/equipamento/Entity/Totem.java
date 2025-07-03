package com.equipamento.Entity;

import java.util.ArrayList;
import java.util.List;

// Import do @JsonManagedReference foi removido
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn; // Adicionado
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

    // --- ALTERAÇÃO PRINCIPAL ---
    // Removemos o 'mappedBy' e adicionamos @JoinColumn
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "totem_id") // Indica que a tabela 'tranca' tem uma coluna 'totem_id'
    private List<Tranca> trancasNaRede = new ArrayList<>();
   
    // Os métodos addTranca e removeTranca ainda podem funcionar, mas a lógica de serviço
    // para associar/desassociar precisará ser ajustada.
    // Para manter a consistência, seria melhor remover estes métodos da entidade
    // e gerenciar a lista de trancas diretamente no serviço. Por ora, vou mantê-los.
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