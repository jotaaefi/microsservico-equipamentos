package com.equipamento.dto;

import com.equipamento.entity.FuncaoFuncionario;

import jakarta.validation.constraints.NotNull;


public record FuncionarioRespostaDTO( @NotNull Integer id,
    @NotNull String matricula, // Matrícula gerada pelo sistema (R2 UC15)
    @NotNull String nome,
    @NotNull Integer idade,
    @NotNull FuncaoFuncionario funcao,
    @NotNull String cpf,
    @NotNull String email
 
)
{}
