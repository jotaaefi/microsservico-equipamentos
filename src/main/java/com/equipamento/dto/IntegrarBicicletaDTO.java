package com.equipamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IntegrarBicicletaDTO(@NotNull(message = "O ID da bicicleta é obrigatório.")
    Integer idBicicleta,
    @NotNull(message = "O ID da tranca é obrigatório.")
    Integer idTranca,
    @NotBlank(message = "O ID do funcionário é obrigatório.") // Ator: Reparador (UC08)
    String idFuncionario) {
    
}
