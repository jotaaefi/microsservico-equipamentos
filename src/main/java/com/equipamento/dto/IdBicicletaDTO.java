package com.equipamento.dto;

import jakarta.validation.constraints.NotNull;

public record IdBicicletaDTO(
    @NotNull(message = "ID da bicicleta é obrigatório.")
    Integer idBicicleta
) {}

