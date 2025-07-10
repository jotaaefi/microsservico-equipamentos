package com.equipamento.Service;

import com.equipamento.Entity.Tranca;
import java.util.Optional;

// Esta interface define apenas os métodos do TrancaService
// que precisam ser usados por outros serviços.
public interface TrancaServiceExterno {
    
    Optional<Tranca> buscarTrancaPorBicicletaId(Integer bicicletaId);
    
    Tranca salvarTranca(Tranca tranca);

    Optional<Tranca> buscarTrancaPorId(Integer idTranca);
}