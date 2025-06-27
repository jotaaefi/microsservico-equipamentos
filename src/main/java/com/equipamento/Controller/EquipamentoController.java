package com.equipamento.Controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.equipamento.Model.Equipamento;
import com.equipamento.Model.NovoEquipamento;
import com.equipamento.Service.EquipamentoService;
import com.equipamento.dto.EquipamentoRequestDTO;
import com.equipamento.dto.EquipamentoRespostaDTO;
import com.equipamento.mapper.EquipamentoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/equipamentos")
public class EquipamentoController {
    
    private final EquipamentoService equipamentoService;
    private final EquipamentoMapper equipamentoMapper;

    public EquipamentoController(EquipamentoService equipamentoService, EquipamentoMapper equipamentoMapper) {
        this.equipamentoService = equipamentoService;
        this.equipamentoMapper = equipamentoMapper;
    }

   
    @GetMapping
    public ResponseEntity<List<EquipamentoRespostaDTO>> listarEquipamentos() {
        List<Equipamento> equipamentos = equipamentoService.listarTodos();
        
        List<EquipamentoRespostaDTO> respostaDTO = equipamentos.stream()
                .map(equipamentoMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(respostaDTO);
    }

    // Endpoint para BUSCAR um equipamento por ID
    @GetMapping("/{id}")
    public ResponseEntity<EquipamentoRespostaDTO> buscarEquipamentoPorId(@PathVariable Long id) {
        Equipamento equipamento = equipamentoService.buscarPorId(id);
        
        if (equipamento == null) {

            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(equipamentoMapper.toResponseDTO(equipamento));
    }
    
    @PostMapping
    public ResponseEntity<EquipamentoRespostaDTO> criarEquipamento(@Valid @RequestBody EquipamentoRequestDTO requestDTO) {
        
        // 1. Pede ao "atendente" (Mapper) para transformar o "pedido" (RequestDTO) em "instruções para a cozinha" (NovoEquipamento)
        NovoEquipamento novoEquipamento = equipamentoMapper.toModel(requestDTO);
        
        // 2. Envia as instruções para a "cozinha" (Service) preparar
        Equipamento equipamentoCriado = equipamentoService.criarEquipamento(novoEquipamento);
        
        // 3. Pega o "prato" pronto, gera o "recibo" (RespostaDTO) e retorna 200 OK
        return ResponseEntity.ok(equipamentoMapper.toResponseDTO(equipamentoCriado));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> aposentarEquipamento(@PathVariable Long id) {
        Equipamento equipamento = equipamentoService.aposentarEquipamento(id);

        if (equipamento == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }



}
