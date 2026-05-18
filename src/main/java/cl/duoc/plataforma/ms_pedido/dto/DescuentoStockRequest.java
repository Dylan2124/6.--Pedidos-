package cl.duoc.plataforma.ms_pedido.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescuentoStockRequest {
    private Long idProducto;
    private Integer cantidad;
}
