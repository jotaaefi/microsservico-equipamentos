package com.equipamento.Controller;


import com.equipamento.dto.TotemRequestDTO;
import com.equipamento.dto.TotemRespostaDTO;
import com.equipamento.dto.TrancaRespostaDTO; // Importe este DTO para a lista de trancas
import com.equipamento.dto.BicicletaRespostaDTO; // Importe este DTO para a lista de bicicletas

import com.equipamento.Entity.Totem; // Model de Totem

import com.equipamento.Service.TotemService; // Seu TotemService
import com.equipamento.mapper.TotemMapper;   // Seu TotemMapper
import com.equipamento.mapper.TrancaMapper;   // Necessário para mapear Tranca para TrancaRespostaDTO
import com.equipamento.mapper.BicicletaMapper; // Necessário para mapear Bicicleta para BicicletaRespostaDTO

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
@RequestMapping("/totem") // Define o caminho base para todos os endpoints neste controller
public class TotemController {

    @Autowired
    private TotemService totemService;
    @Autowired
    private TotemMapper totemMapper;
    @Autowired // Necessário para mapear Tranca para TrancaRespostaDTO
    private TrancaMapper trancaMapper;
    @Autowired // Necessário para mapear Bicicleta para BicicletaRespostaDTO
    private BicicletaMapper bicicletaMapper;

    // Construtor vazio, se for o seu estilo
    public TotemController() {}

    
    @GetMapping
    public ResponseEntity<List<TotemRespostaDTO>> listarTotens() {
        List<Totem> totens = totemService.listarTotens();
        List<TotemRespostaDTO> resposta = totens.stream()
                .map(totemMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resposta);
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<TotemRespostaDTO> buscarTotemPorId(@PathVariable("id") @NotNull Integer id) {
        Optional<Totem> totemOpt = totemService.buscarTotemPorId(id);
        return totemOpt.map(totemMapper::toResponseDTO)
                       .map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.notFound().build());
    }

  
    @PostMapping
    public ResponseEntity<TotemRespostaDTO> criarTotem(@RequestBody @Valid TotemRequestDTO requestDTO) {
        Totem novoTotem = totemService.criarTotem(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(totemMapper.toResponseDTO(novoTotem));
    }

  
    @PutMapping("/{id}")
    public ResponseEntity<TotemRespostaDTO> atualizarTotem(@PathVariable("id") @NotNull Integer id,
                                                           @RequestBody @Valid TotemRequestDTO requestDTO) {
        Optional<Totem> totemAtualizadoOpt = totemService.atualizarTotem(id, requestDTO);
        return totemAtualizadoOpt.map(totemMapper::toResponseDTO)
                                 .map(ResponseEntity::ok)
                                 .orElseGet(() -> ResponseEntity.notFound().build());
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerTotem(@PathVariable("id") @NotNull Integer id) {
        boolean removido = totemService.removerTotem(id);
        // O serviço retorna false se não encontrar ou se não puder remover (ex: possui trancas)
        return removido ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    
    @GetMapping("/{idTotem}/trancas")
    public ResponseEntity<List<TrancaRespostaDTO>> listarTrancasDoTotem(@PathVariable("idTotem") @NotNull Integer idTotem) {
        Optional<Totem> totemOpt = totemService.buscarTotemPorId(idTotem);
        if (totemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Totem totem = totemOpt.get();
        List<TrancaRespostaDTO> trancas = totem.getTrancasNaRede().stream()
                .map(trancaMapper::toResponseDTO) // Mapeia cada Tranca para TrancaRespostaDTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(trancas);
    }

   
    @GetMapping("/{idTotem}/bicicletas")
    public ResponseEntity<List<BicicletaRespostaDTO>> listarBicicletasDoTotem(@PathVariable("idTotem") @NotNull Integer idTotem) {
        Optional<Totem> totemOpt = totemService.buscarTotemPorId(idTotem);
        if (totemOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Totem totem = totemOpt.get();
        // Filtra as trancas que têm bicicletas e mapeia as bicicletas para DTOs
        List<BicicletaRespostaDTO> bicicletas = totem.getTrancasNaRede().stream()
                .filter(tranca -> tranca.getBicicleta() != null) // Apenas trancas com bicicleta
                .map(tranca -> bicicletaMapper.toResponseDTO(tranca.getBicicleta())) // Mapeia a Bicicleta para BicicletaRespostaDTO
                .collect(Collectors.toList());
        return ResponseEntity.ok(bicicletas);
    }
}
