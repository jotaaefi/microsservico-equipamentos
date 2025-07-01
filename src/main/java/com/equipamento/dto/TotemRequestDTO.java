package com.equipamento.dto;

import jakarta.validation.constraints.NotBlank;


public record TotemRequestDTO(@NotBlank (message = "A localização não pode ser vazia.") String localizacao, 
@NotBlank (message = "A descrição não pode ser vazia.") String descricao) {
    
}
