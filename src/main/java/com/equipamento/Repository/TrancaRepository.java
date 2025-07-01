package com.equipamento.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamento.Entity.Tranca;


@Repository
public interface TrancaRepository extends JpaRepository<Tranca, Integer>{
    
}
