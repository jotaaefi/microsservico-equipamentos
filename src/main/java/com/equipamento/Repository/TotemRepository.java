package com.equipamento.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamento.entity.Totem;

@Repository
public interface TotemRepository extends JpaRepository<Totem, Integer> {
    
}
