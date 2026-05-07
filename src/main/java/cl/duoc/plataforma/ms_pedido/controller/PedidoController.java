package cl.duoc.plataforma.ms_pedido.controller;

import cl.duoc.plataforma.ms_pedido.dto.PedidoDto;
import cl.duoc.plataforma.ms_pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
public class PedidoController {

    private final PedidoService pedidoService;

    /**
     * Endpoint para crear un nuevo pedido.
     * @Valid activa las validaciones configuradas en PedidoDto (JSR 380).
     */
    @PostMapping
    public ResponseEntity<PedidoDto> crearPedido(@Valid @RequestBody PedidoDto pedidoDto) {
        PedidoDto nuevoPedido = pedidoService.crearPedido(pedidoDto);
        // Retornamos 201 CREATED si la creación fue exitosa
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener todos los pedidos de un usuario específico.
     * @PathVariable captura el valor de '{idUsuario}' desde la URL.
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PedidoDto>> obtenerPedidosPorUsuario(@PathVariable Integer idUsuario) {
        List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorUsuario(idUsuario);
        // Retornamos 200 OK con la lista
        return ResponseEntity.ok(pedidos);
    }

    /**
     * Endpoint para obtener un solo pedido utilizando su ID.
     */
    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDto> obtenerPedidoPorId(@PathVariable Long idPedido) {
        PedidoDto pedido = pedidoService.obtenerPedidoPorId(idPedido);
        return ResponseEntity.ok(pedido);
    }

    /**
     * Endpoint para actualizar únicamente el estado de un pedido.
     * @RequestParam captura el valor enviado como un parámetro de la URL (ej: ?estado=PAGADO)
     */
    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<PedidoDto> actualizarEstado(@PathVariable Long idPedido, @RequestParam String estado) {
        PedidoDto pedidoActualizado = pedidoService.actualizarEstado(idPedido, estado);
        return ResponseEntity.ok(pedidoActualizado);
    }
}
