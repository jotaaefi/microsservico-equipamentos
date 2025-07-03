package com.equipamento.trabalhoES2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan; // ADICIONE ESTE IMPORT
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// ADICIONE ESTA ANOTAÇÃO FINAL
@EntityScan(basePackages = "com.equipamento.Entity")
@EnableJpaRepositories(basePackages = "com.equipamento.Repository")
@ComponentScan(basePackages = "com.equipamento")
@SpringBootApplication
public class TrabalhoEs2Application {

    public static void main(String[] args) {
        SpringApplication.run(TrabalhoEs2Application.class, args);
    }

}