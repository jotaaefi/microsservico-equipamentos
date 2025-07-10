package com.equipamento.Controller; 

import java.util.List;
import java.util.Optional;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping; 
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.Service.BicicletaService;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.BicicletaRespostaDTO;
import com.equipamento.dto.IntegrarBicicletaDTO; 
import com.equipamento.dto.RetirarBicicletaDTO; 

import com.equipamento.mapper.BicicletaMapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/bicicleta")
public class BicicletaController {
    
    
    private final BicicletaService bicicletaService;
    private final BicicletaMapper bicicletaMapper;


    public BicicletaController(BicicletaService bicicletaService,
                               BicicletaMapper bicicletaMapper) {
        this.bicicletaService = bicicletaService;
        this.bicicletaMapper = bicicletaMapper;
    }



    @GetMapping
    public ResponseEntity<List<BicicletaRespostaDTO>> listarBicicletas() {
        List<Bicicleta> bicicletas = bicicletaService.listarBicicletas();
        List<BicicletaRespostaDTO> resposta = bicicletas.stream().map(bicicletaMapper::toResponseDTO).toList();
                
        return ResponseEntity.ok(resposta); 
    }

    @GetMapping("/{id}")
    public ResponseEntity<BicicletaRespostaDTO> buscarBicicletaPorId(@PathVariable("id") @NotNull Integer id) {
        Optional<Bicicleta> bicicletaOpt = bicicletaService.buscarBicicletaPorId(id);
        return bicicletaOpt.map(bicicletaMapper::toResponseDTO)
                           .map(ResponseEntity::ok)
                           .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<BicicletaRespostaDTO> criarBicicleta(@RequestBody @Valid BicicletaRequestDTO requestDTO) {
        Bicicleta novaBicicleta = bicicletaService.criarBicicleta(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(bicicletaMapper.toResponseDTO(novaBicicleta));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BicicletaRespostaDTO> atualizarBicicleta(@PathVariable("id") @NotNull Integer id,
                                                                   @RequestBody @Valid BicicletaRequestDTO requestDTO) {
        Optional<Bicicleta> bicicletaAtualizadaOpt = bicicletaService.atualizarBicicleta(id, requestDTO);
        return bicicletaAtualizadaOpt.map(bicicletaMapper::toResponseDTO)
                                     .map(ResponseEntity::ok)
                                     .orElseGet(() -> ResponseEntity.notFound().build());
    }



   

    @DeleteMapping("/{id}") 
    public ResponseEntity<Void> removerBicicleta(@PathVariable("id") @NotNull Integer id) {
    boolean removida = bicicletaService.removerBicicleta(id);
    return removida ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    

    @PostMapping("/integrarNaRede")
    public ResponseEntity<String> integrarBicicletaNaRede(@RequestBody @Valid IntegrarBicicletaDTO dto) {
        String resultado = bicicletaService.integrarBicicletaNaRede(dto);
        if (resultado.contains("sucesso")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resultado);
        }
    }

    @PostMapping("/retirarDaRede")
    public ResponseEntity<String> retirarBicicletaDaRede(@RequestBody @Valid RetirarBicicletaDTO dto) {
        String resultado = bicicletaService.retirarBicicletaDaRede(dto);
        if (resultado.contains("sucesso")) {
            return ResponseEntity.ok(resultado);
        } else {
             return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resultado);
        }
    }

    @PostMapping("/{idBicicleta}/status/{acao}")
    public ResponseEntity<BicicletaRespostaDTO> atualizarStatusBicicleta(@PathVariable("idBicicleta") @NotNull Integer idBicicleta,
                                                                         @PathVariable("acao") @NotNull String acao) {
        StatusBicicleta novoStatus;
        try {
            novoStatus = StatusBicicleta.valueOf(acao.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Bicicleta> bicicletaAtualizadaOpt = bicicletaService.atualizarStatusBicicleta(idBicicleta, novoStatus);
        return bicicletaAtualizadaOpt.map(bicicletaMapper::toResponseDTO)
                                     .map(ResponseEntity::ok)
                                     .orElseGet(() -> ResponseEntity.notFound().build());
    }
}