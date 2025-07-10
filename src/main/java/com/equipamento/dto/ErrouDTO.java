package com.equipamento.dto;

import java.time.LocalDateTime;

public record ErrouDTO(LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path) {
    
}
