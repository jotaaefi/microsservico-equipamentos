package com.equipamento.Model;

public enum StatusEquipamento {
    DISPONIVEL, // O equipamento está disponível para uso
    EM_USO,     // O equipamento está atualmente com um ciclista
    EM_MANUTENCAO, // O equipamento foi retirado para reparo
    APOSENTADO;  // O equipamento não será mais utilizado
    
}
