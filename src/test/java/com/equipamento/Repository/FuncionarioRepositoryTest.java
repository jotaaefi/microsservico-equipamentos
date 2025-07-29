package com.equipamento.Repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import com.equipamento.Entity.FuncaoFuncionario;
import com.equipamento.Entity.Funcionario;
import com.equipamento.trabalhoES2.TrabalhoEs2Application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration; 


//So funcionava quando usava o banco H2 local, dps que usou o online comecou a quebrar.

/* 
@DataJpaTest
@ContextConfiguration(classes = TrabalhoEs2Application.class)
class FuncionarioRepositoryTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Test
    void deveSalvarEEncontrarFuncionarioPorEmail() {
        // Arrange
        Funcionario f = new Funcionario(
            "Carlos Oliveira",
            35,
            FuncaoFuncionario.REPARADOR,
            "12345678900",
            "carlos@empresa.com",
            "senhaSegura123"
        );
        f.setMatricula("A123");

        // Act
        funcionarioRepository.save(f);
        Optional<Funcionario> encontrado = funcionarioRepository.findByEmail("carlos@empresa.com");

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Carlos Oliveira", encontrado.get().getNome());
    }

    @Test
    void deveEncontrarPorCpf() {
        // Arrange
        Funcionario f = new Funcionario(
            "Ana Silva",
            28,
            FuncaoFuncionario.ADMINISTRATIVO,
            "98765432100",
            "ana@empresa.com",
            "outraSenha456"
        );
        f.setMatricula("B456");

        // Act
        funcionarioRepository.save(f);
        Optional<Funcionario> encontrado = funcionarioRepository.findByCpf("98765432100");

        // Assert
        assertTrue(encontrado.isPresent());
        assertEquals("Ana Silva", encontrado.get().getNome());
    }

    @Test
    void deveRetornarVazioSeMatriculaNaoExistir() {
        // Act
        Optional<Funcionario> resultado = funcionarioRepository.findByMatricula("X999");

        // Assert
        assertFalse(resultado.isPresent());
    }*/


