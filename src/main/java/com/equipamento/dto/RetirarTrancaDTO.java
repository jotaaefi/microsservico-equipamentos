package com.equipamento.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RetirarTrancaDTO (@NotNull(message = "O ID do totem é obrigatório.")
    Integer idTotem,
    @NotNull(message = "O ID da tranca é obrigatório.")
    Integer idTranca,
    @NotBlank(message = "O ID do funcionário é obrigatório.") // Ator: Reparador (UC12)
    String idFuncionario,
    @NotBlank(message = "A ação do reparador é obrigatória (REPARO ou APOSENTAR).") // Opções de ação no UC12
    String statusAcaoReparador) {
    
}
