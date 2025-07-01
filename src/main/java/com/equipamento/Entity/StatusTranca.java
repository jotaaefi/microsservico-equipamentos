package com.equipamento.Entity;

public enum StatusTranca {

    LIVRE,          // Tranca está vazia e pronta para receber uma bicicleta (UC03, UC04)
    OCUPADA,        // Tranca contém uma bicicleta (UC03, UC04)
    NOVA,           // Status inicial de uma tranca recém-cadastrada (UC13 R1)
    APOSENTADA,     // Tranca não será mais utilizada (UC12 A1)
    EM_REPARO,      // Tranca foi retirada para reparo (UC12)
    REPARO_SOLICITADO; // Tranca t
}
