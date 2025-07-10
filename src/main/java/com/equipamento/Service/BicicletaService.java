package com.equipamento.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.equipamento.Entity.Bicicleta;
import com.equipamento.Entity.StatusBicicleta;
import com.equipamento.Entity.StatusTranca;
import com.equipamento.Entity.Tranca;
import com.equipamento.Repository.BicicletaRepository;
import com.equipamento.dto.BicicletaRequestDTO;
import com.equipamento.dto.IntegrarBicicletaDTO;
import com.equipamento.dto.RetirarBicicletaDTO;
import com.equipamento.mapper.BicicletaMapper;



@Service
public class BicicletaService {
    
    private final BicicletaRepository bicicletaRepository;
    private final BicicletaMapper bicicletaMapper;
    private final FuncionarioService funcionarioService;
    private final TrancaServiceExterno trancaService;
    private static final AtomicInteger numeroBicicletaCounter = new AtomicInteger(0);


    
    public BicicletaService(BicicletaRepository bicicletaRepository,
                            BicicletaMapper bicicletaMapper,
                            FuncionarioService funcionarioService,
                            @Lazy TrancaServiceExterno trancaService) { // <-- Trocamos TrancaService pela Interface aqui
        this.bicicletaRepository = bicicletaRepository;
        this.bicicletaMapper = bicicletaMapper;
        this.funcionarioService = funcionarioService;
        this.trancaService = trancaService;
    }



    public List<Bicicleta> listarBicicletas() {
        return bicicletaRepository.findAll();
    }


