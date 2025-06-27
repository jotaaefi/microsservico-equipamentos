package com.equipamento.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.equipamento.Model.Equipamento;
import com.equipamento.Model.NovoEquipamento;
import com.equipamento.Model.StatusEquipamento;
import com.equipamento.Repository.EquipamentoRepository;



@Service
public class EquipamentoService {
    
    private final EquipamentoRepository equipamentoRepository;

    public EquipamentoService(EquipamentoRepository equipamentoRepository) {
        this.equipamentoRepository = equipamentoRepository;
    }

    //Lista todos os equipamentos cadastrados
    public List<Equipamento> listarTodos() {
        return equipamentoRepository.findAll();
    }

    public Equipamento buscarPorId(Long id) {
    Optional<Equipamento> resultado = equipamentoRepository.findById(id); // buscar 

        if (resultado.isPresent()) {
            return resultado.get();
            
        } 
        else 
            return null;

    }
    
    
    public Equipamento criarEquipamento(NovoEquipamento novoEquipamento) { //Criar um novo equipamento
            Equipamento equipamento = new Equipamento(novoEquipamento);
            return equipamentoRepository.save(equipamento);
    }

    /**
     * Aposenta um equipamento, mudando seu status para APOSENTADO (Soft Delete).
     * @param id O ID do equipamento a ser aposentado.
     * @return O equipamento com o status atualizado.
     */

    public Equipamento aposentarEquipamento(Long id) {
        Equipamento equipamento = buscarPorId(id);

        if (equipamento != null) {
            equipamento.setStatus(StatusEquipamento.APOSENTADO);
            return equipamentoRepository.save(equipamento); //salva no banco
        }
        return null;
    }
}
