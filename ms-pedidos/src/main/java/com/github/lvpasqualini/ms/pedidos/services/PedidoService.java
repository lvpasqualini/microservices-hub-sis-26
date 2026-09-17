package com.github.lvpasqualini.ms.pedidos.services;

import com.github.lvpasqualini.ms.pedidos.dto.ItemPedidoResponseDTO;
import com.github.lvpasqualini.ms.pedidos.dto.PedidoRequestDTO;
import com.github.lvpasqualini.ms.pedidos.dto.PedidoResponseDTO;
import com.github.lvpasqualini.ms.pedidos.entities.ItemDoPedido;
import com.github.lvpasqualini.ms.pedidos.entities.Pedido;
import com.github.lvpasqualini.ms.pedidos.entities.Status;
import com.github.lvpasqualini.ms.pedidos.repositories.ItemDoPedidoRepository;
import com.github.lvpasqualini.ms.pedidos.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.lvpasqualini.ms.pedidos.exceptions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ItemDoPedidoRepository itemDoPedidoRepository;

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> findAll() {
        return pedidoRepository.findAll().stream()
                .map(PedidoResponseDTO::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO findById(Long id) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recurso não encontrado com ID: " + id)
        );
        return new PedidoResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO savePedido(PedidoRequestDTO pedidoDTO) {
        Pedido pedido = new Pedido();
        pedido.setData(LocalDate.now());
        pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDTO,pedido);
        pedido.calcularValorTotalPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoResponseDTO(pedido);
    }

    @Transactional
    public PedidoResponseDTO updatePedido(Long id, PedidoRequestDTO pedidoDTO) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Pedido com ID: " + id + " não encontrado")
        );
        if (pedido.getStatus().equals(Status.PAGO)) {
            throw new PedidoPagoException(
                    String.format("Pedido id: %d já está PAGO e não pode ser alterado", id)
            );
        }
        pedido.getItens().clear();
        pedido.setData(LocalDate.now());
        //pedido.setStatus(Status.CRIADO);
        mapDtoToPedido(pedidoDTO,pedido);
        pedido.calcularValorTotalPedido();
        pedido = pedidoRepository.save(pedido);
        return new PedidoResponseDTO(pedido);
    }

    @Transactional
    public void deleteProduto(Long id) {
        if(!pedidoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pedido com ID: " + id + " não encontrado");
        }
        pedidoRepository.deleteById(id);
    }

    @Transactional
    public void confirmarPagamento(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);

        if(pedido.isEmpty()) {
            throw new ResourceNotFoundException("Pedido não encontrado com o ID: " + id);
        }

        pedido.get().setStatus(Status.PAGO);
        pedidoRepository.save(pedido.get());
    }

    private void mapDtoToPedido(PedidoRequestDTO pedidoDTO, Pedido pedido) {
        pedido.setNome(pedidoDTO.getNome());
        pedido.setCpf(pedidoDTO.getCpf());

        for (ItemPedidoResponseDTO itemDTO : pedidoDTO.getItens()) {
            ItemDoPedido itemPedido = new ItemDoPedido();
            itemPedido.setQuantidade(itemDTO.getQuantidade());
            itemPedido.setDescricao(itemDTO.getDescricao());
            itemPedido.setPrecoUnitario(itemDTO.getPrecoUnitario());
            itemPedido.setPedido(pedido);
            pedido.getItens().add(itemPedido);
        }
    }
}
