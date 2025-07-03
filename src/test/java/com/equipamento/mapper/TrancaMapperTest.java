package com.equipamento.mapper;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Tranca;
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.dto.TrancaRespostaDTO;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class TrancaMapperTest {

    // Pega a implementação do mapper gerada pelo MapStruct
    private final TrancaMapper mapper = Mappers.getMapper(TrancaMapper.class);

    @Test
    void deveMapearRequestDTOparaEntidade() {
        // Arrange (Organizar o cenário)
        // Criamos um DTO com os dados que viriam da API
        TrancaRequestDTO dto = new TrancaRequestDTO(
                101,                        
                "Em frente à Estação A",    
                "2024",                      
                "Modelo-Z"                  
        );

        // Act (Agir)
        // Chamamos o método do mapper para fazer a conversão
        Tranca entidade = mapper.toEntity(dto);

        // Assert (Verificar)
        // Verificamos se o resultado é o esperado
        assertNotNull(entidade);
        assertEquals(101, entidade.getNumero());
        assertEquals("Em frente à Estação A", entidade.getLocalizacao());
        assertEquals("2024", entidade.getAnoDeFabricacao());
        assertEquals("Modelo-Z", entidade.getModelo());
        
        // Verificando as regras definidas na anotação @Mapping
        assertNull(entidade.getId()); 
        assertNull(entidade.getBicicleta()); 
        assertNull(entidade.getTotemId()); 
        assertEquals(StatusTranca.NOVA, entidade.getStatusTranca()); 
    }


    @Test
    void deveMapearEntidadeParaRespostaDTO() {
        // Arrange
        
        Tranca entidade = new Tranca(202, "Vaga B2", "2023", "T-1000", StatusTranca.OCUPADA);
        entidade.setId(15);
        entidade.setTotemId(5);
        // Associamos uma bicicleta a ela para testar o mapeamento do objeto aninhado
        entidade.setBicicleta(new Bicicleta());
        entidade.getBicicleta().setId(77);
        
        // Act
        // Chamamos o método para converter a entidade para o DTO de resposta
        TrancaRespostaDTO dto = mapper.toResponseDTO(entidade);

        // Assert
        // Verificamos se todos os campos foram mapeados corretamente
        assertNotNull(dto);
        assertEquals(15, dto.id());
        assertEquals(202, dto.numero());
        assertEquals("Vaga B2", dto.localizacao());
        assertEquals(StatusTranca.OCUPADA, dto.statusTranca());
        
        // Verificando se o objeto aninhado (bicicleta) também foi mapeado
        // O MapStruct é inteligente e usa o BicicletaMapper para fazer essa conversão
        assertNotNull(dto.bicicleta());
        assertEquals(77, dto.bicicleta().id());
    }
}