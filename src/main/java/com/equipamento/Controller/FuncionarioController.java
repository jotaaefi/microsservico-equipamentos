package com.equipamento.Controller; // Adapte para o seu pacote 'Controller'

import com.equipamento.dto.FuncionarioRequestDTO;
import com.equipamento.dto.FuncionarioRespostaDTO;
import com.equipamento.Entity.Funcionario; // Model de Funcionario

import com.equipamento.Service.FuncionarioService; // Seu FuncionarioService
import com.equipamento.mapper.FuncionarioMapper;   // Seu FuncionarioMapper

import jakarta.validation.Valid; // Para validação dos DTOs
import jakarta.validation.constraints.NotNull; // Para PathVariables obrigatórias

import org.springframework.http.HttpStatus; // Para retornar diferentes códigos HTTP
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Anotações REST

import java.util.List;
import java.util.Optional;


@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/funcionario") // Define o caminho base para todos os endpoints neste controller
public class FuncionarioController {


    private final FuncionarioService funcionarioService;
    private final FuncionarioMapper funcionarioMapper;

   
    public FuncionarioController(FuncionarioService funcionarioService,
                                 FuncionarioMapper funcionarioMapper) {
        this.funcionarioService = funcionarioService;
        this.funcionarioMapper = funcionarioMapper;
    }
   
    @GetMapping
        public ResponseEntity<List<FuncionarioRespostaDTO>> listarFuncionarios() {
        List<Funcionario> funcionarios = funcionarioService.listarFuncionarios();
        List<FuncionarioRespostaDTO> resposta = funcionarios.stream()
                .map(funcionarioMapper::toResponseDTO)
                .toList(); 

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioRespostaDTO> buscarFuncionarioPorId(@PathVariable("id") @NotNull Integer id) {
        Optional<Funcionario> funcionarioOpt = funcionarioService.buscarFuncionarioPorId(id);
        return funcionarioOpt.map(funcionarioMapper::toResponseDTO)
                             .map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping
    public ResponseEntity<FuncionarioRespostaDTO> criarFuncionario(@RequestBody @Valid FuncionarioRequestDTO requestDTO) {
    try {
        Funcionario novoFuncionario = funcionarioService.criarFuncionario(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioMapper.toResponseDTO(novoFuncionario));
    } catch (IllegalArgumentException e) {
        // RETORNE ResponseEntity<String> em vez de FuncionarioRespostaDTO com nulls
        return ResponseEntity.badRequest().build(); 
    }
}

    @PutMapping("/{id}")
public ResponseEntity<FuncionarioRespostaDTO> atualizarFuncionario(@PathVariable("id") @NotNull Integer id,
                                                                   @RequestBody @Valid FuncionarioRequestDTO requestDTO) {
    try {
        Optional<Funcionario> funcionarioAtualizadoOpt = funcionarioService.atualizarFuncionario(id, requestDTO);
        return funcionarioAtualizadoOpt.map(funcionarioMapper::toResponseDTO)
                                     .map(ResponseEntity::ok)
                                     .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    } catch (IllegalArgumentException e) {
        // RETORNE ResponseEntity<String> em vez de FuncionarioRespostaDTO com nulls
        return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request com a mensagem de erro
    }
}

    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerFuncionario(@PathVariable("id") @NotNull Integer id) {
        boolean removido = funcionarioService.removerFuncionario(id);
        return removido ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

   
    @GetMapping("/existe")
    public ResponseEntity<Boolean> verificarFuncionarioExiste(@RequestParam("id") String id) {
        boolean existe = funcionarioService.verificarFuncionarioExiste(); //Comportamento falso
        return ResponseEntity.ok(existe);
    }
}