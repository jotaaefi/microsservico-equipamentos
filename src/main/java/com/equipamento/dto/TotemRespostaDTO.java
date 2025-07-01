package com.equipamento.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record TotemRespostaDTO( @NotNull Integer id,
    @NotNull String localizacao,
    @NotNull String descricao,
    // A lista de trancas associadas ao totem, representada por DTOs de resposta de tranca
    List<TrancaRespostaDTO> trancasNaRede) {
    
}
