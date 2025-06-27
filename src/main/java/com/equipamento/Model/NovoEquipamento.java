package com.equipamento.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@MappedSuperclass

public class NovoEquipamento {
    
    private String tipo; // Ex: "Capacete", "Luva de Ciclismo", "Trava de Segurança"
    private String marca;
    private String modelo;
    private String tamanho; // Ex: "P", "M", "G" ou "Ajustável"

    // Construtor para facilitar a criação
    public NovoEquipamento(String tipo, String marca, String modelo, String tamanho) {
        this.tipo = tipo;
        this.marca = marca;
        this.modelo = modelo;
        this.tamanho = tamanho;
    }
}
