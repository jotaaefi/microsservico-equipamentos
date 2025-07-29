package com.equipamento.Controller;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Tranca;
import com.equipamento.Service.TrancaService;
import com.equipamento.dto.BicicletaRespostaDTO;
import com.equipamento.dto.IdBicicletaDTO;
import com.equipamento.dto.IntegrarTrancaDTO;
import com.equipamento.dto.RetirarTrancaDTO;
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import com.equipamento.mapper.BicicletaMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


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

    @MockBean
    private BicicletaMapper bicicletaMapper;

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
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
        void removerTranca_deveRetornarBadRequest_quandoServicoRetornaFalse() throws Exception {
        // Arrange
        Integer idTranca = 1;
        
        // Mockamos o serviço para retornar 'false', simulando uma falha na remoção
        when(trancaService.removerTranca(idTranca)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/tranca/{id}", idTranca))
                .andExpect(status().isBadRequest()); // Esperamos o status 400 Bad Request
    }


   
    @Test
    void getBicicletaNaTranca_deveRetornarBicicleta_quandoEncontrada() throws Exception {
        // Arrange
        Integer idTranca = 1;
        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(100);
        BicicletaRespostaDTO bicicletaDTO = new BicicletaRespostaDTO(100, "Marca", "Modelo", "2023", 123, null);

        when(trancaService.getBicicletaDeTranca(idTranca)).thenReturn(Optional.of(bicicleta));
        when(bicicletaMapper.toResponseDTO(bicicleta)).thenReturn(bicicletaDTO);

        // Act & Assert
        mockMvc.perform(get("/tranca/{idTranca}/bicicleta", idTranca))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100));
    }

    @Test
    void getBicicletaNaTranca_deveRetornarNotFound_quandoTrancaNaoTemBicicleta() throws Exception {
        // Arrange
        Integer idTranca = 1;
        when(trancaService.getBicicletaDeTranca(idTranca)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/tranca/{idTranca}/bicicleta", idTranca))
                .andExpect(status().isNotFound());
    }

    @Test
    void trancarBicicleta_deveRetornarOk_quandoTrancamentoBemSucedido() throws Exception {
        // Arrange
        Integer idTranca = 1;
        IdBicicletaDTO requestDTO = new IdBicicletaDTO(100);
        Tranca trancaAtualizada = new Tranca(1, "Local", "2023", "Mod", StatusTranca.OCUPADA);
        TrancaRespostaDTO respostaDTO = new TrancaRespostaDTO(1, 1, "Local", "2023", "Mod", StatusTranca.OCUPADA, null);

        when(trancaService.trancar(eq(idTranca), any(IdBicicletaDTO.class))).thenReturn(Optional.of(trancaAtualizada));
        when(trancaMapper.toResponseDTO(trancaAtualizada)).thenReturn(respostaDTO);

        // Act & Assert
        mockMvc.perform(post("/tranca/{idTranca}/trancar", idTranca)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusTranca").value("OCUPADA"));
    }

    @Test
    void destrancarBicicleta_deveRetornarUnprocessableEntity_quandoRegraDeNegocioFalha() throws Exception {
        
        Integer idTranca = 1;
        String mensagemErro = "Tranca não está ocupada ou não contém uma bicicleta.";
        
        // Mockamos o serviço para lançar a exceção que o controller captura
        when(trancaService.destrancar(idTranca)).thenThrow(new IllegalStateException(mensagemErro));

        // Act & Assert
        mockMvc.perform(post("/tranca/{idTranca}/destrancar", idTranca))
                .andExpect(status().isUnprocessableEntity()) // Esperamos o status 422
                .andExpect(jsonPath("$.message").value(mensagemErro)); // <-- CORREÇÃO AQUI
    }


   
    @Test
    void trancarBicicleta_deveRetornarUnprocessableEntity_quandoRegraDeNegocioFalha() throws Exception {
        // Arrange
        Integer idTranca = 1;
        IdBicicletaDTO requestDTO = new IdBicicletaDTO(100);
        String mensagemErro = "Tranca não está livre para receber uma bicicleta.";

        // Mockamos o serviço para lançar a exceção que o controller captura
        when(trancaService.trancar(eq(idTranca), any(IdBicicletaDTO.class)))
            .thenThrow(new IllegalStateException(mensagemErro));

        // Act & Assert
        mockMvc.perform(post("/tranca/{idTranca}/trancar", idTranca)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isUnprocessableEntity()) // Esperamos o status 422
                .andExpect(jsonPath("$.message").value(mensagemErro)); // Verificamos a mensagem no corpo do ErroDTO
    }


    // --- Teste GET /tranca ---
    @Test
    void listarTrancas_deveRetornarOkComListaVazia() throws Exception {
        when(trancaService.listarTrancas()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/tranca")).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));
    }

    // --- Teste POST /tranca ---
    @Test
    void criarTranca_deveRetornarCreated() throws Exception {
        TrancaRequestDTO requestDTO = new TrancaRequestDTO(101, "Hall de Entrada", "2024", "T-Advanced");
        Tranca trancaCriada = new Tranca(101, "Hall de Entrada", "2024", "T-Advanced", StatusTranca.NOVA);
        trancaCriada.setId(1);
        TrancaRespostaDTO respostaDTO = new TrancaRespostaDTO(1, 101, "Hall de Entrada", "2024", "T-Advanced", StatusTranca.NOVA, null);

        when(trancaService.criarTranca(any(TrancaRequestDTO.class))).thenReturn(trancaCriada);
        when(trancaMapper.toResponseDTO(trancaCriada)).thenReturn(respostaDTO);
        
        mockMvc.perform(post("/tranca")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    // --- TESTE ADICIONAL PARA ATUALIZAR TRANCA ---
    @Test
    void atualizarTranca_deveRetornarNotFound_quandoTrancaNaoExiste() throws Exception {
        // Arrange
        Integer idNaoExistente = 99;
        TrancaRequestDTO requestDTO = new TrancaRequestDTO(101, "Novo Local", "2025", "Mod-Y");

        // Mockamos o serviço para retornar um Optional vazio
        when(trancaService.atualizarTranca(eq(idNaoExistente), any(TrancaRequestDTO.class)))
            .thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/tranca/{id}", idNaoExistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound());
    }

    // --- TESTE ADICIONAL PARA INTEGRAR TRANCA ---
    @Test
    void integrarTrancaNaRede_deveRetornarUnprocessableEntity_quandoServicoRetornaErro() throws Exception {
        // Arrange
        IntegrarTrancaDTO integrarDTO = new IntegrarTrancaDTO(99, 1, "FUNC01"); // Totem 99 não existe
        String mensagemDeErro = "Totem não encontrado.";
        
        // Mockamos o serviço para retornar a mensagem de erro
        when(trancaService.integrarTrancaEmTotem(any(IntegrarTrancaDTO.class))).thenReturn(mensagemDeErro);

        // Act & Assert
        mockMvc.perform(post("/tranca/integrarNaRede")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(integrarDTO)))
                .andExpect(status().isUnprocessableEntity()) // Esperamos o status 422
                .andExpect(content().string(mensagemDeErro));
    }

    // --- TESTE ADICIONAL PARA RETIRAR TRANCA ---
    @Test
    void retirarTrancaDaRede_deveRetornarUnprocessableEntity_quandoServicoRetornaErro() throws Exception {
        // Arrange
        RetirarTrancaDTO retirarDTO = new RetirarTrancaDTO(1, 1, "FUNC01", "REPARO");
        String mensagemDeErro = "A tranca não pode ser retirada pois contém uma bicicleta.";

        //Mockada
        when(trancaService.retirarTrancaDoSistema(any(RetirarTrancaDTO.class))).thenReturn(mensagemDeErro);

        // Act & Assert
        mockMvc.perform(post("/tranca/retirarDaRede")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(retirarDTO)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string(mensagemDeErro));
    }

}