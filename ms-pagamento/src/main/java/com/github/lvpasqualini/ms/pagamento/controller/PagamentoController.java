package com.github.lvpasqualini.ms.pagamento.controller;

import com.github.lvpasqualini.ms.pagamento.dto.PagamentoDTO;
import com.github.lvpasqualini.ms.pagamento.service.PagamentoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<PagamentoDTO> confirmarPagamentoDoPedido(@PathVariable
                                                                       @NotNull Long id) {
        PagamentoDTO dto = pagamentoService.confirmarPagamentoDoPedido(id);

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pagamentoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
