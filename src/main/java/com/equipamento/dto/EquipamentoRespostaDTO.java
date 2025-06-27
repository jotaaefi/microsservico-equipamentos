package com.equipamento.dto;

import com.equipamento.Model.StatusEquipamento;

//Record usado para salvar as respostas
public record EquipamentoRespostaDTO(
    Long id,
    String tipo,
    String marca,
    String modelo,
    String tamanho,
    StatusEquipamento status)
    {
}
