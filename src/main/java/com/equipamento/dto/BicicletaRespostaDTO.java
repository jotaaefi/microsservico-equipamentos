package com.equipamento.dto;

import com.equipamento.Entity.StatusBicicleta;

import jakarta.validation.constraints.NotNull;

public record BicicletaRespostaDTO(
    @NotNull Integer id,
    @NotNull String marca,
    @NotNull String modelo,
    @NotNull String ano,
    @NotNull Integer numero,
    @NotNull StatusBicicleta status
) {
        
}