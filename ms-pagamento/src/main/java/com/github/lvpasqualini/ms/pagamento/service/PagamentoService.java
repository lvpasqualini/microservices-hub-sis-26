package com.github.lvpasqualini.ms.pagamento.service;

import com.github.lvpasqualini.ms.pagamento.client.PedidoClient;
import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.entities.Pagamento;
import com.github.lvpasqualini.ms.pagamento.entities.Status;
import com.github.lvpasqualini.ms.pagamento.exceptions.PagamentoAprovadoException;
import com.github.lvpasqualini.ms.pagamento.exceptions.ResourceNotFoundException;
import com.github.lvpasqualini.ms.pagamento.repositories.PagamentoRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagamentoService {
    @Autowired
    private PagamentoRepository repository;

    @Autowired
    private PedidoClient pedidoClient;

    @Transactional(readOnly = true)
    public List<PagamentoDTO> findAll() {
        return repository.findAll().stream().map(PagamentoDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PagamentoDTO findById(Long id) {
        Pagamento pagamento = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado com o ID: " +id)
        );
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO save(PagamentoDTO pagamentoDTO) {
        Pagamento pagamento = new Pagamento();
        mapperDtoToPagamento(pagamentoDTO,pagamento);
        pagamento.setStatus(Status.CRIADO);
        pagamento = repository.save(pagamento);
        return new PagamentoDTO(pagamento);
    }

    @Transactional
    public PagamentoDTO update(Long id, PagamentoDTO pagamentoDTO) {
        try {
            Pagamento pagamento = repository.getReferenceById(id);

            if (pagamento.getStatus().equals(Status.APROVADO)) {
                throw new PagamentoAprovadoException(
                        String.format("Pagamento id: %d já está APROVADO e não pode ser alterado", id)
                );
            }

            mapperDtoToPagamento(pagamentoDTO,pagamento);
            pagamento.setStatus(pagamentoDTO.getStatus());
            pagamento = repository.save(pagamento);
            return new PagamentoDTO(pagamento);
        }catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Recurso não encontrado com o ID: " + id);
        }
    }

    @Transactional
    public void deleteById(Long id) {
        if(!repository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado com o ID: "+ id);
        }
        repository.deleteById(id);
    }

    @Transactional
    public PagamentoDTO confirmarPagamentoDoPedido(Long id) {
        Pagamento pagamento = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pagamento não encontrado com o ID: " +id)
        );

        pagamento.setStatus(Status.APROVADO);
        repository.save(pagamento);

        try {
            pedidoClient.confirmarPagamento(pagamento.getPedidoId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Pedido não encontrado ID: " + pagamento.getPedidoId());
        }catch (FeignException e) {
            throw new RuntimeException("Falha ao comunicar com ms-pedidos", e);
        }

        return new PagamentoDTO(pagamento);
    }

    private void mapperDtoToPagamento(PagamentoDTO pagamentoDTO, Pagamento pagamento) {
        pagamento.setValor(pagamentoDTO.getValor());
        pagamento.setNome(pagamentoDTO.getNome());
        pagamento.setNumeroCartao(pagamentoDTO.getNumeroCartao());
        pagamento.setValidade(pagamentoDTO.getValidade());
        pagamento.setCodigoSeguranca(pagamentoDTO.getCodigoSeguranca());
        pagamento.setPedidoId(pagamentoDTO.getPedidoId());
    }
}
