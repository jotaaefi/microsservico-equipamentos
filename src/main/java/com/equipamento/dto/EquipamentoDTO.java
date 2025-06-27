package com.equipamento.dto; 

import com.equipamento.Model.StatusEquipamento;


public record EquipamentoDTO(Long id, String tipo,
    String marca,
    String modelo,
    String tamanho,
    StatusEquipamento status
) {
    // O corpo do record geralmente fica vazio para DTOs simples como este.
    // É aqui que você poderia adicionar construtores customizados ou métodos, se precisasse.
}