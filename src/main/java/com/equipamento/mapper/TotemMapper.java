package com.equipamento.mapper;



import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.equipamento.Entity.Totem;
import com.equipamento.dto.TotemRequestDTO;
import com.equipamento.dto.TotemRespostaDTO;


@Mapper(componentModel = "spring")
public interface TotemMapper {
    
    @Mapping(target = "id", ignore = true) // O ID será gerado pelo banco
    @Mapping(target = "trancasNaRede", ignore = true) // A lista de trancas não vem no RequestDTO
    Totem toEntity(TotemRequestDTO dto);

    // Método para converter Totem (entidade) para TotemRespostaDTO
    // Precisamos mapear a lista de Trancas para TrancaRespostaDTO dentro de TotemRespostaDTO
    @Mapping(target = "trancasNaRede", source = "trancasNaRede")
    TotemRespostaDTO toResponseDTO(Totem entity);

  
}
