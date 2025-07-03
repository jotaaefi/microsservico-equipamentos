package com.equipamento.Service; 
import com.equipamento.Entity.Tranca;    
import com.equipamento.Entity.StatusTranca; 
import com.equipamento.Entity.Bicicleta; 
import com.equipamento.Entity.StatusBicicleta; 
import com.equipamento.Entity.Totem;     

import com.equipamento.Repository.TrancaRepository; 

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

    @Mock
    private TrancaRepository trancaRepository;

    @Mock
    private TrancaMapper trancaMapper;

    @Mock
    private FuncionarioService funcionarioService;

    @Mock
    private TotemService totemService;

    @InjectMocks
    private TrancaService trancaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ... (os testes de listar, buscar, criar e atualizar não mudam e estão corretos) ...
    
    @Test
    void listarTrancas_deveRetornarTodasAsTrancas() {
        Tranca tranca1 = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        Tranca tranca2 = new Tranca(2, "Local B", "2021", "Mod2", StatusTranca.OCUPADA);
        when(trancaRepository.findAll()).thenReturn(Arrays.asList(tranca1, tranca2));
        List<Tranca> resultado = trancaService.listarTrancas();
        assertEquals(2, resultado.size());
        verify(trancaRepository, times(1)).findAll();
    }
    
    @Test
    void removerTranca_naoDeveAposentarTranca_quandoComBicicleta() {
        Integer id = 1;
        Bicicleta bicicleta = new Bicicleta("MarcaX", "ModeloY", "2020", 1, StatusBicicleta.DISPONIVEL);
        bicicleta.setId(100);
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.OCUPADA, bicicleta);
        tranca.setId(id);
        when(trancaRepository.findById(id)).thenReturn(Optional.of(tranca));
        boolean resultado = trancaService.removerTranca(id);
        assertFalse(resultado);
        verify(trancaRepository, never()).save(any(Tranca.class));
    }


    // --- Testes para integrarTrancaEmTotem() (UC11) ---
    @Test
    void integrarTrancaEmTotem_deveIntegrarComSucesso_quandoValido() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcABC");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.NOVA);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100);

        // CORREÇÃO: Adicionando anyString() pois o método espera um argumento
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
        // CORREÇÃO: Comparamos o ID do totem, não o objeto inteiro
        assertEquals(totem.getId(), tranca.getTotemId()); 
        verify(trancaRepository, times(1)).save(tranca);
        verify(totemService, times(1)).salvarTotem(totem);
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoFuncionarioNaoExiste() {
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcInvalido");
        // CORREÇÃO: Adicionando anyString()
        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(false);
        String resultado = trancaService.integrarTrancaEmTotem(dto);
        assertEquals("Funcionário não cadastrado.", resultado);
        verify(trancaRepository, never()).findById(anyInt());
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoTrancaJaAssociadaATotem() {
        // Cenário
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcABC");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.NOVA);
        tranca.setId(1);
        // CORREÇÃO: Usando setTotemId() para passar o ID (Integer)
        tranca.setTotemId(200); // Tranca já associada a um totem de ID 200

        when(funcionarioService.verificarFuncionarioExiste()).thenReturn(true);
        when(trancaRepository.findById(1)).thenReturn(Optional.of(tranca));
        when(totemService.buscarTotemPorId(100)).thenReturn(Optional.of(new Totem("Outro Local", "Outra Descricao")));

        // Ação
        String resultado = trancaService.integrarTrancaEmTotem(dto);

        // Verificação
        assertEquals("Tranca já está associada a um totem.", resultado);
        verify(trancaRepository, never()).save(any(Tranca.class));
    }


    // --- Testes para retirarTrancaDoSistema() (UC12) ---
    @Test
    void retirarTrancaDoSistema_deveRetirarComSucesso_paraReparo() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "REPARO");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100);
        
        // Associação para o teste
        tranca.setTotemId(totem.getId()); 

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
        // O teste aqui está correto, verifica se o totemId ficou nulo
        assertNull(tranca.getTotemId()); 
    }

    @Test
    void retirarTrancaDoSistema_deveRetirarComSucesso_paraAposentar() {
        // Cenário
        RetirarTrancaDTO dto = new RetirarTrancaDTO(100, 1, "funcABC", "APOSENTAR");
        Tranca tranca = new Tranca(1, "Local A", "2020", "Mod1", StatusTranca.LIVRE);
        tranca.setId(1);
        Totem totem = new Totem("Localizacao Totem", "Descricao Totem");
        totem.setId(100);
        
        tranca.setTotemId(totem.getId());

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
        // CORREÇÃO: Verificar totemId, não o objeto totem
        assertNull(tranca.getTotemId());
    }
    
    // ... O resto dos seus testes que não usam a relação com Totem não precisam de alteração ...
}
