package com.equipamento.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.equipamento.entity.Bicicleta;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.BicicletaRespostaDTO; 

@Mapper(componentModel = "spring")
public interface BicicletaMapper{
    @Mapping(target = "id", ignore = true) // O ID será gerado pelo banco, não vem do DTO
    @Mapping(target = "status", expression = "java(com.equipamento.entity.StatusBicicleta.NOVA)") // Define o status inicial como NOVA (R1 UC10)
    Bicicleta toEntity(BicicletaRequestDTO dto);


     // Método para converter Bicicleta (entidade) para BicicletaRespostaDTO
    BicicletaRespostaDTO toResponseDTO(Bicicleta entity);
}
