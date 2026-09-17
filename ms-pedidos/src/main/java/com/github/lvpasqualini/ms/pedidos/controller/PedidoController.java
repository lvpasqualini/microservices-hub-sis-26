package com.github.lvpasqualini.ms.pedidos.controller;

import com.github.lvpasqualini.ms.pedidos.dto.PedidoRequestDTO;
import com.github.lvpasqualini.ms.pedidos.dto.PedidoResponseDTO;
import com.github.lvpasqualini.ms.pedidos.services.PedidoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/port")
    public String port(@Value("${local.server.port}") String porta) {
        return "Instância respondeu na porta " + porta;
    }

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> findAll() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> findById(@PathVariable Long id) {
        PedidoResponseDTO pedidoDTO = pedidoService.findById(id);
        return ResponseEntity.ok(pedidoDTO);
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> createPedido(@Valid @RequestBody PedidoRequestDTO pedidoDTO) {
        PedidoResponseDTO responseDTO = pedidoService.savePedido(pedidoDTO);

        URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId())
                .toUri();

        return ResponseEntity.created(uri).body(responseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> updatePedido(@Valid @PathVariable Long id, @RequestBody PedidoRequestDTO pedidoDTO) {
        PedidoResponseDTO dto = pedidoService.updatePedido(id,pedidoDTO);

        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{pedidoId}/pagamento/confirmado")
    public void confirmarPagamento(@PathVariable Long pedidoId) {
        pedidoService.confirmarPagamento(pedidoId);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.deleteProduto(id);
        return ResponseEntity.noContent().build();
    }
}
