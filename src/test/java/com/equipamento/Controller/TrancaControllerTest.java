package com.equipamento.Controller;

import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Tranca;
import com.equipamento.Service.TrancaService;
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import com.equipamento.mapper.TrancaMapper;
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


import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = TrabalhoEs2Application.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
@AutoConfigureMockMvc
class TrancaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrancaService trancaService;

    @MockBean
    private TrancaMapper trancaMapper;

    @Test
    void listarTrancas_deveRetornarListaVazia_quandoNaoHaTrancas() throws Exception {
        // Arrange
        when(trancaService.listarTrancas()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/tranca")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
    
  
    @Test
    void criarTranca_deveRetornarCreated_quandoDadosValidos() throws Exception {
        // Arrange
        TrancaRequestDTO requestDTO = new TrancaRequestDTO(101, "Hall de Entrada", "2024", "T-Advanced");
        
        Tranca trancaCriada = new Tranca(101, "Hall de Entrada", "2024", "T-Advanced", StatusTranca.NOVA);
        trancaCriada.setId(1);

        TrancaRespostaDTO respostaDTO = new TrancaRespostaDTO(1, 101, "Hall de Entrada", "2024", "T-Advanced", StatusTranca.NOVA, null);

        when(trancaService.criarTranca(any(TrancaRequestDTO.class))).thenReturn(trancaCriada);
        when(trancaMapper.toResponseDTO(trancaCriada)).thenReturn(respostaDTO);
        
        // Act & Assert
        mockMvc.perform(post("/tranca")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.numero").value(101))
                .andExpect(jsonPath("$.statusTranca").value("NOVA"));
    }


    @Test
    void buscarTrancaPorId_deveRetornarNotFound_quandoIdNaoExiste() throws Exception {
        // Arrange
        Integer idNaoExistente = 99;
        when(trancaService.buscarTrancaPorId(idNaoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tranca/{id}", idNaoExistente))
                .andExpect(status().isNotFound());
    }
    
    
    @Test
    void atualizarStatusTranca_deveRetornarOk_quandoAcaoValida() throws Exception {
        // Arrange
        Integer idTranca = 1;
        String acao = "LIVRE";
        StatusTranca novoStatus = StatusTranca.LIVRE;

        Tranca trancaAtualizada = new Tranca(101, "Hall de Entrada", "2024", "T-Advanced", novoStatus);
        trancaAtualizada.setId(idTranca);

        TrancaRespostaDTO respostaDTO = new TrancaRespostaDTO(idTranca, 101, "Hall de Entrada", "2024", "T-Advanced", novoStatus, null);

        when(trancaService.atualizarStatusTranca(idTranca, novoStatus)).thenReturn(Optional.of(trancaAtualizada));
        when(trancaMapper.toResponseDTO(trancaAtualizada)).thenReturn(respostaDTO);

        // Act & Assert
        mockMvc.perform(post("/tranca/{idTranca}/status/{acao}", idTranca, acao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTranca").value("LIVRE"));
    }

 
    @Test
    void atualizarStatusTranca_deveRetornarBadRequest_quandoAcaoInvalida() throws Exception {
        // Arrange
        Integer idTranca = 1;
        String acaoInvalida = "QUEBRADA"; // Valor que não existe no enum StatusTranca

        // Não precisamos de mocks para o service, pois o controller deve rejeitar a ação antes.
        
        // Act & Assert
        mockMvc.perform(post("/tranca/{idTranca}/status/{acao}", idTranca, acaoInvalida))
                .andExpect(status().isBadRequest());
    }
}