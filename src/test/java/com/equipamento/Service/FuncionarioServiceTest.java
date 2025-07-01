package com.equipamento.Service; // Seu pacote atual para Services e Testes

import com.equipamento.Entity.Funcionario; // Seu modelo Funcionario
import com.equipamento.Entity.FuncaoFuncionario; // Seu enum FuncaoFuncionario

import com.equipamento.Repository.FuncionarioRepository; // Seu repositório Funcionario
import com.equipamento.dto.FuncionarioRequestDTO; // Seu DTO FuncionarioRequestDTO
import com.equipamento.mapper.FuncionarioMapper;

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

class FuncionarioServiceTest {

    @Mock
    private FuncionarioRepository funcionarioRepository;

    @Mock
    private FuncionarioMapper funcionarioMapper;

    @InjectMocks
    private FuncionarioService funcionarioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // -----------------------------------------------------------------------------------
    // Testes para listarFuncionarios() (UC15)
    // -----------------------------------------------------------------------------------
    @Test
    void listarFuncionarios_deveRetornarTodosOsFuncionarios() {
        // Cenário
        Funcionario func1 = new Funcionario("Alice", 30, FuncaoFuncionario.ADMINISTRATIVO, "11122233344", "alice@email.com", "senha123");
        Funcionario func2 = new Funcionario("Bob", 25, FuncaoFuncionario.REPARADOR, "55566677788", "bob@email.com", "senhaabc");
        List<Funcionario> funcionariosMock = Arrays.asList(func1, func2);

        when(funcionarioRepository.findAll()).thenReturn(funcionariosMock);

        // Ação
        List<Funcionario> resultado = funcionarioService.listarFuncionarios();

        // Verificação
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Alice", resultado.get(0).getNome());
        verify(funcionarioRepository, times(1)).findAll();
    }

    // -----------------------------------------------------------------------------------
    // Testes para buscarFuncionarioPorId() (UC15)
    // -----------------------------------------------------------------------------------
    @Test
    void buscarFuncionarioPorId_deveRetornarFuncionario_quandoEncontrado() {
        // Cenário
        Integer id = 1;
        Funcionario funcionarioMock = new Funcionario("Alice", 30, FuncaoFuncionario.ADMINISTRATIVO, "11122233344", "alice@email.com", "senha123");
        funcionarioMock.setId(id);
        when(funcionarioRepository.findById(id)).thenReturn(Optional.of(funcionarioMock));

        // Ação
        Optional<Funcionario> resultado = funcionarioService.buscarFuncionarioPorId(id);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals(funcionarioMock, resultado.get());
        verify(funcionarioRepository, times(1)).findById(id);
    }

    @Test
    void buscarFuncionarioPorId_deveRetornarVazio_quandoNaoEncontrado() {
        // Cenário
        Integer id = 99;
        when(funcionarioRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Funcionario> resultado = funcionarioService.buscarFuncionarioPorId(id);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(funcionarioRepository, times(1)).findById(id);
    }

    // -----------------------------------------------------------------------------------
    // Testes para criarFuncionario() (UC15)
    // -----------------------------------------------------------------------------------
    @Test
    void criarFuncionario_deveSalvarNovoFuncionarioComMatriculaGerada() {
        // Cenário
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura");
        Funcionario funcionarioParaSalvar = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura");
        Funcionario funcionarioSalvo = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura");
        funcionarioSalvo.setId(1);
        funcionarioSalvo.setMatricula("1001");

        when(funcionarioMapper.toEntity(requestDTO)).thenReturn(funcionarioParaSalvar);
        when(funcionarioRepository.findByCpf(anyString())).thenReturn(Optional.empty());
        when(funcionarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionarioSalvo);

        // Ação
        Funcionario resultado = funcionarioService.criarFuncionario(requestDTO);

        // Verificação
        assertNotNull(resultado);
        assertNotNull(resultado.getId());
        assertNotNull(resultado.getMatricula());
        assertEquals("12345678901", resultado.getCpf());
        verify(funcionarioMapper, times(1)).toEntity(requestDTO);
        verify(funcionarioRepository, times(1)).findByCpf(anyString());
        verify(funcionarioRepository, times(1)).findByEmail(anyString());
        verify(funcionarioRepository, times(1)).save(any(Funcionario.class));
    }

    @Test
    void criarFuncionario_deveLancarExcecao_quandoCpfJaCadastrado() {
        // Cenário
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura");
        Funcionario funcionarioParaSalvar = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura"); // Mapeado do DTO

        when(funcionarioMapper.toEntity(requestDTO)).thenReturn(funcionarioParaSalvar);
        when(funcionarioRepository.findByCpf("12345678901")).thenReturn(Optional.of(funcionarioParaSalvar)); // CPF já existe
        
        // Ação e Verificação
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.criarFuncionario(requestDTO);
        });

