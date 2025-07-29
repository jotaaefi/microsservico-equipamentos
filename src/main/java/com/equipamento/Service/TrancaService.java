package com.equipamento.Service;

import java.util.List;
import java.util.Optional;


import org.springframework.stereotype.Service;


import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Totem;
import com.equipamento.Entity.Tranca;
import com.equipamento.Repository.TrancaRepository;
import com.equipamento.dto.IdBicicletaDTO;
import com.equipamento.dto.IntegrarTrancaDTO;
import com.equipamento.dto.RetirarTrancaDTO;
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.mapper.TrancaMapper;



@Service
public class TrancaService implements TrancaServiceExterno{
   
    
    private final TrancaRepository trancaRepository;
    private final TrancaMapper trancaMapper;
    private final FuncionarioService funcionarioService;
    private final TotemService totemService;
    private final BicicletaService bicicletaService;

    public TrancaService(TrancaRepository trancaRepository,
                         TrancaMapper trancaMapper,
                         FuncionarioService funcionarioService,
                         TotemService totemService, BicicletaService bicicletaService) {
        this.trancaRepository = trancaRepository;
        this.trancaMapper = trancaMapper;
        this.funcionarioService = funcionarioService;
        this.totemService = totemService;
        this.bicicletaService = bicicletaService;
    }

    

  
    public List<Tranca> listarTrancas() { // Nome mais claro
        return trancaRepository.findAll();
    }

   
    public Optional<Tranca> buscarTrancaPorId(Integer id) {
        return trancaRepository.findById(id);
    }


  
    public Tranca criarTranca(TrancaRequestDTO requestDTO) {
        // Converte o DTO para entidade Tranca (mapper já configura status NOVA e ignora ID)
        Tranca novaTranca = trancaMapper.toEntity(requestDTO);
        return trancaRepository.save(novaTranca);
    }

    
    
    public Optional<Tranca> atualizarTranca(Integer id, TrancaRequestDTO requestDTO) {
        Optional<Tranca> trancaOpt = trancaRepository.findById(id);

        if (trancaOpt.isEmpty()) {
            return Optional.empty(); // Tranca não encontrada
        }

        Tranca trancaExistente = trancaOpt.get();

        // Regras R1 e R3 UC13: Não permitir a edição de numero e statusTranca
        trancaExistente.setLocalizacao(requestDTO.localizacao());
        trancaExistente.setAnoDeFabricacao(requestDTO.anoDeFabricacao());
        trancaExistente.setModelo(requestDTO.modelo());

        return Optional.of(trancaRepository.save(trancaExistente));
    }

   
    
    public boolean removerTranca(Integer id) { // UC13 - Manter Cadastro de Trancas (Remoção)
        Optional<Tranca> trancaOpt = trancaRepository.findById(id);

        if (trancaOpt.isEmpty()) {
            return false; // Tranca não encontrada
        }

        Tranca tranca = trancaOpt.get();

        // Regra R4 UC13: "Apenas trancas que não estiverem com nenhuma bicicleta podem ser excluídas."
        if (tranca.getBicicleta() != null) {
            return false; // Tranca ocupada, não pode ser removida diretamente.
        }
        
        // Se a tranca estiver associada a um totem, ela deve ser desassociada primeiro.
        // A regra UC14 R3 ("Apenas os totens que não possuem nenhuma tranca podem ser excluídos.")
        // e UC12 (Retirar Tranca do Sistema de Totens) são mais específicas para a retirada física do totem.
        // Aqui, a "remoção" (aposentadoria) da tranca do cadastro. Se ela está em um totem,
        // o ideal seria primeiro retirá-la via UC12 antes de aposentá-la aqui.
        // Por simplicidade, vamos permitir aposentar se não tiver bicicleta.

        tranca.setStatusTranca(StatusTranca.APOSENTADA);
        trancaRepository.save(tranca);
        return true;
    }


    // UC11 - Incluir Tranca em Totem
   
