package cl.duoc.plataforma.ms_pedido.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: DetallePedidoDto.java
 * DTO para transportar la información de los productos
 * dentro de un pedido.
 * ═══════════════════════════════════════════════════
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDto {

    private Long id;

    @NotNull(message = "El idProducto es obligatorio")
    private Integer idProducto;

    /**
     * @Min(1) asegura que no puedan pedir "0" productos o cantidades negativas.
     */
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @Min(value = 1, message = "El precio unitario debe ser mayor a 0") // Ajustado para que no pueda tener valor 0
    private Integer precioUnitario;
}
