package com.equipamento.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.equipamento.Entity.Totem;
import com.equipamento.dto.TotemRequestDTO;
import com.equipamento.dto.TotemRespostaDTO;

@Mapper
public interface TotemMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trancasNaRede", ignore = true) // A lista de trancas não vem no RequestDTO
    Totem toEntity(TotemRequestDTO dto);

    @Mapping(target = "trancasNaRede", source = "trancasNaRede")
    TotemRespostaDTO toResponseDTO(Totem entity);
}
