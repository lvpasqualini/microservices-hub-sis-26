package com.github.lvpasqualini.ms.pagamento.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("ms-pedidos")
public interface PedidoClient {
    @RequestMapping(method = RequestMethod.PUT,
                    value = ("/pedidos/{pedidoId}/pagamento/confirmado"))
    void confirmarPagamento(@PathVariable Long pedidoId);
}
