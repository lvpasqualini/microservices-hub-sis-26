package com.github.lvpasqualini.ms.pedidos.dto;

import com.github.lvpasqualini.ms.pedidos.entities.Pedido;
import com.github.lvpasqualini.ms.pedidos.entities.Status;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PedidoResponseDTO {
    private Long id;
    private String nome;
    private String cpf;
    private LocalDate data;
    @Enumerated(EnumType.STRING)
    private Status status;
    private BigDecimal valorTotal;

    private List<@Valid ItemPedidoResponseDTO> itens = new ArrayList<>();

    public PedidoResponseDTO(Pedido pedido) {
        this.id = pedido.getId();
        this.nome = pedido.getNome();
        this.cpf = pedido.getCpf();
        this.data = pedido.getData();
        this.status = pedido.getStatus();
        this.valorTotal = pedido.getValorTotal();
        this.itens = pedido.getItens().stream().map(ItemPedidoResponseDTO::new).toList();
    }
}
