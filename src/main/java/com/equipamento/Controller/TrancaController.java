package com.equipamento.Controller; // Adapte para o seu pacote 'Controller'

import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import com.equipamento.dto.IntegrarTrancaDTO; // Importe este DTO
import com.equipamento.dto.RetirarTrancaDTO; // Importe este DTO
import com.equipamento.Entity.Tranca; // Model de Tranca
import com.equipamento.Entity.StatusTranca; // Enum de StatusTranca

import com.equipamento.Service.TrancaService; // Seu TrancaService

import com.equipamento.mapper.TrancaMapper;   // Seu TrancaMapper
// import com.equipamento.mapper.BicicletaMapper; // Se precisar mapear Bicicleta para BicicletaRespostaDTO diretamente aqui

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
@RequestMapping("/tranca") // Define o caminho base para todos os endpoints neste controller
public class TrancaController {

    @Autowired
    private TrancaService trancaService;
    @Autowired
    private TrancaMapper trancaMapper;
   
    public TrancaController() {}

    @GetMapping
    public ResponseEntity<List<TrancaRespostaDTO>> listarTrancas() {
        List<Tranca> trancas = trancaService.listarTrancas();
        List<TrancaRespostaDTO> resposta = trancas.stream()
                .map(trancaMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resposta);
    }

 
    @GetMapping("/{id}")
    public ResponseEntity<TrancaRespostaDTO> buscarTrancaPorId(@PathVariable("id") @NotNull Integer id) {
        Optional<Tranca> trancaOpt = trancaService.buscarTrancaPorId(id);
        return trancaOpt.map(trancaMapper::toResponseDTO)
                        .map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    
    @PostMapping
    public ResponseEntity<TrancaRespostaDTO> criarTranca(@RequestBody @Valid TrancaRequestDTO requestDTO) {
        Tranca novaTranca = trancaService.criarTranca(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(trancaMapper.toResponseDTO(novaTranca));
    }

 
    @PutMapping("/{id}")
    public ResponseEntity<TrancaRespostaDTO> atualizarTranca(@PathVariable("id") @NotNull Integer id,
                                                              @RequestBody @Valid TrancaRequestDTO requestDTO) {
        Optional<Tranca> trancaAtualizadaOpt = trancaService.atualizarTranca(id, requestDTO);
        return trancaAtualizadaOpt.map(trancaMapper::toResponseDTO)
                                  .map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerTranca(@PathVariable("id") @NotNull Integer id) {
        boolean removida = trancaService.removerTranca(id);
        return removida ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    
    @PostMapping("/integrarNaRede")
    public ResponseEntity<String> integrarTrancaNaRede(@RequestBody @Valid IntegrarTrancaDTO dto) {
        String resultado = trancaService.integrarTrancaEmTotem(dto);
        if (resultado.contains("sucesso")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }

   
    @PostMapping("/retirarDaRede")
    public ResponseEntity<String> retirarTrancaDaRede(@RequestBody @Valid RetirarTrancaDTO dto) {
        String resultado = trancaService.retirarTrancaDoSistema(dto);
        if (resultado.contains("sucesso")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }

   
 
    @PostMapping("/{idTranca}/status/{acao}") // Endpoint da URL
    public ResponseEntity<TrancaRespostaDTO> atualizarStatusTranca(@PathVariable @NotNull Integer idTranca, 
            @PathVariable @NotNull String acao) {  
        
        
        StatusTranca novoStatus;
        try {
            novoStatus = StatusTranca.valueOf(acao.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); // Ação de status inválida
        }

        Optional<Tranca> trancaAtualizadaOpt = trancaService.atualizarStatusTranca(idTranca, novoStatus);
        return trancaAtualizadaOpt.map(trancaMapper::toResponseDTO)
                                  .map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
    }
}