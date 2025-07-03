package com.equipamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamento.entity.Tranca;


@Repository
public interface TrancaRepository extends JpaRepository<Tranca, Integer>{
    
}
