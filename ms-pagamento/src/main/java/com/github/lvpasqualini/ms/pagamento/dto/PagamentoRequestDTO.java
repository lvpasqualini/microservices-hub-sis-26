package com.github.lvpasqualini.ms.pagamento.dto;

import com.github.lvpasqualini.ms.pagamento.entities.Pagamento;
import com.github.lvpasqualini.ms.pagamento.entities.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PagamentoRequestDTO {
    @NotNull(message = "O valor não pode ser nulo")
    @Positive(message = "O valor deve ser maior que zero!")
    private BigDecimal valor;
    @NotBlank(message = "O campo nome deve ser informado")
    @Size(min = 3, max = 100,message = "O nome deve ter entre 3 a 100 caracteres")
    private String nome;
    @NotBlank(message = "O campo numero do cartão deve ser informado")
    @Size(min = 16, max = 16,message = "O numero do cartao deve ter 16 caracteres")
    private String numeroCartao;
    @NotBlank(message = "O campo validade deve ser informado")
    @Size(min = 5, max = 5,message = "A validade deve ter 5 caracteres")
    private String validade;
    @NotBlank(message = "O campo codigo de seguranca deve ser informado")
    @Size(min = 3, max = 3,message = "O codigo de seguranca deve ter 3 caracteres")
    private String codigoSeguranca;
    @NotNull(message = "O campo ID do pedido é obrigatorio")
    @Positive(message = "O campo ID deve ser positivo ou maior que zero")
    private Long pedidoId;

    public PagamentoRequestDTO(Pagamento pagamento) {
        this.valor = pagamento.getValor();
        this.nome = pagamento.getNome();
        this.numeroCartao = pagamento.getNumeroCartao();
        this.validade = pagamento.getValidade();
        this.codigoSeguranca = pagamento.getCodigoSeguranca();
        this.pedidoId = pagamento.getPedidoId();
    }
}
