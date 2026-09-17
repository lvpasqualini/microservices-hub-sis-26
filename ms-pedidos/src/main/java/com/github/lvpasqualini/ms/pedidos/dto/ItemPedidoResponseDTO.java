package com.github.lvpasqualini.ms.pedidos.dto;

import com.github.lvpasqualini.ms.pedidos.entities.ItemDoPedido;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemPedidoResponseDTO {
    private Long id;
    private Integer quantidade;
    private String descricao;
    private BigDecimal precoUnitario;

    public ItemPedidoResponseDTO(ItemDoPedido itemDoPedido) {
        this.id = itemDoPedido.getId();
        this.quantidade = itemDoPedido.getQuantidade();
        this.descricao = itemDoPedido.getDescricao();
        this.precoUnitario = itemDoPedido.getPrecoUnitario();
    }
}