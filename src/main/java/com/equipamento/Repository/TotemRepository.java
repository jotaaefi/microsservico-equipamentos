package com.equipamento.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.equipamento.Entity.Totem;

@Repository
public interface TotemRepository extends JpaRepository<Totem, Integer> {
    
}
