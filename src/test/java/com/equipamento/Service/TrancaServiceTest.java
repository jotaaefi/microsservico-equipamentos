package com.equipamento.Service; 
import com.equipamento.Entity.Tranca;    
import com.equipamento.Entity.StatusTranca; 
import com.equipamento.Entity.Bicicleta; 
import com.equipamento.Entity.StatusBicicleta; 
import com.equipamento.Entity.Totem;     

import com.equipamento.Repository.TrancaRepository;
import com.equipamento.dto.IdBicicletaDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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

    @Mock
    private BicicletaService bicicletaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    
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
        
        assertEquals(totem.getId(), tranca.getTotemId()); 
        verify(trancaRepository, times(1)).save(tranca);
        verify(totemService, times(1)).salvarTotem(totem);
    }

    @Test
    void integrarTrancaEmTotem_deveRetornarErro_quandoFuncionarioNaoExiste() {
        IntegrarTrancaDTO dto = new IntegrarTrancaDTO(100, 1, "funcInvalido");
        
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

        //Mockou(isolou) todos esses when. Retornando true, a tranca(id1), totemID100 e salvando a tranca e os totem.
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
    
    @Test
    void trancar_deveTrancarBicicleta_quandoValido() {
        // Arrange (Cenário)
        Integer idTranca = 1;
        Integer idBicicleta = 100;
        IdBicicletaDTO dto = new IdBicicletaDTO(idBicicleta);

        // Criamos uma tranca LIVRE e uma bicicleta EM_USO, que são as pré-condições
        Tranca trancaLivre = new Tranca(1, "Local", "2023", "ModX", StatusTranca.LIVRE);
        Bicicleta bicicletaEmUso = new Bicicleta("Marca", "Modelo", "2023", 1, StatusBicicleta.EM_USO);
        
        when(trancaRepository.findById(idTranca)).thenReturn(Optional.of(trancaLivre));
        when(bicicletaService.buscarBicicletaPorId(idBicicleta)).thenReturn(Optional.of(bicicletaEmUso));
        // Mockamos a chamada para salvar a tranca
        when(trancaRepository.save(any(Tranca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        Optional<Tranca> resultado = trancaService.trancar(idTranca, dto);

        // Assert (Verificação)
        assertTrue(resultado.isPresent());
        Tranca trancaAtualizada = resultado.get();
        assertEquals(StatusTranca.OCUPADA, trancaAtualizada.getStatusTranca());
        assertEquals(StatusBicicleta.DISPONIVEL, trancaAtualizada.getBicicleta().getStatus());
        assertNotNull(trancaAtualizada.getBicicleta());
        verify(trancaRepository, times(1)).save(trancaAtualizada);
    }

    @Test
    void trancar_deveLancarExcecao_quandoTrancaNaoEstaLivre() {
        // Arrange
        Integer idTranca = 1;
        IdBicicletaDTO dto = new IdBicicletaDTO(100);
        Tranca trancaOcupada = new Tranca(1, "Local", "2023", "ModX", StatusTranca.OCUPADA); // Status inválido
        Bicicleta bicicleta = new Bicicleta();


        //Mockou(isolou) o findBy Id para tranca estar ocupada e o id da bike tb
        when(trancaRepository.findById(idTranca)).thenReturn(Optional.of(trancaOcupada));
        when(bicicletaService.buscarBicicletaPorId(100)).thenReturn(Optional.of(bicicleta));
        
        // Act & Assert
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            trancaService.trancar(idTranca, dto);
        });
        
        assertEquals("Tranca não está livre para receber uma bicicleta.", thrown.getMessage());
    }

    // Testes para o método destrancar()
    @Test
    void destrancar_deveDestrancarBicicleta_quandoValido() {
        // Arrange
        Integer idTranca = 1;
        Bicicleta bicicletaNaTranca = new Bicicleta("Marca", "Modelo", "2023", 1, StatusBicicleta.DISPONIVEL);
        Tranca trancaOcupada = new Tranca(1, "Local", "2023", "ModX", StatusTranca.OCUPADA, bicicletaNaTranca);


        //Mockou(isolou) o findById para tranca ocupada e o save para salvar tranca
        when(trancaRepository.findById(idTranca)).thenReturn(Optional.of(trancaOcupada));
        when(trancaRepository.save(any(Tranca.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<Tranca> resultado = trancaService.destrancar(idTranca);

        // Assert
        assertTrue(resultado.isPresent());
        Tranca trancaAtualizada = resultado.get();
        assertEquals(StatusTranca.LIVRE, trancaAtualizada.getStatusTranca());
        assertEquals(StatusBicicleta.EM_USO, bicicletaNaTranca.getStatus());
        assertNull(trancaAtualizada.getBicicleta());
        verify(trancaRepository, times(1)).save(trancaAtualizada);

        //resultado é detrancar se tiver valido né
    }

    // Testes para o método getBicicletaDeTranca()
    @Test
    void getBicicletaDeTranca_deveRetornarBicicleta_quandoExiste() {
        // Arrange
        Integer idTranca = 1;
        Bicicleta bicicleta = new Bicicleta();
        Tranca trancaComBicicleta = new Tranca();
        trancaComBicicleta.setBicicleta(bicicleta);
        
        //Mockou(isolou) o find byID e retornou uma bike com tranca
        when(trancaRepository.findById(idTranca)).thenReturn(Optional.of(trancaComBicicleta));

        // Act
        Optional<Bicicleta> resultado = trancaService.getBicicletaDeTranca(idTranca);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(bicicleta, resultado.get());

        //resultado esperado é uma bike kkkkkkk.
    }

    @Test
    void getBicicletaDeTranca_deveRetornarVazio_quandoNaoExisteBicicleta() {
        // Arrange
        Integer idTranca = 1;
        Tranca trancaSemBicicleta = new Tranca(); // Bicicleta é nula por padrão
        
        //Mockou(isolou) a funcao byId e retornou a bike sem ID
        when(trancaRepository.findById(idTranca)).thenReturn(Optional.of(trancaSemBicicleta));

        // Act
        Optional<Bicicleta> resultado = trancaService.getBicicletaDeTranca(idTranca);

        // Assert
        assertTrue(resultado.isEmpty());

        //Resultado esperado é vazio.
    }

    @Test
    void getBicicletaDeTranca_deveRetornarVazio_quandoTrancaNaoExiste() {
        // Arrange
        Integer idTranca = 99;
        //Mockou(isolou) a funcao byId e retornou vazio
        when(trancaRepository.findById(idTranca)).thenReturn(Optional.empty());

        // Act
        Optional<Bicicleta> resultado = trancaService.getBicicletaDeTranca(idTranca);

        // Assert
        assertTrue(resultado.isEmpty());

        //Resultado esperado é vazio.
    }
}
