package com.equipamento.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter // Cria os getter e setters de maneira automatica
@Setter
@NoArgsConstructor
@Entity
@Table(name = "equipamentos")
public class Equipamento extends NovoEquipamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //ID do equipamento

    @Enumerated(EnumType.STRING) // Diz ao JPA para salvar o nome do status (ex: "DISPONIVEL") em vez de um número
    private StatusEquipamento status;


    public Equipamento(NovoEquipamento nv)
    {
        super(nv.getTipo(), nv.getMarca(), nv.getModelo(), nv.getTamanho());
        this.status = StatusEquipamento.DISPONIVEL;
    }

    

}
