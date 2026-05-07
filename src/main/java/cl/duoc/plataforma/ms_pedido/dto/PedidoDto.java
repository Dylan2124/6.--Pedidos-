package cl.duoc.plataforma.ms_pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: PedidoDto.java
 * Data Transfer Object (DTO) para Pedidos.
 * Se utiliza para recibir datos desde el cliente (Postman/Frontend)
 * y para enviar respuestas, protegiendo así las entidades de la Base de Datos.
 * ═══════════════════════════════════════════════════
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {

    private Long id;

    /**
     * @NotNull valida que el cliente envíe obligatoriamente el ID del usuario.
     */
    @NotNull(message = "El idUsuario es obligatorio")
    private Integer idUsuario;

    private LocalDateTime fechaCreacion;

    private String estado;

    private Integer total;

    /**
     * @NotEmpty asegura que el pedido traiga al menos un producto (detalle) en la lista.
     * @Valid obliga a Spring Boot a revisar también las validaciones internas
     * de la clase DetallePedidoDto.
     */
    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Valid
    private List<DetallePedidoDto> detalles;
}
