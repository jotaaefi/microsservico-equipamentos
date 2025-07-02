package com.equipamento.Controller;

import com.equipamento.Entity.FuncaoFuncionario;
import com.equipamento.Entity.Funcionario;
import com.equipamento.Service.FuncionarioService;
import com.equipamento.dto.FuncionarioRequestDTO;
import com.equipamento.dto.FuncionarioRespostaDTO;
import com.equipamento.mapper.FuncionarioMapper;
import com.equipamento.trabalhoES2.TrabalhoEs2Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Configuração de teste final e funcional para o seu projeto
@SpringBootTest(classes = TrabalhoEs2Application.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class FuncionarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FuncionarioService funcionarioService;

    @MockBean
    private FuncionarioMapper funcionarioMapper;

    // --- Teste para GET /funcionario ---
    @Test
    void listarFuncionarios_deveRetornarListaDeFuncionarios() throws Exception {
        // Arrange (Organizando o cenário com os campos corretos)
        Funcionario func1 = new Funcionario("Maria Silva", 30, FuncaoFuncionario.REPARADOR, "11122233344", "maria@email.com", "senha123");
        func1.setId(1); // O ID é gerado, então setamos manualmente para o teste
        func1.setMatricula("MAT001"); // A matrícula também é gerada

        Funcionario func2 = new Funcionario("João Souza", 25, FuncaoFuncionario.ADMINISTRATIVO, "55566677788", "joao@email.com", "senha456");
        func2.setId(2);
        func2.setMatricula("MAT002");

        FuncionarioRespostaDTO dto1 = new FuncionarioRespostaDTO(1, "MAT001", "Maria Silva", 30, FuncaoFuncionario.REPARADOR, "11122233344", "maria@email.com");
        FuncionarioRespostaDTO dto2 = new FuncionarioRespostaDTO(2, "MAT002", "João Souza", 25, FuncaoFuncionario.ADMINISTRATIVO, "55566677788", "joao@email.com");

        when(funcionarioService.listarFuncionarios()).thenReturn(Arrays.asList(func1, func2));
        when(funcionarioMapper.toResponseDTO(func1)).thenReturn(dto1);
        when(funcionarioMapper.toResponseDTO(func2)).thenReturn(dto2);

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(get("/funcionario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Maria Silva"))
                .andExpect(jsonPath("$[1].email").value("joao@email.com"));
    }

    // --- Testes para GET /funcionario/{id} ---
    @Test
    void buscarFuncionarioPorId_deveRetornarFuncionario_quandoEncontrado() throws Exception {
        // Arrange
        Integer idFuncionario = 1;
        Funcionario func = new Funcionario("Carlos Pereira", 40, FuncaoFuncionario.ADMINISTRATIVO, "77788899900", "carlos@email.com", "senha789");
        func.setId(idFuncionario);
        func.setMatricula("MAT003");

        FuncionarioRespostaDTO dto = new FuncionarioRespostaDTO(idFuncionario, "MAT003", "Carlos Pereira", 40, FuncaoFuncionario.ADMINISTRATIVO, "77788899900", "carlos@email.com");

        when(funcionarioService.buscarFuncionarioPorId(idFuncionario)).thenReturn(Optional.of(func));
        when(funcionarioMapper.toResponseDTO(func)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/funcionario/{id}", idFuncionario))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(idFuncionario))
                .andExpect(jsonPath("$.nome").value("Carlos Pereira"));
    }

    @Test
    void buscarFuncionarioPorId_deveRetornarNotFound_quandoNaoEncontrado() throws Exception {
        // Arrange
        Integer idFuncionario = 99;
        when(funcionarioService.buscarFuncionarioPorId(idFuncionario)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/funcionario/{id}", idFuncionario))
                .andExpect(status().isNotFound());
    }

    // --- Testes para POST /funcionario ---
    @Test
    void criarFuncionario_deveRetornarCreated_quandoValido() throws Exception {
        // Arrange
        FuncionarioRequestDTO requestDto = new FuncionarioRequestDTO("Ana Costa", 28, FuncaoFuncionario.REPARADOR, "12345678901", "ana@email.com", "senhaSuperSegura");
        
        Funcionario funcionarioCriado = new Funcionario("Ana Costa", 28, FuncaoFuncionario.REPARADOR, "12345678901", "ana@email.com", "senhaSuperSegura");
        funcionarioCriado.setId(3);
        funcionarioCriado.setMatricula("MAT004");
        
        FuncionarioRespostaDTO respostaDto = new FuncionarioRespostaDTO(3, "MAT004", "Ana Costa", 28, FuncaoFuncionario.REPARADOR, "12345678901", "ana@email.com");

        when(funcionarioService.criarFuncionario(any(FuncionarioRequestDTO.class))).thenReturn(funcionarioCriado);
        when(funcionarioMapper.toResponseDTO(funcionarioCriado)).thenReturn(respostaDto);

        // Act & Assert
        mockMvc.perform(post("/funcionario")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nome").value("Ana Costa"))
                .andExpect(jsonPath("$.matricula").value("MAT004"));
    }
}