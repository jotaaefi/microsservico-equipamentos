package com.equipamento.dto;



import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Size;

public record BicicletaRequestDTO(
    @NotBlank(message = "A marca não pode ser vazia") // R2 UC10: todos os dados são obrigatórios
    String marca,
    @NotBlank(message = "O modelo não pode ser vazio") // R2 UC10
    String modelo,
    @NotBlank(message = "O ano não pode ser vazio") // R2 UC10
    @Size(min = 4, max = 4, message = "O ano deve ter 4 dígitos")
    String ano
    // O número e status não vêm no RequestDTO de criação/edição inicial,
    // pois o número é gerado (R5 UC10) e o status inicial é 'NOVA' (R1 UC10).
    // O status só é alterado por endpoints específicos (integrar/retirar/atualizar status).
) {}