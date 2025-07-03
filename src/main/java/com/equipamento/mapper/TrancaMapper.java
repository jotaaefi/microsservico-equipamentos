package com.equipamento.mapper;

// Imports corrigidos para usar o pacote 'entity' em minúsculo
import com.equipamento.Entity.Tranca;
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrancaMapper {

    @Mapping(target = "id", ignore = true)
    
    @Mapping(target = "statusTranca", expression = "java(com.equipamento.Entity.StatusTranca.NOVA)")
    @Mapping(target = "bicicleta", ignore = true)
    @Mapping(target = "totemId", ignore = true)
    Tranca toEntity(TrancaRequestDTO dto);

    TrancaRespostaDTO toResponseDTO(Tranca entity);
}