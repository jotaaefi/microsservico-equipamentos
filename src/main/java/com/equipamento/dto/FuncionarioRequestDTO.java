package com.equipamento.dto;

import com.equipamento.entity.FuncaoFuncionario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FuncionarioRequestDTO(@NotBlank(message = "O nome é obrigatório.")
    String nome,

    @NotNull(message = "A idade é obrigatória.")
    Integer idade,

    @NotNull(message = "A função é obrigatória.")
    FuncaoFuncionario funcao, // Usamos o enum para garantir valores válidos (R3 UC15)

    @NotBlank(message = "O CPF é obrigatório.")
    @Size(min = 11, max = 11, message = "O CPF deve ter 11 dígitos.") // Assumindo CPF com 11 dígitos
    String cpf,

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Formato de e-mail inválido.") // Validação de formato de email
    String email,
   
    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.") // Exemplo de validação de tamanho para senha
    String senha) {
    
}
