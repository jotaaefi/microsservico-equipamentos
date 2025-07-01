package com.equipamento.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RetirarBicicletaDTO(@NotNull(message = "O ID da bicicleta é obrigatório.")
    Integer idBicicleta,
    @NotNull(message = "O ID da tranca é obrigatório.")
    Integer idTranca,
    @NotBlank(message = "O ID do funcionário é obrigatório.") // UC09: Atores: Reparador (primário)
    String idFuncionario,
    @NotBlank(message = "O status da ação do reparador é obrigatório.") // UC09: Opções: Reparo ou Aposentadoria
    String statusAcaoReparador) {
    
}
