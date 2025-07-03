package com.equipamento.controller;


import com.equipamento.dto.TotemRequestDTO;
import com.equipamento.dto.TotemRespostaDTO;
import com.equipamento.dto.TrancaRespostaDTO; 
import com.equipamento.dto.BicicletaRespostaDTO; 

import com.equipamento.entity.Totem; 

import com.equipamento.service.TotemService; 
import com.equipamento.mapper.TotemMapper;   
import com.equipamento.mapper.TrancaMapper;   
import com.equipamento.mapper.BicicletaMapper; 

import jakarta.validation.Valid; 
import jakarta.validation.constraints.NotNull; 


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; 

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController 
@RequestMapping("/totem")
public class TotemController {


    private final TotemService totemService;
    private final TotemMapper totemMapper;
    private final TrancaMapper trancaMapper;
    private final BicicletaMapper bicicletaMapper;

    
    public TotemController(TotemService totemService,
                           TotemMapper totemMapper,
                           TrancaMapper trancaMapper,
                           BicicletaMapper bicicletaMapper) {
        this.totemService = totemService;
        this.totemMapper = totemMapper;
        this.trancaMapper = trancaMapper;
        this.bicicletaMapper = bicicletaMapper;
    }
    
   @GetMapping
        public ResponseEntity<List<TotemRespostaDTO>> listarTotens() {
        List<Totem> totens = totemService.listarTotens();
        List<TotemRespostaDTO> resposta = totens.stream()
                .map(totemMapper::toResponseDTO)
                .toList(); 

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
        return ResponseEntity.ok(totemMapper.toResponseDTO(novoTotem));
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
        if(removido == true)
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.badRequest().build();
        //  return removido ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
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
