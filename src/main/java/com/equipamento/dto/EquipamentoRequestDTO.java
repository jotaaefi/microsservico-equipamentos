package com.equipamento.dto;

import jakarta.validation.constraints.NotBlank;

public record EquipamentoRequestDTO(
    @NotBlank(message = "O campo 'tipo' não pode ser vazio.")
    String tipo,

    @NotBlank(message = "O campo 'marca' não pode ser vazio.")
    String marca,

    @NotBlank(message = "O campo 'modelo' não pode ser vazio.")
    String modelo,
    
    // O tamanho é opcional, então não precisa de @NotBlank.
    String tamanho)

{ }

//Tipo blank nao deixa os campos serem enviados vazios.

    