        assertEquals("CPF já cadastrado.", thrown.getMessage());
        verify(funcionarioRepository, times(1)).findByCpf("12345678901");
        verify(funcionarioRepository, never()).findByEmail(anyString());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void criarFuncionario_deveLancarExcecao_quandoEmailJaCadastrado() {
        // Cenário
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura");
        Funcionario funcionarioParaSalvar = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senhasegura"); // Mapeado do DTO

        when(funcionarioMapper.toEntity(requestDTO)).thenReturn(funcionarioParaSalvar);
        when(funcionarioRepository.findByCpf("12345678901")).thenReturn(Optional.empty());
        when(funcionarioRepository.findByEmail("carlos@email.com")).thenReturn(Optional.of(funcionarioParaSalvar)); // Email já existe

        // Ação e Verificação
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.criarFuncionario(requestDTO);
        });

        assertEquals("E-mail já cadastrado.", thrown.getMessage());
        verify(funcionarioRepository, times(1)).findByCpf("12345678901");
        verify(funcionarioRepository, times(1)).findByEmail("carlos@email.com");
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para atualizarFuncionario() (UC15)
    // -----------------------------------------------------------------------------------
    @Test
    void atualizarFuncionario_deveAtualizarDadosDoFuncionario_quandoValido() {
        // Cenário
        Integer id = 1;
        // CORREÇÃO: Mudar o CPF e Email no requestDTO para que a lógica de verificação seja acionada
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos Atualizado", 36, FuncaoFuncionario.ADMINISTRATIVO, "98765432109", "carlos.novo@email.com", "nova_senha");
        
        Funcionario funcionarioExistente = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senha_antiga");
        funcionarioExistente.setId(id);
        funcionarioExistente.setMatricula("F001");

        when(funcionarioRepository.findById(id)).thenReturn(Optional.of(funcionarioExistente));
        when(funcionarioRepository.findByCpf("98765432109")).thenReturn(Optional.empty()); // Novo CPF válido
        when(funcionarioRepository.findByEmail("carlos.novo@email.com")).thenReturn(Optional.empty()); // Novo Email válido
        when(funcionarioRepository.save(any(Funcionario.class))).thenReturn(funcionarioExistente);

        // Ação
        Optional<Funcionario> resultado = funcionarioService.atualizarFuncionario(id, requestDTO);

        // Verificação
        assertTrue(resultado.isPresent());
        assertEquals("Carlos Atualizado", resultado.get().getNome());
        assertEquals(36, resultado.get().getIdade());
        assertEquals(FuncaoFuncionario.ADMINISTRATIVO, resultado.get().getFuncao());
        assertEquals("98765432109", resultado.get().getCpf()); // CPF deve ter sido atualizado
        assertEquals("carlos.novo@email.com", resultado.get().getEmail()); // Email deve ter sido atualizado
        assertEquals("nova_senha", resultado.get().getSenha());
        assertEquals("F001", resultado.get().getMatricula());

        verify(funcionarioRepository, times(1)).findById(id);
        verify(funcionarioRepository, times(1)).findByCpf("98765432109"); // Agora esperado ser chamado
        verify(funcionarioRepository, times(1)).findByEmail("carlos.novo@email.com"); // Agora esperado ser chamado
        verify(funcionarioRepository, times(1)).save(funcionarioExistente);
    }

    @Test
    void atualizarFuncionario_deveLancarExcecao_quandoNovoCpfJaCadastrado() {
        // Cenário
        Integer id = 1;
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos", 35, FuncaoFuncionario.REPARADOR, "novo_cpf_diferente", "carlos@email.com", "senha"); // CPF diferente para acionar a verificação
        Funcionario funcionarioExistente = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "cpf_original", "carlos@email.com", "senha");
        funcionarioExistente.setId(id);

        Funcionario outroFuncionarioComCpf = new Funcionario("Outro", 40, FuncaoFuncionario.ADMINISTRATIVO, "novo_cpf_diferente", "outro@email.com", "senha");
        
        when(funcionarioRepository.findById(id)).thenReturn(Optional.of(funcionarioExistente));
        when(funcionarioRepository.findByCpf("novo_cpf_diferente")).thenReturn(Optional.of(outroFuncionarioComCpf)); // Novo CPF já existe
        
        // Ação e Verificação
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.atualizarFuncionario(id, requestDTO);
        });

        assertEquals("Novo CPF já cadastrado para outro funcionário.", thrown.getMessage());
        verify(funcionarioRepository, times(1)).findById(id);
        verify(funcionarioRepository, times(1)).findByCpf("novo_cpf_diferente"); // Agora esperado ser chamado
        verify(funcionarioRepository, never()).findByEmail(anyString()); // Email não deve ser verificado se o CPF falha primeiro
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizarFuncionario_deveLancarExcecao_quandoNovoEmailJaCadastrado() {
        // Cenário
        Integer id = 1;
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "novo.email@email.com", "senha");
        Funcionario funcionarioExistente = new Funcionario("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "email_original@email.com", "senha");
        funcionarioExistente.setId(id);

        Funcionario outroFuncionarioComEmail = new Funcionario("Outro", 40, FuncaoFuncionario.ADMINISTRATIVO, "99988877766", "novo.email@email.com", "senha");
        
        when(funcionarioRepository.findById(id)).thenReturn(Optional.of(funcionarioExistente));
        // CPF não mudou, então findByCpf não será chamado:
        // when(funcionarioRepository.findByCpf("12345678901")).thenReturn(Optional.empty()); // REMOVIDO para que o verify never funcione.
        when(funcionarioRepository.findByEmail("novo.email@email.com")).thenReturn(Optional.of(outroFuncionarioComEmail));

        // Ação e Verificação
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            funcionarioService.atualizarFuncionario(id, requestDTO);
        });

        assertEquals("Novo e-mail já cadastrado para outro funcionário.", thrown.getMessage());
        verify(funcionarioRepository, times(1)).findById(id);
        verify(funcionarioRepository, never()).findByCpf(anyString()); // CORREÇÃO: findByCpf NUNCA deve ser invocado
        verify(funcionarioRepository, times(1)).findByEmail("novo.email@email.com");
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    @Test
    void atualizarFuncionario_deveRetornarVazio_quandoNaoEncontrado() {
        // Cenário
        Integer id = 99;
        FuncionarioRequestDTO requestDTO = new FuncionarioRequestDTO("Carlos", 35, FuncaoFuncionario.REPARADOR, "12345678901", "carlos@email.com", "senha");
        when(funcionarioRepository.findById(id)).thenReturn(Optional.empty());

        // Ação
        Optional<Funcionario> resultado = funcionarioService.atualizarFuncionario(id, requestDTO);

        // Verificação
        assertTrue(resultado.isEmpty());
        verify(funcionarioRepository, times(1)).findById(id);
        verify(funcionarioRepository, never()).findByCpf(anyString()); // Nao deve verificar CPF/Email se funcionario nao encontrado
        verify(funcionarioRepository, never()).findByEmail(anyString());
        verify(funcionarioRepository, never()).save(any(Funcionario.class));
    }

    // -----------------------------------------------------------------------------------
    // Testes para removerFuncionario() (UC15)
    // -----------------------------------------------------------------------------------
    @Test
    void removerFuncionario_deveRemoverComSucesso_quandoEncontrado() {
        // Cenário
        Integer id = 1;
        when(funcionarioRepository.existsById(id)).thenReturn(true);
        doNothing().when(funcionarioRepository).deleteById(id);

        // Ação
        boolean resultado = funcionarioService.removerFuncionario(id);

        // Verificação
        assertTrue(resultado);
        verify(funcionarioRepository, times(1)).existsById(id);
        verify(funcionarioRepository, times(1)).deleteById(id);
    }

    @Test
    void removerFuncionario_deveRetornarFalse_quandoNaoEncontrado() {
        // Cenário
        Integer id = 99;
        when(funcionarioRepository.existsById(id)).thenReturn(false);

        // Ação
        boolean resultado = funcionarioService.removerFuncionario(id);

        // Verificação
        assertFalse(resultado);
        verify(funcionarioRepository, times(1)).existsById(id);
        verify(funcionarioRepository, never()).deleteById(anyInt());
    }

    // -----------------------------------------------------------------------------------
    // Testes para verificarFuncionarioExiste()
    // -----------------------------------------------------------------------------------
    @Test
    void verificarFuncionarioExiste_deveRetornarTrue_comComportamentoFalso() {
        // Cenário (Comportamento falso sempre retorna true)
        String idFuncionario = "qualquerId";

        // Ação
        boolean resultado = funcionarioService.verificarFuncionarioExiste(idFuncionario);

        // Verificação
        assertTrue(resultado);
        // Este teste verifica o comportamento falso. Não há chamadas a mocks externos aqui.
    }
}