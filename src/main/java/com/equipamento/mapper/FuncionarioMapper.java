package com.equipamento.mapper;

import com.equipamento.Entity.Funcionario; // Verifique se o pacote está correto (com.equipamento.Entity)
import com.equipamento.dto.FuncionarioRequestDTO; // Verifique se o pacote está correto
import com.equipamento.dto.FuncionarioRespostaDTO; // Verifique se o pacote está correto
import org.mapstruct.Mapper;
import org.mapstruct.Mapping; // Mantenha este import, pois outras anotações @Mapping existem

@Mapper(componentModel = "spring")
public interface FuncionarioMapper {
    
    // Método para converter FuncionarioRequestDTO para Funcionario
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "matricula", ignore = true)
    Funcionario toEntity(FuncionarioRequestDTO dto);

    
    FuncionarioRespostaDTO toResponseDTO(Funcionario entity);
}