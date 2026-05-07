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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDto {

    private Long idPedido;

    @NotNull(message = "El idUsuario es obligatorio")
    private Integer idUsuario;

    private LocalDateTime fechaCreacion;

    private String estado;

    private Integer total;

    @NotEmpty(message = "El pedido debe contener al menos un detalle")
    @Valid
    private List<DetallePedidoDto> detalles;
}
