package cl.duoc.plataforma.ms_pedido.service;

import cl.duoc.plataforma.ms_pedido.dto.DetallePedidoDto;
import cl.duoc.plataforma.ms_pedido.dto.PedidoDto;
import cl.duoc.plataforma.ms_pedido.model.DetallePedido;
import cl.duoc.plataforma.ms_pedido.model.Pedido;
import cl.duoc.plataforma.ms_pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    @Transactional
    public PedidoDto crearPedido(PedidoDto pedidoDto) {
        log.info("Creando nuevo pedido para usuario ID: {}", pedidoDto.getIdUsuario());

        try {
            Pedido pedido = new Pedido();
            pedido.setIdUsuario(pedidoDto.getIdUsuario());
            pedido.setFechaCreacion(LocalDateTime.now());
            pedido.setEstado("PENDIENTE");

            int totalPedido = 0;

            for (DetallePedidoDto dtoDetalle : pedidoDto.getDetalles()) {
                DetallePedido detalle = new DetallePedido();
                detalle.setIdProducto(dtoDetalle.getIdProducto());
                detalle.setCantidad(dtoDetalle.getCantidad());
                detalle.setPrecioUnitario(dtoDetalle.getPrecioUnitario());
                
                totalPedido += (detalle.getCantidad() * detalle.getPrecioUnitario());
                pedido.addDetalle(detalle);
            }

            pedido.setTotal(totalPedido);

            Pedido savedPedido = pedidoRepository.save(pedido);
            log.info("Pedido creado exitosamente con ID: {}", savedPedido.getIdPedido());

            return mapToDto(savedPedido);
        } catch (Exception e) {
            log.error("Error al crear el pedido: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar el pedido", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PedidoDto> obtenerPedidosPorUsuario(Integer idUsuario) {
        log.info("Obteniendo pedidos para usuario ID: {}", idUsuario);
        return pedidoRepository.findByIdUsuario(idUsuario).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoDto obtenerPedidoPorId(Long idPedido) {
        log.info("Obteniendo pedido por ID: {}", idPedido);
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + idPedido));
        return mapToDto(pedido);
    }

    @Transactional
    public PedidoDto actualizarEstado(Long idPedido, String nuevoEstado) {
        log.info("Actualizando estado de pedido ID: {} a {}", idPedido, nuevoEstado);
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con ID: " + idPedido));
        
        pedido.setEstado(nuevoEstado);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        
        return mapToDto(updatedPedido);
    }

    private PedidoDto mapToDto(Pedido pedido) {
        List<DetallePedidoDto> detallesDto = pedido.getDetalles().stream().map(d -> 
            DetallePedidoDto.builder()
                .idDetalle(d.getIdDetalle())
                .idProducto(d.getIdProducto())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .build()
        ).collect(Collectors.toList());

        return PedidoDto.builder()
                .idPedido(pedido.getIdPedido())
                .idUsuario(pedido.getIdUsuario())
                .fechaCreacion(pedido.getFechaCreacion())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .detalles(detallesDto)
                .build();
    }
}
