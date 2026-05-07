package cl.duoc.plataforma.ms_pedido.controller;

import cl.duoc.plataforma.ms_pedido.dto.PedidoDto;
import cl.duoc.plataforma.ms_pedido.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    public ResponseEntity<PedidoDto> crearPedido(@Valid @RequestBody PedidoDto pedidoDto) {
        PedidoDto nuevoPedido = pedidoService.crearPedido(pedidoDto);
        return new ResponseEntity<>(nuevoPedido, HttpStatus.CREATED);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PedidoDto>> obtenerPedidosPorUsuario(@PathVariable Integer idUsuario) {
        List<PedidoDto> pedidos = pedidoService.obtenerPedidosPorUsuario(idUsuario);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{idPedido}")
    public ResponseEntity<PedidoDto> obtenerPedidoPorId(@PathVariable Long idPedido) {
        PedidoDto pedido = pedidoService.obtenerPedidoPorId(idPedido);
        return ResponseEntity.ok(pedido);
    }

    @PutMapping("/{idPedido}/estado")
    public ResponseEntity<PedidoDto> actualizarEstado(@PathVariable Long idPedido, @RequestParam String estado) {
        PedidoDto pedidoActualizado = pedidoService.actualizarEstado(idPedido, estado);
        return ResponseEntity.ok(pedidoActualizado);
    }
}
