package com.equipamento.Service; // Seu pacote atual para Services e Testes

import com.equipamento.Entity.Totem;     // Seu modelo Totem
import com.equipamento.Entity.Tranca;     // Seu modelo Tranca
import com.equipamento.Entity.StatusTranca; // Seu enum StatusTranca (necessário para Tranca)

import com.equipamento.Repository.TotemRepository; // Seu repositório Totem
import com.equipamento.dto.TotemRequestDTO; // Seu DTO TotemRequestDTO
import com.equipamento.mapper.TotemMapper; // Seu mapper TotemMapper

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TotemServiceTest {

    @Mock // Simula o repositório TotemRepository
    private TotemRepository totemRepository;

    @Mock // Simula o mapper TotemMapper
    private TotemMapper totemMapper;

    @Mock // Simula o serviço de tranca (para interações de Totem com Tranca)
    private TrancaService trancaService; // Pode ser necessário se o TotemService precisar interagir com TrancaService de forma mais profunda

    @InjectMocks // Injeta os mocks automaticamente no serviço a ser testado
    private TotemService totemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste
    }

    // -----------------------------------------------------------------------------------
    // Testes para listarTotens() (UC14)
    // -----------------------------------------------------------------------------------
    @Test
    void listarTotens_deveRetornarTodosOsTotens() {
        // Cenário
        Totem totem1 = new Totem("Localizacao A", "Descricao A");
        Totem totem2 = new Totem("Localizacao B", "Descricao B");
        List<Totem> totemsMock = Arrays.asList(totem1, totem2);

        when(totemRepository.findAll()).thenReturn(totemsMock);

        // Ação
        List<Totem> resultado = totemService.listarTotens();

        // Verificação
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Localizacao A", resultado.get(0).getLocalizacao());
        verify(totemRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------------------
    // Testes para buscarTotemPorId() (UC14)
    // -----------------------------------------------------------------------------------
    @Test
    void buscarTotemPorId_deveRetornarTotem_quandoEncontrado() {
        // Cenário
        Integer id = 1;
        Totem totemMock = new Totem("Localizacao A", "Descricao A");
        when(totemRepository.findById(id)).thenReturn(Optional.of(totemMock));

        // Ação
        Optional<Totem> resultado = totemService.buscarTotemPorId(id);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(totemMock, resultado.get());
        verify(totemRepository, times(1)).findById(id);
    }

    @Test
    void buscarTotemPorId_deveRetornarVazio_quandoNaoEncontrado() {
        // Cenário
        Integer id = 99;
        when(totemRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Totem> resultado = totemService.buscarTotemPorId(id);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(totemRepository, times(1)).findById(id);
    }

    // -----------------------------------------------------------------------------------
    // Testes para criarTotem() (UC14)
    // -----------------------------------------------------------------------------------
    @Test
    void criarTotem_deveSalvarNovoTotem() {
        // Cenário
        TotemRequestDTO requestDTO = new TotemRequestDTO("Nova Localizacao", "Nova Descricao");
        Totem totemParaSalvar = new Totem("Nova Localizacao", "Nova Descricao");
        Totem totemSalvo = new Totem("Nova Localizacao", "Nova Descricao");
        totemSalvo.setId(1); // Simula ID gerado

        when(totemMapper.toEntity(requestDTO)).thenReturn(totemParaSalvar);
        when(totemRepository.save(any(Totem.class))).thenReturn(totemSalvo);

        // Ação
        Totem resultado = totemService.criarTotem(requestDTO);

        // Verificação
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals("Nova Localizacao", resultado.getLocalizacao());
        verify(totemMapper, times(1)).toEntity(requestDTO);
        verify(totemRepository, times(1)).save(any(Totem.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para atualizarTotem() (UC14)
    // -----------------------------------------------------------------------------------
    @Test
    void atualizarTotem_deveAtualizarDadosDoTotem_quandoEncontrado() {
        // Cenário
        Integer id = 1;
        TotemRequestDTO requestDTO = new TotemRequestDTO("Localizacao Atualizada", "Descricao Atualizada");
        Totem totemExistente = new Totem("Local Antigo", "Desc Antiga");
        totemExistente.setId(id);

        when(totemRepository.findById(id)).thenReturn(Optional.of(totemExistente));
        when(totemRepository.save(any(Totem.class))).thenReturn(totemExistente);

        // Ação
        Optional<Totem> resultado = totemService.atualizarTotem(id, requestDTO);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals("Localizacao Atualizada", resultado.get().getLocalizacao());
        assertEquals("Descricao Atualizada", resultado.get().getDescricao());
        verify(totemRepository, times(1)).findById(id);
        verify(totemRepository, times(1)).save(totemExistente);
    }

    @Test
    void atualizarTotem_deveRetornarVazio_quandoNaoEncontrado() {
        // Cenário
        Integer id = 99;
        TotemRequestDTO requestDTO = new TotemRequestDTO("Localizacao Atualizada", "Descricao Atualizada");
        when(totemRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Totem> resultado = totemService.atualizarTotem(id, requestDTO);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(totemRepository, times(1)).findById(id);
        verify(totemRepository, never()).save(any(Totem.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para removerTotem() (UC14 - Remoção)
    // -----------------------------------------------------------------------------------
    @Test
    void removerTotem_deveRemoverComSucesso_quandoNaoPossuiTrancas() {
        // Cenário
        Integer id = 1;
        Totem totem = new Totem("Localizacao Teste", "Descricao Teste");
        totem.setId(id);
        // Garante que a lista de trancas está vazia
        totem.setTrancasNaRede(Arrays.asList());

        when(totemRepository.findById(id)).thenReturn(Optional.of(totem));
        doNothing().when(totemRepository).delete(totem); // Mocka a chamada de delete

        // Ação
        boolean resultado = totemService.removerTotem(id);

        // Verificação
        assertTrue(resultado);
        verify(totemRepository, times(1)).findById(id);
        verify(totemRepository, times(1)).delete(totem);
    }

    @Test
    void removerTotem_naoDeveRemover_quandoPossuiTrancas() {
        // Cenário
        Integer id = 1;
        Totem totem = new Totem("Localizacao Teste", "Descricao Teste");
        totem.setId(id);
        // Adiciona uma tranca para simular que o totem não está vazio
        Tranca trancaMock = new Tranca(1, "Local", "2020", "Mod", StatusTranca.LIVRE);
        totem.addTranca(trancaMock); // Assume que addTranca funciona como esperado

        when(totemRepository.findById(id)).thenReturn(Optional.of(totem));

        // Ação
        boolean resultado = totemService.removerTotem(id);

        // Verificação
        assertFalse(resultado); // R3 UC14
        verify(totemRepository, times(1)).findById(id);
        verify(totemRepository, never()).delete(any(Totem.class)); // Garante que delete não foi chamado
    }

    @Test
    void removerTotem_deveRetornarFalse_quandoTotemNaoEncontrado() {
        // Cenário
        Integer id = 99;
        when(totemRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        boolean resultado = totemService.removerTotem(id);

        // Verificação
        assertFalse(resultado);
        verify(totemRepository, times(1)).findById(id);
        verify(totemRepository, never()).delete(any(Totem.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para salvarTotem()
    // -----------------------------------------------------------------------------------
    @Test
    void salvarTotem_deveSalvarTotem() {
        // Cenário
        Totem totem = new Totem("Localizacao Teste", "Descricao Teste");
        when(totemRepository.save(totem)).thenReturn(totem);

        // Ação
        Totem resultado = totemService.salvarTotem(totem);

        // Verificação
        assertNotNull(resultado);
        assertEquals(totem, resultado);
        verify(totemRepository, times(1)).save(totem);
    }
}