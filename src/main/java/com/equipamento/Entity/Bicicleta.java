package com.equipamento.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bicicleta")
public class Bicicleta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Gerado automaticamente pelo sistema (conforme R5 UC10)

    private String marca;
    private String modelo;
    private String ano;
    private Integer numero; // Número gerado ou atribuído (conforme R5 UC10)


    @Enumerated(EnumType.STRING) // Salva o nome do enum no banco (ex: "NOVA")
    private StatusBicicleta status;


}
