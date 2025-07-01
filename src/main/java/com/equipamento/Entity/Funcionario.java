package com.equipamento.Entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor 
@Entity
@Table(name = "funcionarios") 
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; 

    @Column(unique = true, nullable = false) // Matrícula é única e obrigatória
    private String matricula; // Gerada automaticamente pelo sistema (R2 UC15)

    private String nome;
    private Integer idade;

    @Enumerated(EnumType.STRING) // Salva o nome do enum no banco (ex: "REPARADOR")
    private FuncaoFuncionario funcao; // Conforme R3 UC15

    @Column(unique = true, nullable = false) // CPF é único e obrigatório
    private String cpf;

    @Column(unique = true, nullable = false) // Email é único e obrigatório
    private String email;

    @Column(nullable = false)
    private String senha; 

   public Funcionario(String nome, Integer idade, FuncaoFuncionario funcao, String cpf, String email, String senha) {
        this.nome = nome;
        this.idade = idade;
        this.funcao = funcao;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
    }
}