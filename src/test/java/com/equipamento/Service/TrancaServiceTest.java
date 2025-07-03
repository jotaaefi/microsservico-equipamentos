package com.equipamento.Service; 
import com.equipamento.Entity.Tranca;    
import com.equipamento.Entity.StatusTranca; 
import com.equipamento.Entity.Bicicleta; 
import com.equipamento.Entity.StatusBicicleta; 
import com.equipamento.Entity.Totem;     

import com.equipamento.Repository.TrancaRepository; 
import com.equipamento.dto.TrancaRequestDTO; 
import com.equipamento.dto.IntegrarTrancaDTO; 
import com.equipamento.dto.RetirarTrancaDTO; 
import com.equipamento.mapper.TrancaMapper; 

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

class TrancaServiceTest {

    @Mock // Simula o repositório TrancaRepository
    private TrancaRepository trancaRepository;

    @Mock // Simula o mapper TrancaMapper
    private TrancaMapper trancaMapper;

    @Mock // Simula o serviço de funcionário FuncionarioService
    private FuncionarioService funcionarioService;

    @Mock // Simula o serviço de totem TotemService
    private TotemService totemService;

    @InjectMocks // Injeta os mocks automaticamente no serviço a ser testado
    private TrancaService trancaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste
    }

    // -----------------------------------------------------------------------------------
    // Testes para listarTrancas() (UC13)
    // -----------------------------------------------------------------------------------
    @Test
    void listarTrancas_deveRetornarTodasAsTrancas() {
        // Cenário
        Tranca tranca1 = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        Tranca tranca2 = new Tranca(2, "Local B", "2021", "Mod2", StatusTranca.OCUPADA);
        List<Tranca> trancasMock = Arrays.asList(tranca1, tranca2);

        when(trancaRepository.findAll()).thenReturn(trancasMock);

        // Ação
        List<Tranca> resultado = trancaService.listarTrancas();

        // Verificação
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Local A", resultado.get(0).getLocalizacao());
        verify(trancaRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------------------
    // Testes para buscarTrancaPorId() (UC13)
    // -----------------------------------------------------------------------------------
    @Test
    void buscarTrancaPorId_deveRetornarTranca_quandoEncontrada() {
        // Cenário
        Integer id = 1;
        Tranca trancaMock = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        when(trancaRepository.findById(id)).thenReturn(Optional.of(trancaMock));

        // Ação
        Optional<Tranca> resultado = trancaService.buscarTrancaPorId(id);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(trancaMock, resultado.get());
        verify(trancaRepository, times(1)).findById(id);
    }

    @Test
    void buscarTrancaPorId_deveRetornarVazio_quandoNaoEncontrada() {
        // Cenário
        Integer id = 99;
        when(trancaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Tranca> resultado = trancaService.buscarTrancaPorId(id);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(trancaRepository, times(1)).findById(id);
    }

    // -----------------------------------------------------------------------------------
    // Testes para criarTranca() (UC13)
    // -----------------------------------------------------------------------------------
    @Test
    void criarTranca_deveSalvarNovaTrancaComStatusNova() {
        // Cenário
        TrancaRequestDTO requestDTO = new TrancaRequestDTO(3, "Local C", "2023", "Mod3");
        Tranca trancaParaSalvar = new Tranca(3, "Local C", "2023", "Mod3", StatusTranca.NOVA);
        Tranca trancaSalva = new Tranca(3, "Local C", "2023", "Mod3", StatusTranca.NOVA);
        trancaSalva.setId(1); // Simula o ID gerado pelo banco

        when(trancaMapper.toEntity(requestDTO)).thenReturn(trancaParaSalvar);
        when(trancaRepository.save(any(Tranca.class))).thenReturn(trancaSalva);

        // Ação
        Tranca resultado = trancaService.criarTranca(requestDTO);

        // Verificação
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertEquals(StatusTranca.NOVA, resultado.getStatusTranca()); // R1 UC13
        verify(trancaMapper, times(1)).toEntity(requestDTO);
        verify(trancaRepository, times(1)).save(any(Tranca.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para atualizarTranca() (UC13)
    // -----------------------------------------------------------------------------------
    @Test
    void atualizarTranca_deveAtualizarDadosDaTranca_quandoEncontrada() {
        // Cenário
        Integer id = 1;
        TrancaRequestDTO requestDTO = new TrancaRequestDTO(1, "Local X", "2024", "ModX");
        Tranca trancaExistente = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        trancaExistente.setId(id);

        when(trancaRepository.findById(id)).thenReturn(Optional.of(trancaExistente));
        when(trancaRepository.save(any(Tranca.class))).thenReturn(trancaExistente);

        // Ação
        Optional<Tranca> resultado = trancaService.atualizarTranca(id, requestDTO);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals("Local X", resultado.get().getLocalizacao());
        assertEquals("2024", resultado.get().getAnoDeFabricacao());
        assertEquals("ModX", resultado.get().getModelo());
        // Verifica que numero e status NÃO foram alterados (R1, R3 UC13)
        assertEquals(1, resultado.get().getNumero()); // O número original deve permanecer
        assertEquals(StatusTranca.LIVRE, resultado.get().getStatusTranca()); // O status original deve permanecer
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, times(1)).save(trancaExistente);
    }

    @Test
    void atualizarTranca_deveRetornarVazio_quandoNaoEncontrada() {
        // Cenário
        Integer id = 99;
        TrancaRequestDTO requestDTO = new TrancaRequestDTO(99, "Local X", "2024", "ModX");
        when(trancaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Tranca> resultado = trancaService.atualizarTranca(id, requestDTO);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, never()).save(any(Tranca.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para removerTranca() (UC13 - Remoção Lógica)
    // -----------------------------------------------------------------------------------
    @Test
    void removerTranca_deveAposentarTranca_quandoNaoComBicicleta() {
        // Cenário
        Integer id = 1;
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(id);

        when(trancaRepository.findById(id)).thenReturn(Optional.of(tranca));
        when(trancaRepository.save(any(Tranca.class))).thenReturn(tranca);

        // Ação
        boolean resultado = trancaService.removerTranca(id);

        // Verificação
        assertTrue(resultado);
        assertEquals(StatusTranca.APOSENTADA, tranca.getStatusTranca());
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, times(1)).save(tranca);
    }

    @Test
    void removerTranca_naoDeveAposentarTranca_quandoComBicicleta() {
        // Cenário
        Integer id = 1;
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(100); // ATRIBUIR ID À BICICLETA NO TESTE
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.OCUPADA, bicicleta);
        tranca.setId(id);

        when(trancaRepository.findById(id)).thenReturn(Optional.of(tranca));

        // Ação
        boolean resultado = trancaService.removerTranca(id);

        // Verificação
        assertFalse(resultado); // R4 UC13
        assertEquals(StatusTranca.OCUPADA, tranca.getStatusTranca()); // Status não deve mudar
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, never()).save(any(Tranca.class));
    }

    @Test
    void removerTranca_deveRetornarFalse_quandoTrancaNaoEncontrada() {
        // Cenário
        Integer id = 99;
        when(trancaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        boolean resultado = trancaService.removerTranca(id);

        // Verificação
        assertFalse(resultado);
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, never()).save(any(Tranca.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para integrarTrancaEmTotem() (UC11)
    // -----------------------------------------------------------------------------------
    @Test
    void integrarTrancaEmTotem_deveIntegrarComSucesso_quandoValido() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcABC");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.NOVA);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100); // ATRIBUIR ID AO TOTEM NO TESTE
        // Note: Totem.addTranca(tranca) fará setTotem(this) na tranca e adicionará à lista.

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(totem));
        when(trancaRepository.save(any(Tranca.class))).thenReturn(tranca);
        when(totemService.salvarTotem(any(Totem.class))).thenReturn(totem);

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("Tranca integrada ao totem com sucesso.", resultado);
        assertEquals(StatusTranca.LIVRE, tranca.getStatusTranca());
        assertEquals(totem, tranca.getTotem()); // Verifica se a tranca foi associada ao totem
        assertTrue(totem.getTrancasNaRede().contains(tranca)); // Verifica se o totem contém a tranca

        verify(funcionarioService, times(1)).verificarFuncionarioExiste();
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(100);
        verify(trancaRepository, times(1)).save(tranca);
        verify(totemService, times(1)).salvarTotem(totem);
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoFuncionarioNaoExiste() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcInvalido");
        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(false);

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("Funcionário não cadastrado.", resultado);
        verify(funcionarioService, times(1)).verificarFuncionarioExiste();
        verify(trancaRepository, never()).findById(anyInt());
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoTrancaNaoEncontrada() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 99, "funcABC");
        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("Tranca não encontrada.", resultado);
        verify(trancaRepository, times(1)).findById(99);
        verify(totemService, never()).buscarTotemPorId(anyInt());
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoTrancaStatusInvalido() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcABC");
        // Status LIVRE é inválido para integração (esperado NOVA ou EM_REPARO)
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("A tranca não está em um status válido para integração (esperado NOVA ou EM_REPARO).", resultado);
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, never()).buscarTotemPorId(anyInt());
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoTotemNaoEncontrado() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(99, 1, "funcABC");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.NOVA);
        tranca.setId(1);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("Totem não encontrado.", resultado);
        verify(totemService, times(1)).buscarTotemPorId(99);
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoTrancaJaAssociadaATotem() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcABC");
        Totem totemExistente = new Totem("Localizacao Existente", "Descricao Existente");
        totemExistente.setId(200); // ATRIBUIR ID AO TOTEM EXISTENTE
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.NOVA);
        tranca.setId(1);
        tranca.setTotem(totemExistente); // Tranca já associada a um totem

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        // Mocka totemService para retornar o totem que queremos integrar, mas a tranca já está associada a outro
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(new Totem("Outro Local", "Outra Descricao")));

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("Tranca já está associada a um totem.", resultado);
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(100);
        verify(trancaRepository, never()).save(any(Tranca.class));
        verify(totemService, never()).salvarTotem(any(Totem.class));
    }


    // -----------------------------------------------------------------------------------
    // Testes para retirarTrancaDoSistema() (UC12)
    // -----------------------------------------------------------------------------------
    @Test
    void retirarTrancaDoSistema_deveRetirarComSucesso_paraReparo() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "REPARO");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100); // ATRIBUIR ID AO TOTEM NO TESTE
        totem.addTranca(tranca); // Simula que a tranca está no totem

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(totem));
        when(trancaRepository.save(any(Tranca.class))).thenReturn(tranca);
        when(totemService.salvarTotem(any(Totem.class))).thenReturn(totem);

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Tranca retirada da rede com sucesso.", resultado);
        assertEquals(StatusTranca.EM_REPARO, tranca.getStatusTranca());
        assertNull(tranca.getTotem()); // Tranca deve ser desassociada do totem
        assertFalse(totem.getTrancasNaRede().contains(tranca)); // Totem não deve conter a tranca

        verify(funcionarioService, times(1)).verificarFuncionarioExiste();
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(100);
        verify(trancaRepository, times(1)).save(tranca);
        verify(totemService, times(1)).salvarTotem(totem);
    }

    @Test
    void retirarTrancaDoSistema_deveRetirarComSucesso_paraAposentar() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "APOSENTAR");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100); // ATRIBUIR ID AO TOTEM NO TESTE
        totem.addTranca(tranca);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(totem));
        when(trancaRepository.save(any(Tranca.class))).thenReturn(tranca);
        when(totemService.salvarTotem(any(Totem.class))).thenReturn(totem);

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Tranca retirada da rede com sucesso.", resultado);
        assertEquals(StatusTranca.APOSENTADA, tranca.getStatusTranca());
        assertNull(tranca.getTotem());
        assertFalse(totem.getTrancasNaRede().contains(tranca));

        verify(funcionarioService, times(1)).verificarFuncionarioExiste();
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(100);
        verify(trancaRepository, times(1)).save(tranca);
        verify(totemService, times(1)).salvarTotem(totem);
    }

    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoFuncionarioNaoExiste() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcInvalido", "REPARO");
        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(false);

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Funcionário não cadastrado.", resultado);
        verify(funcionarioService, times(1)).verificarFuncionarioExiste();
        verify(trancaRepository, never()).findById(anyInt());
    }

    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoTrancaNaoEncontrada() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 99, "funcABC", "REPARO");
        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Tranca não encontrada.", resultado);
        verify(trancaRepository, times(1)).findById(99);
        verify(totemService, never()).buscarTotemPorId(anyInt());
    }

    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoTrancaComBicicleta() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "REPARO");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1); // ATRIBUIR ID À BICICLETA NO TESTE
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.OCUPADA, bicicleta);
        tranca.setId(1);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("A tranca não pode ser retirada pois contém uma bicicleta.", resultado);
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, never()).buscarTotemPorId(anyInt());
        verify(trancaRepository, never()).save(any(Tranca.class));
    }

    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoTotemNaoEncontrado() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(99, 1, "funcABC", "REPARO");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Totem não encontrado.", resultado);
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(99);
        verify(trancaRepository, never()).save(any(Tranca.class));
        verify(totemService, never()).salvarTotem(any(Totem.class));
    }

    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoTrancaNaoAssociadaAoTotem() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "REPARO");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);
        // Tranca não associada a nenhum totem (totem nulo)
        // Ou tranca associada a um totem DIFERENTE do ID do DTO
        Totem totemNoDTO = new Totem("Localizacao Totem", "Descricao Totem");
        totemNoDTO.setId(100);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(totemNoDTO));

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Tranca não está associada a este totem.", resultado);
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(100);
        verify(trancaRepository, never()).save(any(Tranca.class));
        verify(totemService, never()).salvarTotem(any(Totem.class));
    }

    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoTrancaStatusInvalido() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "REPARO");
        // Status NOVA é inválido para retirada (não é LIVRE, OCUPADA ou REPARO_SOLICITADO)
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.NOVA);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100);
        totem.addTranca(tranca); // Simula que a tranca está no totem

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(totem));

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("A tranca está em um status inválido para retirada (esperado LIVRE, OCUPADA ou REPARO_SOLICITADO).", resultado);
        verify(trancaRepository, times(1)).findById(1);
        verify(totemService, times(1)).buscarTotemPorId(100);
        verify(trancaRepository, never()).save(any(Tranca.class));
        verify(totemService, never()).salvarTotem(any(Totem.class));
    }


    @Test
    void retirarTrancaDoSistema_deveRetornarErro_quandoAcaoReparadorInvalida() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "ACAO_INVALIDA"); // Ação inválida
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100);
        totem.addTranca(tranca);

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(totem));

        // Ação
        String resultado = trancaService.retirarTrancaDoSistema(dto);

        // Verificação
        assertEquals("Ação de reparador inválida (REPARO ou APOSENTAR).", resultado);
        verify(trancaRepository, never()).save(any(Tranca.class));
        verify(totemService, never()).salvarTotem(any(Totem.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para salvarTranca()
    // -----------------------------------------------------------------------------------
    @Test
    void salvarTranca_deveSalvarTranca() {
        // Cenário
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        when(trancaRepository.save(tranca)).thenReturn(tranca);

        // Ação
        Tranca resultado = trancaService.salvarTranca(tranca);

        // Verificação
        assertNotNull(resultado);
        assertEquals(tranca, resultado);
        verify(trancaRepository, times(1)).save(tranca);
    }

    // -----------------------------------------------------------------------------------
    // Testes para buscarTrancaPorBicicletaId()
    // -----------------------------------------------------------------------------------
    @Test
    void buscarTrancaPorBicicletaId_deveRetornarTranca_quandoEncontrada() {
        // Cenário
        Integer bicicletaId = 50;
        // ATRIBUIR ID À BICICLETA AQUI!
        Bicicleta bicicleta = new Bicicleta("MarcaB", "ModeloB", "2019", 50, StatusBicicleta.EM_USO);
        bicicleta.setId(bicicletaId); // <--- ATRIBUÍDO AQUI

        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.OCUPADA, bicicleta);
        tranca.setId(1);
        List<Tranca> allTrancas = Arrays.asList(
            new Tranca(2, "Local B", "2021", "Mod2", StatusTranca.LIVRE),
            tranca
        );
        when(trancaRepository.findAll()).thenReturn(allTrancas);

        // Ação
        Optional<Tranca> resultado = trancaService.buscarTrancaPorBicicletaId(bicicletaId);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(tranca, resultado.get());
        verify(trancaRepository, times(1)).findAll();
    }

    @Test
    void buscarTrancaPorBicicletaId_deveRetornarVazio_quandoNaoEncontrada() {
        // Cenário
        Integer bicicletaId = 99;
        // ATRIBUIR ID À BICICLETA AQUI!
        Bicicleta outraBicicleta = new Bicicleta("M", "M", "A", 1, StatusBicicleta.EM_USO);
        outraBicicleta.setId(1); // <--- ATRIBUÍDO AQUI (ID diferente do procurado)

        List<Tranca> allTrancas = Arrays.asList(
            new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE),
            new Tranca(2, "Local B", "2021", "Mod2", StatusTranca.OCUPADA, outraBicicleta)
        );
        when(trancaRepository.findAll()).thenReturn(allTrancas);

        // Ação
        Optional<Tranca> resultado = trancaService.buscarTrancaPorBicicletaId(bicicletaId);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(trancaRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------------------
    // Testes para atualizarStatusTranca()
    // -----------------------------------------------------------------------------------
    @Test
    void atualizarStatusTranca_deveAtualizarStatus_quandoTrancaEncontrada() {
        // Cenário
        Integer id = 1;
        StatusTranca novoStatus = StatusTranca.LIVRE;
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.OCUPADA);
        tranca.setId(id);

        when(trancaRepository.findById(id)).thenReturn(Optional.of(tranca));
        when(trancaRepository.save(any(Tranca.class))).thenReturn(tranca);

        // Ação
        Optional<Tranca> resultado = trancaService.atualizarStatusTranca(id, novoStatus);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(novoStatus, resultado.get().getStatusTranca());
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, times(1)).save(tranca);
    }

    @Test
    void atualizarStatusTranca_deveRetornarVazio_quandoTrancaNaoEncontrada() {
        // Cenário
        Integer id = 99;
        StatusTranca novoStatus = StatusTranca.LIVRE;
        when(trancaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Tranca> resultado = trancaService.atualizarStatusTranca(id, novoStatus);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(trancaRepository, times(1)).findById(id);
        verify(trancaRepository, never()).save(any(Tranca.class));
    }
}