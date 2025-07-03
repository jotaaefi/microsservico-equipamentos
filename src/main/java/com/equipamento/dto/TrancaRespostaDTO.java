package com.equipamento.dto;

import com.equipamento.entity.StatusTranca;

import jakarta.validation.constraints.NotNull;

public record TrancaRespostaDTO(@NotNull Integer id,
    @NotNull Integer numero,
    @NotNull String localizacao,
    @NotNull String anoDeFabricacao, // Ano de fabricação
    @NotNull String modelo,
    @NotNull StatusTranca statusTranca, // O status atual da tranca
    BicicletaRespostaDTO bicicleta)

{}
