package com.github.lvpasqualini.ms.pagamento.controller;

import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.dto.PagamentoRequestDTO;
import com.github.lvpasqualini.ms.pagamento.dto.PagamentoResponseDTO;
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
    public ResponseEntity<List<PagamentoResponseDTO>> findAll() {
         return ResponseEntity.ok(pagamentoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<PagamentoResponseDTO> save(@Valid @RequestBody PagamentoRequestDTO pagamentoDTO) {
        PagamentoResponseDTO responseDTO = pagamentoService.save(pagamentoDTO);
        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();
        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagamentoResponseDTO> update(@PathVariable Long id,@Valid @RequestBody PagamentoRequestDTO pagamentoDTO) {
        PagamentoResponseDTO responseDTO = pagamentoService.update(id,pagamentoDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/{id}/confirmar")
    @CircuitBreaker(name = "atualizarPedido",
                    fallbackMethod = "fallbackConfirmarPagamentoPendente")
    public ResponseEntity<PagamentoResponseDTO> confirmarPagamentoDoPedido(@PathVariable
                                                                       @NotNull Long id) {
        PagamentoResponseDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }

    public ResponseEntity<PagamentoResponseDTO> fallbackConfirmarPagamentoPendente(Long id, Throwable e){
        // Registra o erro para fins de log/observabilidade
        log.error("Falha ao confirmar pedido {}. Ativando fallback. Erro: {}", id, e.getMessage());
        PagamentoResponseDTO dto = pagamentoService.alterarStatusDoPagamento(id);
        // 503 - explicitar que o serviço destino falhou, mas ainda assim enviando o corpo.
        return ResponseEntity.status(503).body(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagamentoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
