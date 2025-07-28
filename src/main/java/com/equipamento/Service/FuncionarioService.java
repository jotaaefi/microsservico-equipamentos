package com.equipamento.Service; // Seu pacote atual

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.transaction.annotation.Transactional;



import org.springframework.stereotype.Service; 



import com.equipamento.Entity.Funcionario; 
import com.equipamento.Repository.FuncionarioRepository; 
import com.equipamento.mapper.FuncionarioMapper;
import com.equipamento.dto.FuncionarioRequestDTO; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class FuncionarioService {

    private static final Logger logger = LoggerFactory.getLogger(FuncionarioService.class);


    private FuncionarioRepository funcionarioRepository;
    private FuncionarioMapper funcionarioMapper;
    private static final AtomicInteger matriculaCounter = new AtomicInteger(1000);


    public FuncionarioService(FuncionarioRepository funcionarioRepository, FuncionarioMapper funcionarioMapper) {
        this.funcionarioMapper = funcionarioMapper;
        this.funcionarioRepository = funcionarioRepository;
    }

    
    public List<Funcionario> listarFuncionarios() {
        return funcionarioRepository.findAll();
    }

   
    public Optional<Funcionario> buscarFuncionarioPorId(Integer id) {
        return funcionarioRepository.findById(id);
    }

    /**
     * Cria um novo funcionário no sistema (UC15).
     * A matrícula é gerada automaticamente (Regra R2 UC15).
     * parametro -> requestDTO Dados do funcionário a ser criado.
     * retorna O funcionário criado e salvo no banco de dados.
     */
    @Transactional
    public Funcionario criarFuncionario(FuncionarioRequestDTO requestDTO) {
        Funcionario novoFuncionario = funcionarioMapper.toEntity(requestDTO);
        novoFuncionario.setMatricula(String.valueOf(matriculaCounter.incrementAndGet()));

        if (funcionarioRepository.findByCpf(novoFuncionario.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        if (funcionarioRepository.findByEmail(novoFuncionario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }

        return funcionarioRepository.save(novoFuncionario);
    }

   
    @Transactional
    public Optional<Funcionario> atualizarFuncionario(Integer id, FuncionarioRequestDTO requestDTO) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(id);

        if (funcionarioOpt.isEmpty()) {
            return Optional.empty(); // Funcionário não encontrado
        }

        Funcionario funcionarioExistente = funcionarioOpt.get();

        funcionarioExistente.setNome(requestDTO.nome());
        funcionarioExistente.setIdade(requestDTO.idade());
        funcionarioExistente.setFuncao(requestDTO.funcao());
        
        if (!funcionarioExistente.getCpf().equals(requestDTO.cpf()) && funcionarioRepository.findByCpf(requestDTO.cpf()).isPresent()) {
             throw new IllegalArgumentException("Novo CPF já cadastrado para outro funcionário.");
        }
        if (!funcionarioExistente.getEmail().equals(requestDTO.email()) && funcionarioRepository.findByEmail(requestDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Novo e-mail já cadastrado para outro funcionário.");
        }

        funcionarioExistente.setCpf(requestDTO.cpf());
        funcionarioExistente.setEmail(requestDTO.email());
        
        funcionarioExistente.setSenha(requestDTO.senha());

        return Optional.of(funcionarioRepository.save(funcionarioExistente));
    }

    /**
     * Remove (exclui) um funcionário do sistema (UC15).
     *  id O ID do funcionário a ser removido.
     * retorna true se o funcionário foi removido com sucesso, false caso contrário.
     */
    @Transactional
    public boolean removerFuncionario(Integer id) {
        if (funcionarioRepository.existsById(id)) {
            funcionarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

  
    public boolean verificarFuncionarioExiste() {
        logger.debug("Verificando funcionário externo (comportamento falso)."); 
        return true; // SIMULA que qualquer funcionário existe por padrão
    }
  
}