    public Optional<Bicicleta> buscarBicicletaPorId(Integer id) {
        return bicicletaRepository.findById(id);
    }

   
    //@Transactional
    public Bicicleta criarBicicleta(BicicletaRequestDTO requestDTO) {
        Bicicleta novaBicicleta = bicicletaMapper.toEntity(requestDTO);
        novaBicicleta.setNumero(numeroBicicletaCounter.incrementAndGet());
        return bicicletaRepository.save(novaBicicleta);
    }

   
    //@Transactional
    public Optional<Bicicleta> atualizarBicicleta(Integer id, BicicletaRequestDTO requestDTO) {
        Optional<Bicicleta> bicicletaOpt = bicicletaRepository.findById(id);
        if (bicicletaOpt.isEmpty()) {
            return Optional.empty();
        }
        Bicicleta bicicletaExistente = bicicletaOpt.get();
        bicicletaExistente.setMarca(requestDTO.marca());
        bicicletaExistente.setModelo(requestDTO.modelo());
        bicicletaExistente.setAno(requestDTO.ano());
        return Optional.of(bicicletaRepository.save(bicicletaExistente));
    }

  
    //@Transactional
    public boolean aposentarBicicleta(Integer id) {
        Optional<Bicicleta> bicicletaOpt = bicicletaRepository.findById(id);
        if (bicicletaOpt.isEmpty()) {
            return false;
        }
        Bicicleta bicicleta = bicicletaOpt.get();

        // Regra R4 UC10: A bicicleta não pode estar em nenhuma tranca para ser aposentada diretamente.
        // Esta chamada depende do TrancaService ter o método buscarTrancaPorBicicletaId.
        Optional<Tranca> trancaAssociada = trancaService.buscarTrancaPorBicicletaId(id);
        if (trancaAssociada.isPresent()) {
            return false;
        }
        bicicleta.setStatus(StatusBicicleta.APOSENTADA);
        bicicletaRepository.save(bicicleta);
        return true;
    }

  
    //@Transactional
    public String integrarBicicletaNaRede(IntegrarBicicletaDTO dto) {
        // Validação do funcionário (Reparador - UC08 Atores). Esta chamada depende do FuncionarioService ter verificarFuncionarioExiste.
        // Se o FuncionarioService não tiver este método ou for simplificado, esta linha pode precisar de um mock ou comportamento falso temporário.
        boolean funcionarioExiste = funcionarioService.verificarFuncionarioExiste();
        if (!funcionarioExiste) {
            return "Funcionário não cadastrado.";
        }

        // Validação da bicicleta
        Optional<Bicicleta> bicicletaOpt = buscarBicicletaPorId(dto.idBicicleta());
        if (bicicletaOpt.isEmpty()) {
            return "Bicicleta não encontrada.";
        }
        Bicicleta bicicleta = bicicletaOpt.get();

        // Pré-condição UC08: "a bicicleta deve estar com status de "nova" ou "em reparo""
        if (!(bicicleta.getStatus().equals(StatusBicicleta.NOVA) || bicicleta.getStatus().equals(StatusBicicleta.EM_REPARO))) {
            return "A bicicleta não está em um status válido para integração (esperado NOVA ou EM_REPARO).";
        }

        // Validação da tranca. Esta chamada depende do TrancaService ter buscarTrancaPorId e salvarTranca.
        Optional<Tranca> trancaOpt = trancaService.buscarTrancaPorId(dto.idTranca());
        if (trancaOpt.isEmpty()) {
            return "Tranca não encontrada.";
        }
        Tranca tranca = trancaOpt.get();

        // Pré-condição UC08: "a tranca deve estar com o status "disponível"." (LIVRE no seu enum)
        if (!tranca.getStatusTranca().equals(StatusTranca.LIVRE)) { // Use getStatusTranca
            return "A tranca não está disponível (LIVRE).";
        }
        
        // Lógica de integração (UC08 - Fluxo Principal)
        bicicleta.setStatus(StatusBicicleta.DISPONIVEL);
        tranca.setBicicleta(bicicleta);
        tranca.setStatusTranca(StatusTranca.OCUPADA); // Use setStatusTranca

        bicicletaRepository.save(bicicleta);
        trancaService.salvarTranca(tranca);

        // Simula o envio de e-mail ao reparador (Regra R2 UC08)

        return "Bicicleta integrada à rede com sucesso.";
    }

  
    //@Transactional
    public String retirarBicicletaDaRede(RetirarBicicletaDTO dto) {
        // Validação do funcionário (Reparador - UC09 Atores)
        // Se o FuncionarioService não tiver este método ou for simplificado, esta linha pode precisar de um mock ou comportamento falso temporário.
        boolean funcionarioExiste = funcionarioService.verificarFuncionarioExiste();
        if (!funcionarioExiste) {
            return "Funcionário não cadastrado.";
        }

        // Validação da bicicleta
        Optional<Bicicleta> bicicletaOpt = buscarBicicletaPorId(dto.idBicicleta());
        if (bicicletaOpt.isEmpty()) {
            return "Bicicleta não encontrada.";
        }
        Bicicleta bicicleta = bicicletaOpt.get();

        // Pré-condição UC09: "bicicleta deve estar ... com status "reparo solicitado"."
        if (!bicicleta.getStatus().equals(StatusBicicleta.DISPONIVEL) && !bicicleta.getStatus().equals(StatusBicicleta.REPARO_SOLICITADO)) {
            return "A bicicleta não está em um status válido para retirada (esperado DISPONIVEL ou REPARO_SOLICITADO).";
        }

     
        Optional<Tranca> trancaOpt = trancaService.buscarTrancaPorId(dto.idTranca());
        if (trancaOpt.isEmpty()) {
            return "Tranca não encontrada.";
        }
        Tranca tranca = trancaOpt.get();

        
        if (tranca.getBicicleta() == null || !tranca.getBicicleta().getId().equals(bicicleta.getId())) {
             return "A bicicleta não está associada a esta tranca.";
        }
        if (!tranca.getStatusTranca().equals(StatusTranca.OCUPADA)) { // Use getStatusTranca
            return "A tranca não está no status OCUPADA.";
        }

      
        if (dto.statusAcaoReparador().equalsIgnoreCase("REPARO")) {
            bicicleta.setStatus(StatusBicicleta.EM_REPARO);
        } else if (dto.statusAcaoReparador().equalsIgnoreCase("APOSENTAR")) {
            bicicleta.setStatus(StatusBicicleta.APOSENTADA);
        } else {
            return "Ação de reparador inválida (REPARO ou APOSENTAR).";
        }

        tranca.setBicicleta(null);
        tranca.setStatusTranca(StatusTranca.LIVRE); // Use setStatusTranca

        bicicletaRepository.save(bicicleta);
        trancaService.salvarTranca(tranca);

        // Simula o envio de e-mail ao reparador (Regra R2 UC09)
        

        return "Bicicleta retirada da rede com sucesso.";
    }

    //@Transactional
    public Optional<Bicicleta> atualizarStatusBicicleta(Integer idBicicleta, StatusBicicleta novoStatus) {
        Optional<Bicicleta> bicicletaOpt = bicicletaRepository.findById(idBicicleta);
        if (bicicletaOpt.isEmpty()) {
            return Optional.empty();
        }

        Bicicleta bicicleta = bicicletaOpt.get();
        bicicleta.setStatus(novoStatus);
        return Optional.of(bicicletaRepository.save(bicicleta));
    }

    public boolean removerBicicleta(Integer idBicicleta) {
            Optional<Bicicleta> bicicletaOpt = bicicletaRepository.findById(idBicicleta);
            
            if (bicicletaOpt.isEmpty()) {
                return false;
            }

            Bicicleta bicicleta = bicicletaOpt.get();
            
            if (bicicleta.getStatus() != StatusBicicleta.APOSENTADA) {
                return false;
            }

            bicicletaRepository.delete(bicicleta);
            return true;
        }

}
