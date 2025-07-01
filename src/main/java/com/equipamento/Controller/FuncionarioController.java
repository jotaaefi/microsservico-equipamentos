package com.equipamento.Controller; // Adapte para o seu pacote 'Controller'

import com.equipamento.dto.FuncionarioRequestDTO;
import com.equipamento.dto.FuncionarioRespostaDTO;
import com.equipamento.Entity.Funcionario; // Model de Funcionario

import com.equipamento.Service.FuncionarioService; // Seu FuncionarioService
import com.equipamento.mapper.FuncionarioMapper;   // Seu FuncionarioMapper

import jakarta.validation.Valid; // Para validação dos DTOs
import jakarta.validation.constraints.NotNull; // Para PathVariables obrigatórias

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Para retornar diferentes códigos HTTP
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Anotações REST

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController // Indica que esta classe é um controlador REST
@RequestMapping("/funcionario") // Define o caminho base para todos os endpoints neste controller
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;
    @Autowired
    private FuncionarioMapper funcionarioMapper;

    // Construtor vazio, se for o seu estilo
    public FuncionarioController() {}

    /**
     * Lista todos os funcionários.
     * GET /funcionario
     * @return Lista de FuncionarioRespostaDTO.
     */
    @GetMapping
    public ResponseEntity<List<FuncionarioRespostaDTO>> listarFuncionarios() {
        List<Funcionario> funcionarios = funcionarioService.listarFuncionarios();
        List<FuncionarioRespostaDTO> resposta = funcionarios.stream()
                .map(funcionarioMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resposta);
    }

    /**
     * Busca um funcionário por ID.
     * GET /funcionario/{id}
     * @param id ID do funcionário.
     * @return FuncionarioRespostaDTO ou 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FuncionarioRespostaDTO> buscarFuncionarioPorId(@PathVariable("id") @NotNull Integer id) {
        Optional<Funcionario> funcionarioOpt = funcionarioService.buscarFuncionarioPorId(id);
        return funcionarioOpt.map(funcionarioMapper::toResponseDTO)
                             .map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo funcionário.
     * POST /funcionario
     * @param requestDTO Dados do funcionário a ser criado.
     * @return FuncionarioRespostaDTO do funcionário criado.
     */
    @PostMapping
    public ResponseEntity<FuncionarioRespostaDTO> criarFuncionario(@RequestBody @Valid FuncionarioRequestDTO requestDTO) {
        try {
            Funcionario novoFuncionario = funcionarioService.criarFuncionario(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(funcionarioMapper.toResponseDTO(novoFuncionario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new FuncionarioRespostaDTO(
                null, null, e.getMessage(), null, null, null, null // Retorna erro com mensagem (simplificado)
            ));
        }
    }

    /**
     * Atualiza dados de um funcionário existente.
     * PUT /funcionario/{id}
     * @param id ID do funcionário.
     * @param requestDTO Novos dados do funcionário.
     * @return FuncionarioRespostaDTO do funcionário atualizado ou 404 Not Found / 400 Bad Request.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FuncionarioRespostaDTO> atualizarFuncionario(@PathVariable("id") @NotNull Integer id,
                                                                       @RequestBody @Valid FuncionarioRequestDTO requestDTO) {
        try {
            Optional<Funcionario> funcionarioAtualizadoOpt = funcionarioService.atualizarFuncionario(id, requestDTO);
            return funcionarioAtualizadoOpt.map(funcionarioMapper::toResponseDTO)
                                         .map(ResponseEntity::ok)
                                         .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new FuncionarioRespostaDTO(
                null, null, e.getMessage(), null, null, null, null // Retorna erro com mensagem (simplificado)
            ));
        }
    }

    /**
     * Remove (exclui) um funcionário.
     * DELETE /funcionario/{id}
     * @param id ID do funcionário a ser removido.
     * @return 200 OK se removido, 404 Not Found se não encontrado.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerFuncionario(@PathVariable("id") @NotNull Integer id) {
        boolean removido = funcionarioService.removerFuncionario(id);
        return removido ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    /**
     * Verifica se um funcionário existe em um sistema externo (ou mockado).
     * GET /funcionario/existe?id={idFuncionario}
     * Endpoint do seu FuncionarioController original.
     * @param id ID ou matrícula do funcionário.
     * @return true ou false.
     */
    @GetMapping("/existe")
    public ResponseEntity<Boolean> verificarFuncionarioExiste(@RequestParam("id") String id) {
        boolean existe = funcionarioService.verificarFuncionarioExiste(id);
        return ResponseEntity.ok(existe);
    }
}