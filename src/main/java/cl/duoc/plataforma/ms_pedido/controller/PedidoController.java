package cl.duoc.plataforma.ms_pedido.controller;

import cl.duoc.plataforma.ms_pedido.dto.PedidoDto;
import cl.duoc.plataforma.ms_pedido.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: PedidoController.java
 * Capa Controladora (REST API). 
 * Recibe peticiones HTTP, delega la lógica al Service, 
 * y retorna respuestas estructuradas al cliente (JSON).
 * ═══════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/pedidos") // Ruta base para todos los endpoints de este controlador
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión del ciclo de vida de los pedidos y control de stock remotos.")
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Endpoint para crear un nuevo pedido.
     * @Valid activa las validaciones configuradas en PedidoDto (JSR 380).
     */
    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Registra un pedido en el sistema, calcula subtotales/totales dinámicamente y solicita descuento de stock en el microservicio de inventario.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Pedido creado exitosamente", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o faltantes en el request body")
    })
    public ResponseEntity<PedidoDto> crearPedido(
            @Valid @RequestBody @Parameter(description = "DTO con la información del pedido y sus detalles") PedidoDto pedidoDto) {
        PedidoDto nuevoPedido = pedidoService.crearPedido(pedidoDto);
        // Retornamos 201 CREATED si la creación fue exitosa
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener todos los pedidos de un usuario específico.
     * @PathVariable captura el valor de '{idUsuario}' desde la URL.
     */
    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener pedidos por usuario", description = "Retorna una lista de todos los pedidos históricos registrados por un usuario en particular.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedidos del usuario obtenidos con éxito")
    })
    public ResponseEntity<List<PedidoDto>> obtenerPedidosPorUsuario(
            @PathVariable @Parameter(description = "ID del usuario", example = "10") Integer idUsuario) {
        List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorUsuario(idUsuario);
        // Retornamos 200 OK con la lista
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para obtener un solo pedido utilizando su ID.
     */
    @GetMapping("/{idPedido}")
    @Operation(summary = "Obtener pedido por ID", description = "Obtiene los detalles completos de un pedido específico a partir de su ID único.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pedido encontrado y retornado", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDto.class))),
        @ApiResponse(responseCode = "404", description = "El pedido con el ID especificado no fue encontrado")
    })
    public ResponseEntity<?> obtenerPedidoPorId(
            @PathVariable @Parameter(description = "ID del pedido", example = "100") Long idPedido) {
        Optional<PedidoDto> pedidoOpt = pedidoService.obtenerPedidoPorId(idPedido);
        if (pedidoOpt.isPresent()) {
            return ResponseEntity.ok(pedidoOpt.get());
        } else {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", LocalDateTime.now());
            errorBody.put("status", HttpStatus.NOT_FOUND.value());
            errorBody.put("error", "Pedido no encontrado con el ID: " + idPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }
    }

    /**
     * Endpoint para actualizar únicamente el estado de un pedido.
     * @RequestParam captura el valor enviado como un parámetro de la URL (ej: ?estado=PAGADO)
     */
    @PutMapping("/{idPedido}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Modifica el estado de procesamiento del pedido (ej: PAGADO, PENDIENTE, ENVIADO).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estado del pedido actualizado con éxito", 
                     content = @Content(mediaType = "application/json", schema = @Schema(implementation = PedidoDto.class))),
        @ApiResponse(responseCode = "404", description = "El pedido con el ID especificado no fue encontrado")
    })
    public ResponseEntity<?> actualizarEstado(
            @PathVariable @Parameter(description = "ID del pedido a actualizar", example = "100") Long idPedido, 
            @RequestParam @Parameter(description = "Nuevo estado a aplicar al pedido (ej: PAGADO, RECHAZADO)", example = "PAGADO") String estado) {
        Optional<PedidoDto> pedidoOpt = pedidoService.actualizarEstado(idPedido, estado);
        if (pedidoOpt.isPresent()) {
            return ResponseEntity.ok(pedidoOpt.get());
        } else {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put("timestamp", LocalDateTime.now());
            errorBody.put("status", HttpStatus.NOT_FOUND.value());
            errorBody.put("error", "Pedido no encontrado con el ID: " + idPedido);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        }
    }
}
