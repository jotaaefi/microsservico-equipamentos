package com.equipamento.mapper;

import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Totem;
import com.equipamento.Entity.Tranca;
import com.equipamento.dto.TotemRequestDTO;
import com.equipamento.dto.TotemRespostaDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TotemMapperTest {

    private final TotemMapper mapper = Mappers.getMapper(TotemMapper.class);

    @Test
    void deveMapearRequestDTOparaEntidadeCorretamente() {
        // Arrange
        TotemRequestDTO dto = new TotemRequestDTO("Shopping Aricanduva", "Entrada Principal");

        // Act
        Totem entidade = mapper.toEntity(dto);

        // Assert
        assertNotNull(entidade);
        assertEquals("Shopping Aricanduva", entidade.getLocalizacao());
        assertEquals("Entrada Principal", entidade.getDescricao());
        assertNull(entidade.getId()); // Verifica se o ID foi ignorado
        assertTrue(entidade.getTrancasNaRede().isEmpty()); // Verifica se a lista de trancas foi ignorada e está vazia
    }

    @Test
    void deveMapearEntidadeParaResponseDTOCorretamente() {
        // Arrange
        // Criar uma entidade Totem completa
        Totem entidade = new Totem("Centro", "Ao lado da estação");
        entidade.setId(10);
        
        // E adicionamos algumas trancas a ele para testar o mapeamento da lista
        Tranca tranca1 = new Tranca(101, "Vaga A1", "2024", "T-800", StatusTranca.LIVRE);
        Tranca tranca2 = new Tranca(102, "Vaga A2", "2024", "T-800", StatusTranca.OCUPADA);
        entidade.setTrancasNaRede(Arrays.asList(tranca1, tranca2));

        // Act
        TotemRespostaDTO dto = mapper.toResponseDTO(entidade);

        // Assert
        assertNotNull(dto);
        assertEquals(10, dto.id());
        assertEquals("Centro", dto.localizacao());
        
        // Verificando se a lista de trancas foi mapeada (o MapStruct é configurado para mapear os tipos internos também)
        assertNotNull(dto.trancasNaRede());
        assertEquals(2, dto.trancasNaRede().size());
    }
}