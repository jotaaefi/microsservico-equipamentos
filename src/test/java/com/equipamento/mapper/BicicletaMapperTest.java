package com.equipamento.mapper;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.BicicletaRespostaDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class BicicletaMapperTest {

    // Pega a implementação do mapper gerada pelo MapStruct
    private final BicicletaMapper mapper = Mappers.getMapper(BicicletaMapper.class);

    @Test
    void deveMapearRequestDTOparaEntidadeCorretamente() {
        // Arrange (Organizar o cenário)
        // Criamos um DTO com os dados que viriam de uma requisição da API
        BicicletaRequestDTO dto = new BicicletaRequestDTO("Caloi", "Mountain Bike", "2024");

        // Act (Agir)
        // Chamamos o método do mapper para fazer a conversão
        Bicicleta entidade = mapper.toEntity(dto);

        // Assert (Verificar)
        // Verificamos se o resultado é o esperado
        assertNotNull(entidade);
        assertEquals("Caloi", entidade.getMarca());
        assertEquals("Mountain Bike", entidade.getModelo());
        assertEquals("2024", entidade.getAno());

        // Verificando as regras da anotação @Mapping
        assertNull(entidade.getId()); // Deve ser nulo porque usamos @Mapping(target = "id", ignore = true)
        assertEquals(StatusBicicleta.NOVA, entidade.getStatus()); // Deve ser NOVA por causa da 'expression'
    }

    @Test
    void deveMapearEntidadeParaRespostaDTOCorretamente() {
        // Arrange
        // Criamos uma entidade completa, como se viesse do banco de dados
        Bicicleta entidade = new Bicicleta("Houston", "Foxer", "2025", 101, StatusBicicleta.EM_REPARO);
        entidade.setId(42);

        // Act
        // Chamamos o método do mapper para converter para o DTO de resposta
        BicicletaRespostaDTO dto = mapper.toResponseDTO(entidade);

        // Assert
        // Verificamos se todos os campos foram mapeados corretamente
        assertNotNull(dto);
        assertEquals(42, dto.id());
        assertEquals("Houston", dto.marca());
        assertEquals("Foxer", dto.modelo());
        assertEquals("2025", dto.ano());
        assertEquals(101, dto.numero());
        assertEquals(StatusBicicleta.EM_REPARO, dto.status());
    }
}