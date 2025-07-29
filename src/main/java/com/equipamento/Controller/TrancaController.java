package com.equipamento.Controller;

import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import com.equipamento.dto.BicicletaRespostaDTO;
import com.equipamento.dto.ErrouDTO;
import com.equipamento.dto.IdBicicletaDTO;
import com.equipamento.dto.IntegrarTrancaDTO; 
import com.equipamento.dto.RetirarTrancaDTO; 
import com.equipamento.Entity.Tranca;
import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusTranca; 

import com.equipamento.Service.TrancaService;
import com.equipamento.mapper.BicicletaMapper;
import com.equipamento.mapper.TrancaMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid; 
import jakarta.validation.constraints.NotNull; 


import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RestController 
@RequestMapping("/tranca") 
public class TrancaController {

    private final TrancaService trancaService;
    private final BicicletaMapper bicicletaMapper;

 
    private final TrancaMapper trancaMapper;
   
    public TrancaController(TrancaService trancaService, TrancaMapper trancaMapper, BicicletaMapper bicicletaMapper) {
        this.trancaMapper = trancaMapper;
        this.trancaService = trancaService;
        this.bicicletaMapper = bicicletaMapper;
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
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resultado);
        }
    }

   
    @PostMapping("/retirarDaRede")
    public ResponseEntity<String> retirarTrancaDaRede(@RequestBody @Valid RetirarTrancaDTO dto) {
        String resultado = trancaService.retirarTrancaDoSistema(dto);
        if (resultado.contains("sucesso")) {
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resultado);
        }
    }

   
 
    @PostMapping("/{idTranca}/status/{acao}") // Endpoint da URL
    public ResponseEntity<TrancaRespostaDTO> atualizarStatusTranca(@PathVariable @NotNull Integer idTranca, 
            @PathVariable @NotNull String acao) {  
        
        
        StatusTranca novoStatus;
       try {
    novoStatus = StatusTranca.valueOf(acao.toUpperCase());
} catch (IllegalArgumentException e) {
    return ResponseEntity.unprocessableEntity().build();
}

        Optional<Tranca> trancaAtualizadaOpt = trancaService.atualizarStatusTranca(idTranca, novoStatus);
        return trancaAtualizadaOpt.map(trancaMapper::toResponseDTO)
                                  .map(ResponseEntity::ok)
                                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

        


    @PostMapping("/{idTranca}/trancar")
    public ResponseEntity<Object> trancarBicicleta(@PathVariable Integer idTranca, @RequestBody @Valid IdBicicletaDTO IdBicicletaDTO, HttpServletRequest request) {
        try {
            Optional<Tranca> trancaOpt = trancaService.trancar(idTranca, IdBicicletaDTO);
            
                    return trancaOpt
                    .map(tranca -> {
                    TrancaRespostaDTO dto = trancaMapper.toResponseDTO(tranca);
                    return ResponseEntity.ok().body((Object) dto); 
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
        } catch (IllegalStateException e) {
           
            ErrouDTO erro = new ErrouDTO(LocalDateTime.now(),HttpStatus.UNPROCESSABLE_ENTITY.value(),"Unprocessable Entity",
                e.getMessage(),
                request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
        }
    }


    @PostMapping("/{idTranca}/destrancar")
    public ResponseEntity<Object> destrancarBicicleta(@PathVariable Integer idTranca, HttpServletRequest request) {
        try {
            Optional<Tranca> trancaOpt = trancaService.destrancar(idTranca);
            return trancaOpt
                    .map(tranca -> {
                        
                        TrancaRespostaDTO respostaDTO = trancaMapper.toResponseDTO(tranca);
                        return ResponseEntity.ok().body((Object) respostaDTO);
                    })
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

        } catch (IllegalStateException e) {
            ErrouDTO erro = new ErrouDTO(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Unprocessable Entity",
                e.getMessage(),
                request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(erro);
        }
    }


    @GetMapping("/{idTranca}/bicicleta")
    public ResponseEntity<BicicletaRespostaDTO> getBicicletaNaTranca(@PathVariable Integer idTranca) {
        
        //Vai ser mocado pq usa uma funcao externa
        Optional<Bicicleta> bicicletaOpt = trancaService.getBicicletaDeTranca(idTranca);
        
        return bicicletaOpt
                .map(bicicletaMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}