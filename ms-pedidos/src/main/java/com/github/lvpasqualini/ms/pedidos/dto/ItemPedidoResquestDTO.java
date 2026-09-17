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
public class ItemPedidoResquestDTO {
    @NotNull(message = "Quantidade requerido")
    @Positive(message = "Quantidade deve ser um número positivo")
    private Integer quantidade;
    @NotBlank(message = "Descrição requerido")
    private String descricao;
    @NotNull(message = "Preço unitário é requerido")
    @Positive(message = "O preço deve ser um número positivo e maior que zero")
    private BigDecimal precoUnitario;

}