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
public class PedidoRequestDTO {
    @NotBlank(message = "Nome é requerido")
    @Size(min = 3, max = 100,message = "O nome deve ter entre 3 a 100 caracteres")
    private String nome;

    @NotBlank(message = "CPF é requerido")
    @Size(min = 11,max = 11, message = "O CPF deve ter 11 caracteres")
    private String cpf;

    @NotEmpty(message = "Pedido deve ter pelo menos um item")
    private List<@Valid ItemPedidoResponseDTO> itens = new ArrayList<>();

    public PedidoRequestDTO(Pedido pedido) {
        this.nome = pedido.getNome();
        this.cpf = pedido.getCpf();
        this.itens = pedido.getItens().stream().map(ItemPedidoResponseDTO::new).toList();
    }
}