    public String integrarTrancaEmTotem(IntegrarTrancaDTO dto) {
        // 1. Validação do funcionário (Reparador)
        boolean funcionarioExiste = funcionarioService.verificarFuncionarioExiste();
        if (!funcionarioExiste) {
            return "Funcionário não cadastrado."; // E1 UC11
        }

        // 2. Validação da tranca
        Optional<Tranca> trancaOpt = buscarTrancaPorId(dto.idTranca());
        if (trancaOpt.isEmpty()) {
            return "Tranca não encontrada."; // E1 UC11
        }
        Tranca tranca = trancaOpt.get();

        // 3. Pré-condição UC11: "a tranca deve estar com status de "nova" ou "em reparo"."
        if (!(tranca.getStatusTranca().equals(StatusTranca.NOVA) || tranca.getStatusTranca().equals(StatusTranca.EM_REPARO))) {
            return "A tranca não está em um status válido para integração (esperado NOVA ou EM_REPARO).";
        }
        
        // 4. Validação do totem
        Optional<Totem> totemOpt = totemService.buscarTotemPorId(dto.idTotem()); // Precisaremos de buscarTotemPorId em TotemService
        if (totemOpt.isEmpty()) {
            return "Totem não encontrado.";
        }
        Totem totem = totemOpt.get();

        // 5. Verifica se a tranca já está associada a algum totem (não pode integrar se já estiver)
        if (tranca.getTotemId() != null) { // <-- CÓDIGO CORRIGIDO
            return "Tranca já está associada a um totem.";
        }


        // Lógica de integração (UC11 - Fluxo Principal)
        // O sistema registra os dados da inclusão (log) [R1 UC11]
        tranca.setStatusTranca(StatusTranca.LIVRE); // Altera o status da tranca para "disponível" (LIVRE no enum)
        totem.addTranca(tranca); // Adiciona a tranca ao totem (gerencia a relação bidirecional)

        trancaRepository.save(tranca);
        totemService.salvarTotem(totem); // Precisaremos de salvarTotem em TotemService

        // Simula o envio de e-mail ao reparador (Regra R2 UC11)
   

        return "Tranca integrada ao totem com sucesso.";
    }


    
    public String retirarTrancaDoSistema(RetirarTrancaDTO dto) {
        // 1. Validação do funcionário (Reparador)
        boolean funcionarioExiste = funcionarioService.verificarFuncionarioExiste();
        if (!funcionarioExiste) {
            return "Funcionário não cadastrado.";
        }

        // 2. Validação da tranca
        Optional<Tranca> trancaOpt = buscarTrancaPorId(dto.idTranca());
        if (trancaOpt.isEmpty()) {
            return "Tranca não encontrada."; // E1 UC12
        }
        Tranca tranca = trancaOpt.get();

        // 3. Pré-condição UC12: "a tranca deve estar sem nenhuma bicicleta presa nela."
        if (tranca.getBicicleta() != null) {
            return "A tranca não pode ser retirada pois contém uma bicicleta.";
        }

        // 4. Validação do totem
        Optional<Totem> totemOpt = totemService.buscarTotemPorId(dto.idTotem());
        if (totemOpt.isEmpty()) {
            return "Totem não encontrado.";
        }
        Totem totem = totemOpt.get();

        // 5. Verifica se a tranca pertence ao totem informado
        if (tranca.getTotemId() == null || !tranca.getTotemId().equals(totem.getId())) {
            return "Tranca não está associada a este totem.";
        }

        
        
        if(tranca.getStatusTranca().equals(StatusTranca.NOVA) ||
            tranca.getStatusTranca().equals(StatusTranca.EM_REPARO) ||
            tranca.getStatusTranca().equals(StatusTranca.APOSENTADA)) {
            return "A tranca está em um status inválido para retirada (esperado LIVRE, OCUPADA ou REPARO_SOLICITADO).";
        }


        // Lógica de retirada (UC12 - Fluxo Principal e Alternativo A1)
        if (dto.statusAcaoReparador().equalsIgnoreCase("REPARO")) {
            tranca.setStatusTranca(StatusTranca.EM_REPARO);
        } else if (dto.statusAcaoReparador().equalsIgnoreCase("APOSENTAR")) {
            tranca.setStatusTranca(StatusTranca.APOSENTADA);
        } else {
            return "Ação de reparador inválida (REPARO ou APOSENTAR).";
        }

        // Desassocia a tranca do totem
        totem.removeTranca(tranca); // Remove a tranca do totem (gerencia a relação bidirecional)
        // O setTotem(null) já é feito no removeTranca do Totem

        trancaRepository.save(tranca);
        totemService.salvarTotem(totem); // Salva o totem atualizado

        
        

        return "Tranca retirada da rede com sucesso.";
    }

   
  
