package cl.duoc.plataforma.ms_pedido.client;

import cl.duoc.plataforma.ms_pedido.dto.DescuentoStockRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ms-inventario", url = "http://localhost:8083/api/stock")
public interface InventarioClient {

    @PutMapping("/descontar")
    void descontarStock(@RequestBody List<DescuentoStockRequest> requests);
}
