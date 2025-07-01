package com.equipamento.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamento.Entity.Funcionario;


@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer>{
    
    // Opcional: Se precisar buscar funcionários por matrícula ou e-mail (que são únicos),
    // você pode adicionar métodos aqui (o Spring Data JPA os implementa automaticamente):
     Optional<Funcionario> findByMatricula(String matricula);
     Optional<Funcionario> findByEmail(String email);
     Optional<Funcionario> findByCpf(String cpf);
}
