package com.github.lvpasqualini.ms.pagamento.controller;

import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.service.PagamentoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pagamento")
public class PagamentoController {
    @Autowired
    private PagamentoService pagamentoService;

    @GetMapping
    public ResponseEntity<List<PagamentoDTO>> findAll() {
         return ResponseEntity.ok(pagamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PagamentoDTO> save(@Valid @RequestBody PagamentoDTO pagamentoDTO) {
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(pagamentoDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(pagamentoService.save(pagamentoDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoDTO> update(@PathVariable Long id,@Valid @RequestBody PagamentoDTO pagamentoDTO) {
        pagamentoDTO = pagamentoService.update(id,pagamentoDTO);
        return ResponseEntity.ok(pagamentoDTO);
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido",
                    fallbackMethod = "fallbackConfirmarPagamentoPendente")
    public ResponseEntity<PagamentoDTO> confirmarPagamentoDoPedido(@PathVariable
                                                                       @NotNull Long id) {
        PagamentoDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<PagamentoDTO> fallbackConfirmarPagamentoPendente(Long id, Throwable e){
        // Registra o erro para fins de log/observabilidade
        log.error("Falha ao confirmar pedido {}. Ativando fallback. Erro: {}", id, e.getMessage());
        PagamentoDTO dto = pagamentoService.alterarStatusDoPagamento(id);
        // 503 - explicitar que o serviço destino falhou, mas ainda assim enviando o corpo.
        return ResponseEntity.status(503).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagamentoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
