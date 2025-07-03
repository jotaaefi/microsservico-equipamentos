package com.equipamento.Controller;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.Service.BicicletaService;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.BicicletaRespostaDTO;
import com.equipamento.dto.IntegrarBicicletaDTO;
import com.equipamento.dto.RetirarBicicletaDTO; 
import com.equipamento.mapper.BicicletaMapper;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Configuração de teste final e funcional para o seu projeto
@SpringBootTest(classes = TrabalhoEs2Application.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class BicicletaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BicicletaService bicicletaService;

    @MockBean
    private BicicletaMapper bicicletaMapper;

    // --- Teste para GET /bicicleta ---
    @Test
    void listarBicicletas_deveRetornarListaDeBicicletas() throws Exception {
        // Arrange
        Bicicleta bike1 = new Bicicleta("Caloi", "10", "2023", 1, StatusBicicleta.DISPONIVEL);
        bike1.setId(1);
        Bicicleta bike2 = new Bicicleta("Monark", "Barra Forte", "2024", 2, StatusBicicleta.EM_REPARO);
        bike2.setId(2);

        BicicletaRespostaDTO dto1 = new BicicletaRespostaDTO(1, "Caloi", "10", "2023", 1, StatusBicicleta.DISPONIVEL);
        BicicletaRespostaDTO dto2 = new BicicletaRespostaDTO(2, "Monark", "Barra Forte", "2024", 2, StatusBicicleta.EM_REPARO);

        when(bicicletaService.listarBicicletas()).thenReturn(Arrays.asList(bike1, bike2));
        when(bicicletaMapper.toResponseDTO(bike1)).thenReturn(dto1);
        when(bicicletaMapper.toResponseDTO(bike2)).thenReturn(dto2);

        // Act & Assert
        mockMvc.perform(get("/bicicleta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].marca").value("Caloi"))
                .andExpect(jsonPath("$[1].status").value("EM_REPARO"));
    }

    // --- Teste para POST /bicicleta ---
    @Test
    void criarBicicleta_deveRetornarCreatedComBicicleta() throws Exception {
        // Arrange
        BicicletaRequestDTO requestDTO = new BicicletaRequestDTO("Houston", "Foxer", "2025");
        
        Bicicleta bicicletaCriada = new Bicicleta("Houston", "Foxer", "2025", 3, StatusBicicleta.NOVA);
        bicicletaCriada.setId(3);

        BicicletaRespostaDTO respostaDTO = new BicicletaRespostaDTO(3, "Houston", "Foxer", "2025", 3, StatusBicicleta.NOVA);

        when(bicicletaService.criarBicicleta(any(BicicletaRequestDTO.class))).thenReturn(bicicletaCriada);
        when(bicicletaMapper.toResponseDTO(bicicletaCriada)).thenReturn(respostaDTO);

        // Act & Assert
        mockMvc.perform(post("/bicicleta")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.status").value("NOVA"));
    }

    // --- Teste para DELETE /bicicleta/{id} ---
    @Test
    void aposentarBicicleta_deveRetornarOk_quandoAposentadaComSucesso() throws Exception {
        // Arrange
        Integer idBicicleta = 1;
        when(bicicletaService.aposentarBicicleta(idBicicleta)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/bicicleta/{id}", idBicicleta))
                .andExpect(status().isOk());
    }

    // --- Teste para POST /bicicleta/integrarNaRede ---
    @Test
    void integrarBicicletaNaRede_deveRetornarOk_quandoIntegradaComSucesso() throws Exception {
        // Arrange
        IntegrarBicicletaDTO integrarDTO = new IntegrarBicicletaDTO(1, 10, "FUNC01");
        String mensagemSucesso = "Bicicleta integrada com sucesso na tranca 10.";

        when(bicicletaService.integrarBicicletaNaRede(any(IntegrarBicicletaDTO.class))).thenReturn(mensagemSucesso);

        // Act & Assert
        mockMvc.perform(post("/bicicleta/integrarNaRede")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(integrarDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));
    }

    // --- NOVO TESTE para POST /bicicleta/retirarDaRede ---
    @Test
    void retirarBicicletaDaRede_deveRetornarOk_quandoRetiradaComSucesso() throws Exception {
        // Arrange
        RetirarBicicletaDTO retirarDTO = new RetirarBicicletaDTO(1, 10, "FUNC01", "REPARO");
        String mensagemSucesso = "Bicicleta retirada com sucesso para reparo.";

        when(bicicletaService.retirarBicicletaDaRede(any(RetirarBicicletaDTO.class))).thenReturn(mensagemSucesso);

        // Act & Assert
        mockMvc.perform(post("/bicicleta/retirarDaRede")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(retirarDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));
    }

    // --- Teste para POST /bicicleta/{idBicicleta}/status/{acao} ---
    @Test
    void atualizarStatusBicicleta_deveRetornarBicicletaComNovoStatus() throws Exception {
        // Arrange
        Integer idBicicleta = 1;
        String acao = "EM_REPARO";
        
        Bicicleta bicicletaAtualizada = new Bicicleta("Caloi", "10", "2023", 1, StatusBicicleta.EM_REPARO);
        bicicletaAtualizada.setId(idBicicleta);
        
        BicicletaRespostaDTO respostaDTO = new BicicletaRespostaDTO(1, "Caloi", "10", "2023", 1, StatusBicicleta.EM_REPARO);

        when(bicicletaService.atualizarStatusBicicleta(eq(idBicicleta), eq(StatusBicicleta.EM_REPARO))).thenReturn(Optional.of(bicicletaAtualizada));
        when(bicicletaMapper.toResponseDTO(bicicletaAtualizada)).thenReturn(respostaDTO);

        // Act & Assert
        mockMvc.perform(post("/bicicleta/{idBicicleta}/status/{acao}", idBicicleta, acao))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_REPARO"));
    }

     @Test
    void atualizarBicicleta_deveRetornarNotFound_quandoBicicletaNaoExiste() throws Exception {
        // Arrange (Organizar)
        Integer idNaoExistente = 99;
        BicicletaRequestDTO requestDTO = new BicicletaRequestDTO("Marca Fantasma", "Modelo Fantasma", "2099");
        
        // Configuramos o mock do serviço para retornar um Optional vazio
        when(bicicletaService.atualizarBicicleta(eq(idNaoExistente), any(BicicletaRequestDTO.class)))
            .thenReturn(Optional.empty());

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(put("/bicicleta/{id}", idNaoExistente)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound()); // Esperamos o status 404 Not Found
    }


    @Test
    void integrarBicicletaNaRede_deveRetornarBadRequest_quandoServicoRetornaErro() throws Exception {
        // Arrange
        IntegrarBicicletaDTO integrarDTO = new IntegrarBicicletaDTO(1, 10, "FUNC01");
        String mensagemDeErro = "A tranca não está disponível (LIVRE).";
        
        // Mockamos o serviço para retornar a mensagem de erro
        when(bicicletaService.integrarBicicletaNaRede(any(IntegrarBicicletaDTO.class))).thenReturn(mensagemDeErro);

        // Act & Assert
        mockMvc.perform(post("/bicicleta/integrarNaRede")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(integrarDTO)))
                .andExpect(status().isBadRequest()) // Esperamos o status 400 Bad Request
                .andExpect(content().string(mensagemDeErro)); // E que o corpo da resposta seja a mensagem de erro
    }

}