package com.equipamento.Controller;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Totem;
import com.equipamento.Entity.Tranca;
import com.equipamento.Service.TotemService;
import com.equipamento.dto.*;
import com.equipamento.mapper.BicicletaMapper;
import com.equipamento.mapper.TotemMapper;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(classes = TrabalhoEs2Application.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class TotemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mockando todas as dependências do TotemController
    @MockBean
    private TotemService totemService;
    @MockBean
    private TotemMapper totemMapper;
    @MockBean
    private TrancaMapper trancaMapper;
    @MockBean
    private BicicletaMapper bicicletaMapper;

    // --- Teste para POST /totem ---
    @Test
    void criarTotem_deveRetornarTotemCriado() throws Exception {
        // Arrange
        TotemRequestDTO requestDTO = new TotemRequestDTO("Praca da Se", "Totem proximo a catedral");
        Totem totemCriado = new Totem("Praca da Se", "Totem proximo a catedral");
        totemCriado.setId(1);
        TotemRespostaDTO respostaDTO = new TotemRespostaDTO(1, "Praca da Se", "Totem proximo a catedral", List.of());

        when(totemService.criarTotem(any(TotemRequestDTO.class))).thenReturn(totemCriado);
        when(totemMapper.toResponseDTO(totemCriado)).thenReturn(respostaDTO);

        // Act & Assert
        mockMvc.perform(post("/totem")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk()) // Seu controller retorna 200 OK
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.localizacao").value("Praca da Se"));
    }

    // --- Teste para DELETE /totem/{id} ---
    @Test
    void removerTotem_deveRetornarOk_quandoRemovidoComSucesso() throws Exception {
        // Arrange
        Integer idTotem = 1;
        when(totemService.removerTotem(idTotem)).thenReturn(true);

        // Act & Assert
        mockMvc.perform(delete("/totem/{id}", idTotem))
                .andExpect(status().isOk());
    }

    @Test
    void removerTotem_deveRetornarBadRequest_quandoFalhaNaRemocao() throws Exception {
        // Arrange
        Integer idTotem = 99;
        when(totemService.removerTotem(idTotem)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/totem/{id}", idTotem))
                .andExpect(status().isBadRequest());
    }

    // --- Teste para GET /totem/{idTotem}/trancas ---
    @Test
    void listarTrancasDoTotem_deveRetornarListaDeTrancas() throws Exception {
        // Arrange
        Integer idTotem = 1;

     
        Tranca tranca1 = new Tranca(10, "Ponto A", "2023", "T1", StatusTranca.LIVRE);
        Tranca tranca2 = new Tranca(11, "Ponto B", "2023", "T1", StatusTranca.OCUPADA);

        Totem totem = new Totem("Praca da Se", "Totem proximo a catedral");
        totem.setId(idTotem);
        totem.setTrancasNaRede(Arrays.asList(tranca1, tranca2)); 

        
        TrancaRespostaDTO dtoTranca1 = new TrancaRespostaDTO(10, 10, "Ponto A", "2023", "T1", StatusTranca.LIVRE, null);
        TrancaRespostaDTO dtoTranca2 = new TrancaRespostaDTO(11, 11, "Ponto B", "2023", "T1", StatusTranca.OCUPADA, null);

        when(totemService.buscarTotemPorId(idTotem)).thenReturn(Optional.of(totem));
        when(trancaMapper.toResponseDTO(tranca1)).thenReturn(dtoTranca1);
        when(trancaMapper.toResponseDTO(tranca2)).thenReturn(dtoTranca2);

        // Act & Assert
        mockMvc.perform(get("/totem/{idTotem}/trancas", idTotem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[1].statusTranca").value("OCUPADA"));
    }
    
    // --- Teste para GET /totem/{idTotem}/bicicletas ---
    @Test
    void listarBicicletasDoTotem_deveRetornarApenasBicicletasAncoradas() throws Exception {
        // Arrange
        Integer idTotem = 1;

        // Criando objetos de teste com os campos corretos
        Bicicleta bicicleta1 = new Bicicleta(); 
        bicicleta1.setId(100);
        bicicleta1.setMarca("Caloi");
        bicicleta1.setModelo("Mountain Bike");
        bicicleta1.setAno("2024");
        bicicleta1.setNumero(123);
        bicicleta1.setStatus(StatusBicicleta.DISPONIVEL);

        Tranca trancaComBicicleta = new Tranca(10, "Ponto A", "2023", "T1", StatusTranca.OCUPADA);
        trancaComBicicleta.setBicicleta(bicicleta1); 

        Tranca trancaSemBicicleta = new Tranca(11, "Ponto B", "2023", "T1", StatusTranca.LIVRE);

        Totem totem = new Totem("Praca da Se", "Totem proximo a catedral");
        totem.setId(idTotem);
        totem.setTrancasNaRede(Arrays.asList(trancaComBicicleta, trancaSemBicicleta));
        
        
        BicicletaRespostaDTO dtoBicicleta = new BicicletaRespostaDTO(100, "Caloi", "Mountain Bike", "2024", 123, StatusBicicleta.DISPONIVEL);

        when(totemService.buscarTotemPorId(idTotem)).thenReturn(Optional.of(totem));
        when(bicicletaMapper.toResponseDTO(bicicleta1)).thenReturn(dtoBicicleta);

        // Act & Assert
        mockMvc.perform(get("/totem/{idTotem}/bicicletas", idTotem))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1)) 
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].marca").value("Caloi"));
    }

 

    @Test
    void removerTotem_deveRetornarBadRequest_quandoServicoRetornaFalse() throws Exception {
        // Arrange (Organizar)
        Integer idTotem = 1;
        // mockamos  para simular uma falha na remoção (ex: totem com trancas)
        when(totemService.removerTotem(idTotem)).thenReturn(false);

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(delete("/totem/{id}", idTotem))
                .andExpect(status().isBadRequest()); // Esperamos o status 400 Bad Request
    }


 

    @Test
    void buscarTotemPorId_deveRetornarNotFound_quandoNaoEncontrado() throws Exception {
        // Arrange
        Integer idNaoExistente = 99;
        //  mockamos o  service para retornar um Optional vazio
        when(totemService.buscarTotemPorId(idNaoExistente)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/totem/{id}", idNaoExistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Esperamos o status 404 Not Found
    }

    @Test
    void listarTrancasDoTotem_deveRetornarNotFound_quandoTotemNaoExiste() throws Exception {
        
        Integer idTotemInexistente = 999;
        
        //  o mockamos o serviço para simular que o totem não foi encontrado
        when(totemService.buscarTotemPorId(idTotemInexistente)).thenReturn(Optional.empty());

        // Act & Assert (Agir e Verificar)
        mockMvc.perform(get("/totem/{idTotem}/trancas", idTotemInexistente)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()); // Esperamos o status 404 Not Found
    }


    
}