    public Tranca salvarTranca(Tranca tranca) {
        return trancaRepository.save(tranca);
    }

  
    public Optional<Tranca> buscarTrancaPorBicicletaId(Integer bicicletaId) {
        return trancaRepository.findAll().stream()
                .filter(tranca -> tranca.getBicicleta() != null && tranca.getBicicleta().getId().equals(bicicletaId))
                .findFirst();
    }


    

    public Optional<Tranca> atualizarStatusTranca(Integer idTranca, StatusTranca novoStatus) {
        Optional<Tranca> trancaOpt = trancaRepository.findById(idTranca);
        if (trancaOpt.isEmpty()) {
            return Optional.empty();
        }

        Tranca tranca = trancaOpt.get();
        tranca.setStatusTranca(novoStatus); // Use setStatusTranca
        return Optional.of(trancaRepository.save(tranca));
    }

    
    public Optional<Tranca> trancar(Integer idTranca, IdBicicletaDTO dto) {
        Optional<Tranca> trancaOpt = this.buscarTrancaPorId(idTranca);
       
        Optional<Bicicleta> bicicletaOpt = bicicletaService.buscarBicicletaPorId(dto.idBicicleta());

        if (trancaOpt.isEmpty() || bicicletaOpt.isEmpty()) {
            return Optional.empty(); // Tranca ou Bicicleta não encontrada
        }

        Tranca tranca = trancaOpt.get();
        Bicicleta bicicleta = bicicletaOpt.get();

        // Valida as pré-condições
        if (tranca.getBicicleta() != null || tranca.getStatusTranca() != StatusTranca.LIVRE) {
            throw new IllegalStateException("Tranca não está livre para receber uma bicicleta.");
        }
        if (bicicleta.getStatus() != StatusBicicleta.EM_USO) {
            throw new IllegalStateException("Bicicleta não está em um status que permita a devolução.");
        }

        
        tranca.setBicicleta(bicicleta);
        tranca.setStatusTranca(StatusTranca.OCUPADA);
        bicicleta.setStatus(StatusBicicleta.DISPONIVEL); // Bicicleta agora está disponível na rede

        
        return Optional.of(this.salvarTranca(tranca));
    }

   
    public Optional<Tranca> destrancar(Integer idTranca) {
        Optional<Tranca> trancaOpt = this.buscarTrancaPorId(idTranca);
        if (trancaOpt.isEmpty()) {
            return Optional.empty(); // Tranca não encontrada
        }

        Tranca tranca = trancaOpt.get();
        Bicicleta bicicleta = tranca.getBicicleta();

        // Valida as pré-condições
        if (bicicleta == null || tranca.getStatusTranca() != StatusTranca.OCUPADA) {
            throw new IllegalStateException("Tranca não está ocupada ou não contém uma bicicleta.");
        }
        
       
        tranca.setBicicleta(null); // Desassocia a bicicleta
        tranca.setStatusTranca(StatusTranca.LIVRE);
        bicicleta.setStatus(StatusBicicleta.EM_USO);

       
        return Optional.of(this.salvarTranca(tranca));
    }


  
    public Optional<Bicicleta> getBicicletaDeTranca(Integer idTranca) {
        // Busca a tranca pelo ID
        Optional<Tranca> trancaOpt = trancaRepository.findById(idTranca);

        return trancaOpt.flatMap(tranca -> Optional.ofNullable(tranca.getBicicleta()));
    }

}
