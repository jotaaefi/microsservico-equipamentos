package com.equipamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TrancaRequestDTO( @NotNull(message = "O número da tranca é obrigatório.") // Alterado para @NotNull
    Integer numero,

    @NotBlank(message = "A localização não pode ser vazia.")
    String localizacao,

    @NotBlank(message = "O ano de fabricação não pode ser vazio.")
    @Size(min = 4, max = 4, message = "O ano de fabricação deve ter 4 dígitos.") // Adicionando validação de tamanho
    String anoDeFabricacao,

    @NotBlank(message = "O modelo não pode ser vazio.")
    String modelo
    // O ID e o Status não são incluídos aqui, pois são gerenciados pelo sistema
) 
{}
