package com.equipamento.Service;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.Entity.StatusTranca; // Importe StatusTranca para uso com Tranca
import com.equipamento.Entity.Tranca;     // Importe Tranca para uso nas validações

import com.equipamento.Repository.BicicletaRepository;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.IntegrarBicicletaDTO;
import com.equipamento.dto.RetirarBicicletaDTO;
import com.equipamento.mapper.BicicletaMapper;

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

class BicicletaServiceTest {

    @Mock // Simula o repositório
    private BicicletaRepository bicicletaRepository;

    @Mock // Simula o mapper
    private BicicletaMapper bicicletaMapper;

    @Mock // Simula o serviço de funcionário
    private FuncionarioService funcionarioService;

    @Mock // Simula o serviço de tranca
    private TrancaService trancaService;

    @InjectMocks // Injeta os mocks automaticamente no serviço a ser testado
    private BicicletaService bicicletaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste
        // Reseta o contador estático para garantir testes isolados (se usar um contador real no serviço)
        // Se o numeroBicicletaCounter não for estático ou se for gerenciado por Spring, isso não seria necessário.
        // Se for estático, para testes unitários, uma forma é resetá-lo ou passar um valor fixo.
        // Para este teste, vamos assumir que ele é resetável ou não afeta o teste isolado.
    }

    // -----------------------------------------------------------------------------------
    // Testes para listarBicicletas()
    // -----------------------------------------------------------------------------------
    @Test
    void listarBicicletas_deveRetornarTodasAsBicicletas() {
        // Cenário (Arrange)
        Bicicleta bicicleta1 = new Bicicleta("MarcaA", "ModeloX", "2020", 1, StatusBicicleta.DISPONIVEL);
        Bicicleta bicicleta2 = new Bicicleta("MarcaB", "ModeloY", "2021", 2, StatusBicicleta.EM_USO);
        List<Bicicleta> bicicletasMock = Arrays.asList(bicicleta1, bicicleta2);

        when(bicicletaRepository.findAll()).thenReturn(bicicletasMock);

        // Ação (Act)
        List<Bicicleta> resultado = bicicletaService.listarBicicletas();

        // Verificação (Assert)
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("MarcaA", resultado.get(0).getMarca());
        verify(bicicletaRepository, times(1)).findAll(); // Verifica se o findAll foi chamado uma vez
    }

    // -----------------------------------------------------------------------------------
    // Testes para buscarBicicletaPorId()
    // -----------------------------------------------------------------------------------
    @Test
    void buscarBicicletaPorId_deveRetornarBicicleta_quandoEncontrada() {
        // Cenário
        Integer id = 1;
        Bicicleta bicicletaMock = new Bicicleta("MarcaA", "ModeloX", "2020", 1, StatusBicicleta.DISPONIVEL);
        when(bicicletaRepository.findById(id)).thenReturn(Optional.of(bicicletaMock));

        // Ação
        Optional<Bicicleta> resultado = bicicletaService.buscarBicicletaPorId(id);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(bicicletaMock, resultado.get());
        verify(bicicletaRepository, times(1)).findById(id);
    }

    @Test
    void buscarBicicletaPorId_deveRetornarVazio_quandoNaoEncontrada() {
        // Cenário
        Integer id = 99;
        when(bicicletaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Bicicleta> resultado = bicicletaService.buscarBicicletaPorId(id);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(bicicletaRepository, times(1)).findById(id);
    }

    // -----------------------------------------------------------------------------------
    // Testes para criarBicicleta()
    // -----------------------------------------------------------------------------------
    @Test
    void criarBicicleta_deveSalvarNovaBicicletaComStatusNovaENumeroGerado() {
        // Cenário
        BicicletaRequestDTO requestDTO = new BicicletaRequestDTO("MarcaD", "ModeloW", "2023");
        Bicicleta bicicletaParaSalvar = new Bicicleta("MarcaD", "ModeloW", "2023", 1, StatusBicicleta.NOVA);
        Bicicleta bicicletaSalva = new Bicicleta("MarcaD", "ModeloW", "2023", 1, StatusBicicleta.NOVA);
        bicicletaSalva.setId(1); // Simula o ID gerado pelo banco

        when(bicicletaMapper.toEntity(requestDTO)).thenReturn(bicicletaParaSalvar);
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicletaSalva);

        // Ação
        Bicicleta resultado = bicicletaService.criarBicicleta(requestDTO);

        // Verificação
        assertNotNull(resultado);
        assertNotNull(resultado.getNumero()); // Verifica se o número foi gerado
        assertEquals(StatusBicicleta.NOVA, resultado.getStatus()); // Verifica se o status é NOVA
        verify(bicicletaMapper, times(1)).toEntity(requestDTO);
        verify(bicicletaRepository, times(1)).save(any(Bicicleta.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para atualizarBicicleta()
    // -----------------------------------------------------------------------------------
    @Test
    void atualizarBicicleta_deveAtualizarDadosDaBicicleta_quandoEncontrada() {
        // Cenário
        Integer id = 1;
        BicicletaRequestDTO requestDTO = new BicicletaRequestDTO("MarcaNova", "ModeloNovo", "2023");
        Bicicleta bicicletaExistente = new Bicicleta("MarcaAntiga", "ModeloAntigo", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicletaExistente.setId(id); // Importante para simular o ID real

        when(bicicletaRepository.findById(id)).thenReturn(Optional.of(bicicletaExistente));
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicletaExistente);

        // Ação
        Optional<Bicicleta> resultado = bicicletaService.atualizarBicicleta(id, requestDTO);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals("MarcaNova", resultado.get().getMarca());
        assertEquals("ModeloNovo", resultado.get().getModelo());
        assertEquals("2023", resultado.get().getAno());
        assertEquals(StatusBicicleta.DISPONIVEL, resultado.get().getStatus()); // Status não deve ser alterado aqui
        assertEquals(bicicletaExistente.getNumero(), resultado.get().getNumero()); // Número não deve ser alterado
        verify(bicicletaRepository, times(1)).findById(id);
        verify(bicicletaRepository, times(1)).save(bicicletaExistente);
    }

    @Test
    void atualizarBicicleta_deveRetornarVazio_quandoNaoEncontrada() {
        // Cenário
        Integer id = 99;
        BicicletaRequestDTO requestDTO = new BicicletaRequestDTO("MarcaNova", "ModeloNovo", "2023");
        when(bicicletaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Bicicleta> resultado = bicicletaService.atualizarBicicleta(id, requestDTO);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(bicicletaRepository, times(1)).findById(id);
        verify(bicicletaRepository, never()).save(any(Bicicleta.class)); // Garante que save não foi chamado
    }

    // -----------------------------------------------------------------------------------
    // Testes para aposentarBicicleta()
    // -----------------------------------------------------------------------------------
    @Test
    void aposentarBicicleta_deveAposentarBicicleta_quandoNaoEmTranca() {
        // Cenário
        Integer id = 1;
        Bicicleta bicicletaExistente = new Bicicleta("MarcaA", "ModeloX", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicletaExistente.setId(id);

        when(bicicletaRepository.findById(id)).thenReturn(Optional.of(bicicletaExistente));
        when(trancaService.buscarTrancaPorBicicletaId(id)).thenReturn(Optional.empty()); // SIMULA: Bicicleta não está em nenhuma tranca
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicletaExistente);

        // Ação
        boolean resultado = bicicletaService.aposentarBicicleta(id);

        // Verificação
        assertTrue(resultado);
        assertEquals(StatusBicicleta.APOSENTADA, bicicletaExistente.getStatus());
        verify(bicicletaRepository, times(1)).findById(id);
        verify(trancaService, times(1)).buscarTrancaPorBicicletaId(id);
        verify(bicicletaRepository, times(1)).save(bicicletaExistente);
    }

    @Test
    void aposentarBicicleta_naoDeveAposentarBicicleta_quandoEmTranca() {
        // Cenário
        Integer id = 1;
        Bicicleta bicicletaExistente = new Bicicleta("MarcaA", "ModeloX", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicletaExistente.setId(id);
        Tranca trancaMock = new Tranca(101, "Local", "2022", "Modelo", StatusTranca.OCUPADA, bicicletaExistente);

        when(bicicletaRepository.findById(id)).thenReturn(Optional.of(bicicletaExistente));
        when(trancaService.buscarTrancaPorBicicletaId(id)).thenReturn(Optional.of(trancaMock)); // SIMULA: Bicicleta ESTÁ em tranca

        // Ação
        boolean resultado = bicicletaService.aposentarBicicleta(id);

        // Verificação
        assertFalse(resultado); // Deve retornar falso
        assertEquals(StatusBicicleta.DISPONIVEL, bicicletaExistente.getStatus()); // Status não deve mudar
        verify(bicicletaRepository, times(1)).findById(id);
        verify(trancaService, times(1)).buscarTrancaPorBicicletaId(id);
        verify(bicicletaRepository, never()).save(any(Bicicleta.class)); // Save não deve ser chamado
    }

    @Test
    void aposentarBicicleta_deveRetornarFalse_quandoBicicletaNaoEncontrada() {
        // Cenário
        Integer id = 99;
        when(bicicletaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        boolean resultado = bicicletaService.aposentarBicicleta(id);

        // Verificação
        assertFalse(resultado);
        verify(bicicletaRepository, times(1)).findById(id);
        verify(trancaService, never()).buscarTrancaPorBicicletaId(anyInt());
        verify(bicicletaRepository, never()).save(any(Bicicleta.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para integrarBicicletaNaRede()
    // -----------------------------------------------------------------------------------
    @Test
    void integrarBicicletaNaRede_deveIntegrarComSucesso_quandoValido() {
        // Cenário
        IntegrarBicicletaDTO dto = new IntegrarBicicletaDTO(1, 10, "func123");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.NOVA);
        bicicleta.setId(1);
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.LIVRE);
        tranca.setId(10);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicleta);
        when(trancaService.salvarTranca(any(Tranca.class))).thenReturn(tranca);

        // Ação
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);

        // Verificação
        assertEquals("Bicicleta integrada à rede com sucesso.", resultado);
        assertEquals(StatusBicicleta.DISPONIVEL, bicicleta.getStatus());
        assertEquals(StatusTranca.OCUPADA, tranca.getStatusTranca());
        assertEquals(bicicleta, tranca.getBicicleta()); // Verifica se a bicicleta foi associada à tranca

        verify(funcionarioService, times(1)).verificarFuncionarioExiste("func123");
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, times(1)).buscarTrancaPorId(10);
        verify(bicicletaRepository, times(1)).save(bicicleta);
        verify(trancaService, times(1)).salvarTranca(tranca);
    }

    @Test
    void integrarBicicletaNaRede_deveRetornarErro_quandoFuncionarioNaoExiste() {
        // Cenário
        IntegrarBicicletaDTO dto = new IntegrarBicicletaDTO(1, 10, "funcInvalido");
        when(funcionarioService.verificarFuncionarioExiste("funcInvalido")).thenReturn(false);

        // Ação
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);

        // Verificação
        assertEquals("Funcionário não cadastrado.", resultado);
        verify(funcionarioService, times(1)).verificarFuncionarioExiste("funcInvalido");
        verify(bicicletaRepository, never()).findById(anyInt()); // Nenhuma outra chamada se falhar no funcionário
    }

    @Test
    void integrarBicicletaNaRede_deveRetornarErro_quandoBicicletaNaoEncontrada() {
        // Cenário
        IntegrarBicicletaDTO dto = new IntegrarBicicletaDTO(99, 10, "func123");
        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);

        // Verificação
        assertEquals("Bicicleta não encontrada.", resultado);
        verify(bicicletaRepository, times(1)).findById(99);
        verify(trancaService, never()).buscarTrancaPorId(anyInt());
    }

    @Test
    void integrarBicicletaNaRede_deveRetornarErro_quandoBicicletaStatusInvalido() {
        // Cenário
        IntegrarBicicletaDTO dto = new IntegrarBicicletaDTO(1, 10, "func123");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.EM_USO); // Status inválido
        bicicleta.setId(1);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));

        // Ação
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);

        // Verificação
        assertEquals("A bicicleta não está em um status válido para integração (esperado NOVA ou EM_REPARO).", resultado);
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, never()).buscarTrancaPorId(anyInt());
    }

    @Test
    void integrarBicicletaNaRede_deveRetornarErro_quandoTrancaNaoEncontrada() {
        // Cenário
        IntegrarBicicletaDTO dto = new IntegrarBicicletaDTO(1, 99, "func123");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.NOVA);
        bicicleta.setId(1);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);

        // Verificação
        assertEquals("Tranca não encontrada.", resultado);
        verify(trancaService, times(1)).buscarTrancaPorId(99);
        verify(bicicletaRepository, times(1)).findById(1);
    }

    @Test
    void integrarBicicletaNaRede_deveRetornarErro_quandoTrancaStatusInvalido() {
        // Cenário
        IntegrarBicicletaDTO dto = new IntegrarBicicletaDTO(1, 10, "func123");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.NOVA);
        bicicleta.setId(1);
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.OCUPADA); // Status inválido
        tranca.setId(10);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));

        // Ação
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);

        // Verificação
        assertEquals("A tranca não está disponível (LIVRE).", resultado);
        verify(trancaService, times(1)).buscarTrancaPorId(10);
    }

    // -----------------------------------------------------------------------------------
    // Testes para retirarBicicletaDaRede()
    // -----------------------------------------------------------------------------------
    @Test
    void retirarBicicletaDaRede_deveRetirarComSucesso_quandoValidoParaReparo() {
        // Cenário
        IntegrarBicicletaDTO dtoIntegrar = new IntegrarBicicletaDTO(1, 10, "func123"); // para o setup
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "func123", "REPARO");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1);
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.OCUPADA, bicicleta); // Tranca com a bicicleta
        tranca.setId(10);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicleta);
        when(trancaService.salvarTranca(any(Tranca.class))).thenReturn(tranca);

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("Bicicleta retirada da rede com sucesso.", resultado);
        assertEquals(StatusBicicleta.EM_REPARO, bicicleta.getStatus()); // Verifica se o status mudou para EM_REPARO
        assertEquals(StatusTranca.LIVRE, tranca.getStatusTranca()); // Tranca deve ficar LIVRE
        assertNull(tranca.getBicicleta()); // Bicicleta deve ser desassociada da tranca

        verify(funcionarioService, times(1)).verificarFuncionarioExiste("func123");
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, times(1)).buscarTrancaPorId(10);
        verify(bicicletaRepository, times(1)).save(bicicleta);
        verify(trancaService, times(1)).salvarTranca(tranca);
    }

    @Test
    void retirarBicicletaDaRede_deveRetirarComSucesso_quandoValidoParaAposentar() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "func123", "APOSENTAR");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1);
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.OCUPADA, bicicleta);
        tranca.setId(10);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicleta);
        when(trancaService.salvarTranca(any(Tranca.class))).thenReturn(tranca);

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("Bicicleta retirada da rede com sucesso.", resultado);
        assertEquals(StatusBicicleta.APOSENTADA, bicicleta.getStatus()); // Verifica se o status mudou para APOSENTADA
        assertEquals(StatusTranca.LIVRE, tranca.getStatusTranca()); // Tranca deve ficar LIVRE
        assertNull(tranca.getBicicleta()); // Bicicleta deve ser desassociada da tranca

        verify(funcionarioService, times(1)).verificarFuncionarioExiste("func123");
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, times(1)).buscarTrancaPorId(10);
        verify(bicicletaRepository, times(1)).save(bicicleta);
        verify(trancaService, times(1)).salvarTranca(tranca);
    }

    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoFuncionarioNaoExiste() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "funcInvalido", "REPARO");
        when(funcionarioService.verificarFuncionarioExiste("funcInvalido")).thenReturn(false);

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("Funcionário não cadastrado.", resultado);
        verify(funcionarioService, times(1)).verificarFuncionarioExiste("funcInvalido");
        verify(bicicletaRepository, never()).findById(anyInt());
    }

    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoBicicletaNaoEncontrada() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(99, 10, "func123", "REPARO");
        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("Bicicleta não encontrada.", resultado);
        verify(bicicletaRepository, times(1)).findById(99);
        verify(trancaService, never()).buscarTrancaPorId(anyInt());
    }

    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoBicicletaStatusInvalido() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "func123", "REPARO");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.NOVA); // Status inválido para retirada
        bicicleta.setId(1);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("A bicicleta não está em um status válido para retirada (esperado DISPONIVEL ou REPARO_SOLICITADO).", resultado);
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, never()).buscarTrancaPorId(anyInt());
    }

    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoTrancaNaoEncontrada() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 99, "func123", "REPARO");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(99)).thenReturn(Optional.empty());

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("Tranca não encontrada.", resultado);
        verify(trancaService, times(1)).buscarTrancaPorId(99);
    }

    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoBicicletaNaoAssociadaATranca() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "func123", "REPARO");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1);
        // Tranca com outra bicicleta ou sem bicicleta
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.OCUPADA);
        tranca.setId(10);
        Bicicleta outraBicicleta = new Bicicleta("Outra", "Outra", "2022", 99, StatusBicicleta.EM_USO);
        outraBicicleta.setId(99);
        tranca.setBicicleta(outraBicicleta); // Tranca está ocupada por OUTRA bicicleta

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("A bicicleta não está associada a esta tranca.", resultado);
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, times(1)).buscarTrancaPorId(10);
        verify(bicicletaRepository, never()).save(any(Bicicleta.class));
        verify(trancaService, never()).salvarTranca(any(Tranca.class));
    }


    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoTrancaStatusInvalido() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "func123", "REPARO");
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1);
        // Tranca com a bicicleta, mas status LIVRE (inválido para retirada)
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.LIVRE, bicicleta);
        tranca.setId(10);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("A tranca não está no status OCUPADA.", resultado);
        verify(bicicletaRepository, times(1)).findById(1);
        verify(trancaService, times(1)).buscarTrancaPorId(10);
        verify(bicicletaRepository, never()).save(any(Bicicleta.class));
        verify(trancaService, never()).salvarTranca(any(Tranca.class));
    }

    @Test
    void retirarBicicletaDaRede_deveRetornarErro_quandoAcaoReparadorInvalida() {
        // Cenário
        RetirarBicicletaDTO dto = new RetirarBicicletaDTO(1, 10, "func123", "ACAO_INVALIDA"); // Ação inválida
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(1);
        Tranca tranca = new Tranca(10, "LocalA", "2021", "ModZ", StatusTranca.OCUPADA, bicicleta);
        tranca.setId(10);

        when(funcionarioService.verificarFuncionarioExiste("func123")).thenReturn(true);
        when(bicicletaRepository.findById(1)).thenReturn(Optional.of(bicicleta));
        when(trancaService.buscarTrancaPorId(10)).thenReturn(Optional.of(tranca));

        // Ação
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);

        // Verificação
        assertEquals("Ação de reparador inválida (REPARO ou APOSENTAR).", resultado);
        verify(bicicletaRepository, never()).save(any(Bicicleta.class));
        verify(trancaService, never()).salvarTranca(any(Tranca.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para atualizarStatusBicicleta()
    // -----------------------------------------------------------------------------------
    @Test
    void atualizarStatusBicicleta_deveAtualizarStatus_quandoBicicletaEncontrada() {
        // Cenário
        Integer id = 1;
        StatusBicicleta novoStatus = StatusBicicleta.EM_USO;
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(id);

        when(bicicletaRepository.findById(id)).thenReturn(Optional.of(bicicleta));
        when(bicicletaRepository.save(any(Bicicleta.class))).thenReturn(bicicleta);

        // Ação
        Optional<Bicicleta> resultado = bicicletaService.atualizarStatusBicicleta(id, novoStatus);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(novoStatus, resultado.get().getStatus());
        verify(bicicletaRepository, times(1)).findById(id);
        verify(bicicletaRepository, times(1)).save(bicicleta);
    }

    @Test
    void atualizarStatusBicicleta_deveRetornarVazio_quandoBicicletaNaoEncontrada() {
        // Cenário
        Integer id = 99;
        StatusBicicleta novoStatus = StatusBicicleta.EM_USO;
        when(bicicletaRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Bicicleta> resultado = bicicletaService.atualizarStatusBicicleta(id, novoStatus);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(bicicletaRepository, times(1)).findById(id);
        verify(bicicletaRepository, never()).save(any(Bicicleta.class));
    }
}