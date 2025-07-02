package com.equipamento.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamento.Entity.Funcionario;


@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer>{
    
   
     Optional<Funcionario> findByMatricula(String matricula);
     Optional<Funcionario> findByEmail(String email);
     Optional<Funcionario> findByCpf(String cpf);
}
