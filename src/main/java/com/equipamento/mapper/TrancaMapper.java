package com.equipamento.mapper;

import com.equipamento.entity.Tranca; 
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrancaMapper {

    // Método para converter TrancaRequestDTO para Tranca
    // O ID é gerado pelo banco.
    // O status inicial é definido como 'NOVA' (R1 UC13).
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statusTranca", expression = "java(com.equipamento.entity.StatusTranca.NOVA)") 
    @Mapping(target = "bicicleta", ignore = true)
    @Mapping(target = "totem", ignore = true)
    Tranca toEntity(TrancaRequestDTO dto);

    // Método para converter Tranca (entidade) para TrancaRespostaDTO
    TrancaRespostaDTO toResponseDTO(Tranca entity);
}