package com.equipamento.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Totem;
import com.equipamento.Entity.Tranca;
import com.equipamento.Repository.TrancaRepository;
import com.equipamento.dto.IntegrarTrancaDTO;
import com.equipamento.dto.RetirarTrancaDTO;
import com.equipamento.dto.TrancaRequestDTO;
import com.equipamento.mapper.TrancaMapper;

import jakarta.transaction.Transactional;

@Service
public class TrancaService {
   
    @Autowired
    private  TrancaRepository trancaRepository;
    
    @Autowired
    private  TrancaMapper trancaMapper;
    
    @Autowired
    private FuncionarioService funcionarioService;

    @Autowired
    private TotemService totemService;
    
    public TrancaService(){}
    
    

    /**
     * Lista todas as trancas cadastradas no sistema.
     * @return Uma lista de objetos Tranca.
     */
    public List<Tranca> listarTrancas() { // Nome mais claro
        return trancaRepository.findAll();
    }

 
    public Optional<Tranca> buscarTrancaPorId(Integer id) {
        return trancaRepository.findById(id);
    }


    @Transactional
    public Tranca criarTranca(TrancaRequestDTO requestDTO) {
        // Converte o DTO para entidade Tranca (mapper já configura status NOVA e ignora ID)
        Tranca novaTranca = trancaMapper.toEntity(requestDTO);
        return trancaRepository.save(novaTranca);
    }

    
    @Transactional
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

   
    @Transactional
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
    @Transactional
    public String integrarTrancaEmTotem(IntegrarTrancaDTO dto) {
        // 1. Validação do funcionário (Reparador)
        boolean funcionarioExiste = funcionarioService.verificarFuncionarioExiste(dto.idFuncionario());
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
        if (tranca.getTotem() != null) {
             return "Tranca já está associada a um totem.";
        }


        // Lógica de integração (UC11 - Fluxo Principal)
        // O sistema registra os dados da inclusão (log) [R1 UC11]
        tranca.setStatusTranca(StatusTranca.LIVRE); // Altera o status da tranca para "disponível" (LIVRE no enum)
        totem.addTranca(tranca); // Adiciona a tranca ao totem (gerencia a relação bidirecional)

        trancaRepository.save(tranca);
        totemService.salvarTotem(totem); // Precisaremos de salvarTotem em TotemService

        // Simula o envio de e-mail ao reparador (Regra R2 UC11)
        // emailService.enviarEmail(dto.idFuncionario(), "Tranca Integrada", "Sua tranca foi integrada ao totem.");

        return "Tranca integrada ao totem com sucesso.";
    }


    @Transactional
    public String retirarTrancaDoSistema(RetirarTrancaDTO dto) {
        // 1. Validação do funcionário (Reparador)
        boolean funcionarioExiste = funcionarioService.verificarFuncionarioExiste(dto.idFuncionario());
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
        if (tranca.getTotem() == null || !tranca.getTotem().getId().equals(totem.getId())) {
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

        // Simula o envio de e-mail ao reparador (Regra R2 UC12)
        // emailService.enviarEmail(dto.idFuncionario(), "Tranca Retirada", "Sua tranca foi retirada da rede.");

        return "Tranca retirada da rede com sucesso.";
    }

   
    @Transactional
    public Tranca salvarTranca(Tranca tranca) {
        return trancaRepository.save(tranca);
    }


    public Optional<Tranca> buscarTrancaPorBicicletaId(Integer bicicletaId) {
        return trancaRepository.findAll().stream()
                .filter(tranca -> tranca.getBicicleta() != null && tranca.getBicicleta().getId().equals(bicicletaId))
                .findFirst();
    }


    
    @Transactional
    public Optional<Tranca> atualizarStatusTranca(Integer idTranca, StatusTranca novoStatus) {
        Optional<Tranca> trancaOpt = trancaRepository.findById(idTranca);
        if (trancaOpt.isEmpty()) {
            return Optional.empty();
        }

        Tranca tranca = trancaOpt.get();
        tranca.setStatusTranca(novoStatus); // Use setStatusTranca
        return Optional.of(trancaRepository.save(tranca));
    }


}
