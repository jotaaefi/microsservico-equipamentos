package com.equipamento.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import com.equipamento.Entity.Bicicleta;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.BicicletaRespostaDTO; 

@Mapper(componentModel = "spring")
public  interface BicicletaMapper {
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "status", expression = "java(com.equipamento.Entity.StatusBicicleta.NOVA)") // Define o status inicial como NOVA (R1 UC10)
    Bicicleta toEntity(BicicletaRequestDTO dto);


     
    BicicletaRespostaDTO toResponseDTO(Bicicleta entity);
}
