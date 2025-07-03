package com.equipamento.entity;

public enum StatusBicicleta {
    
    DISPONIVEL,         // O equipamento está disponível para uso
    EM_USO,             // O equipamento está atualmente com um ciclista
    NOVA,               // Status inicial de uma bicicleta recém-criada (R1 UC10)
    APOSENTADA,         // O equipamento não será mais utilizado
    REPARO_SOLICITADO,  // O reparo foi solicitado, mas ainda não começou (UC04 A3.2)
    EM_REPARO;          // O equipamento foi retirado para reparo (UC09)
    
}
