package com.equipamento.mapper;

import com.equipamento.Model.Equipamento;
import com.equipamento.Model.NovoEquipamento;

import com.equipamento.dto.EquipamentoRequestDTO;
import com.equipamento.dto.EquipamentoRespostaDTO;

import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface EquipamentoMapper {
 
    NovoEquipamento toModel(EquipamentoRequestDTO requestDTO);

    EquipamentoRespostaDTO toResponseDTO(Equipamento equipamento);
}
