package com.equipamento.controller;

import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import com.equipamento.dto.IntegrarTrancaDTO; 
import com.equipamento.dto.RetirarTrancaDTO; 
import com.equipamento.entity.Tranca; 
import com.equipamento.entity.StatusTranca; 

import com.equipamento.service.TrancaService; 

import com.equipamento.mapper.TrancaMapper;   

import jakarta.validation.Valid; 
import jakarta.validation.constraints.NotNull; 


import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 
import java.util.List;
import java.util.Optional;


@RestController 
@RequestMapping("/tranca") 
public class TrancaController {

    private final TrancaService trancaService;
 
    private final TrancaMapper trancaMapper;
   
    public TrancaController(TrancaService trancaService, TrancaMapper trancaMapper) {
        this.trancaMapper = trancaMapper;
        this.trancaService = trancaService;
    }

    @GetMapping
    public ResponseEntity<List<TrancaRespostaDTO>> listarTrancas() {
    List<Tranca> trancas = trancaService.listarTrancas();
    List<TrancaRespostaDTO> resposta = trancas.stream()
            .map(trancaMapper::toResponseDTO)
            .toList(); 
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