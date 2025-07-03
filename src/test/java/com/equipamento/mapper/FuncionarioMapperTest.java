package com.equipamento.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.equipamento.Entity.FuncaoFuncionario;
import com.equipamento.Entity.Funcionario;
import com.equipamento.dto.FuncionarioRequestDTO;
import com.equipamento.dto.FuncionarioRespostaDTO;

class FuncionarioMapperTest {
    private final FuncionarioMapper mapper = Mappers.getMapper(FuncionarioMapper.class);

    @Test
    void deveMapearRequestDTOparaEntidade() {
        // Arrange
        FuncionarioRequestDTO dto = new FuncionarioRequestDTO("Maria", 30, FuncaoFuncionario.ADMINISTRATIVO, "11122233344", "maria@email.com", "senha123");

        // Act
        Funcionario entidade = mapper.toEntity(dto);

        // Assert
        assertNotNull(entidade);
        assertEquals("Maria", entidade.getNome());
        assertNull(entidade.getId());
        assertNull(entidade.getMatricula());
    }
    
    @Test
    void deveMapearEntidadeParaRespostaDTO() {
        // Arrange
        // 
        Funcionario entidade = new Funcionario("João Silva", 42, FuncaoFuncionario.REPARADOR, "99988877766", "joao.silva@email.com", "senhaForte");
        entidade.setId(10);
        entidade.setMatricula("REP1001");

        // Act
        // Chamamos o método para converter a entidade para o DTO de resposta
        FuncionarioRespostaDTO dto = mapper.toResponseDTO(entidade);

        // Assert
        // Verificamos se todos os campos foram mapeados corretamente
        assertNotNull(dto);
        assertEquals(10, dto.id());
        assertEquals("REP1001", dto.matricula());
        assertEquals("João Silva", dto.nome());
        assertEquals(42, dto.idade());
        assertEquals(FuncaoFuncionario.REPARADOR, dto.funcao());
        assertEquals("99988877766", dto.cpf());
        assertEquals("joao.silva@email.com", dto.email());
        // Note que a senha não é mapeada para o DTO de resposta, o que está correto.
    }
}
