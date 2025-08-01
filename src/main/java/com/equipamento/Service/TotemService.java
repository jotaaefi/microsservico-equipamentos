package com.equipamento.Service;

import org.springframework.stereotype.Service;

import com.equipamento.Entity.Totem;
import com.equipamento.Repository.TotemRepository;
import com.equipamento.dto.TotemRequestDTO;
import com.equipamento.mapper.TotemMapper;





import java.util.List;
import java.util.Optional;

@Service
public class TotemService {

  
    private final TotemRepository totemRepository;
    private final TotemMapper totemMapper;

    public TotemService(TotemRepository totemRepository, TotemMapper totemMapper){
        this.totemMapper = totemMapper;
        this.totemRepository = totemRepository;
    }

   
    public List<Totem> listarTotens() { 
        return totemRepository.findAll();
    }

    
    
    public Optional<Totem> buscarTotemPorId(Integer id) { // Necessário para TrancaService
        return totemRepository.findById(id);
    }


  
    public Totem criarTotem(TotemRequestDTO requestDTO) {
        
        Totem novoTotem =  totemMapper.toEntity(requestDTO);
        return totemRepository.save(novoTotem);
    }

   
  
    public Optional<Totem> atualizarTotem(Integer id, TotemRequestDTO requestDTO) {
        Optional<Totem> totemOpt = totemRepository.findById(id);

        if (totemOpt.isEmpty()) {
            return Optional.empty(); // Totem não encontrado
        }

        Totem totemExistente = totemOpt.get();

        // Regra R2 UC14: "A informação não pode ser editada." (se refere a ID que já não é editável).
        // Localização e descrição podem ser atualizadas.
        totemExistente.setLocalizacao(requestDTO.localizacao());
        totemExistente.setDescricao(requestDTO.descricao());

        return Optional.of(totemRepository.save(totemExistente));
    }

   
    
    public boolean removerTotem(Integer id) { // UC14 - Manter Cadastro de Totens (Remoção)
        Optional<Totem> totemOpt = totemRepository.findById(id);

        if (totemOpt.isEmpty()) {
            return false; // Totem não encontrado
        }

        Totem totem = totemOpt.get();

       
        if (!totem.getTrancasNaRede().isEmpty()) {
            return false; // Totem possui trancas, não pode ser removido.
        }

        totemRepository.delete(totem); // Exclusão física (já que não há status "aposentado" para totem)
        return true;
    }

    
    public Totem salvarTotem(Totem totem) { // Necessário para TrancaService
        return totemRepository.save(totem);
    }
}
