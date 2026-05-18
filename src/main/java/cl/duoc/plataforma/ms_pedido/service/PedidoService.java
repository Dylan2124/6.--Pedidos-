package cl.duoc.plataforma.ms_pedido.service;

import cl.duoc.plataforma.ms_pedido.dto.DetallePedidoDto;
import cl.duoc.plataforma.ms_pedido.dto.PedidoDto;
import cl.duoc.plataforma.ms_pedido.model.DetallePedido;
import cl.duoc.plataforma.ms_pedido.model.Pedido;
import cl.duoc.plataforma.ms_pedido.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import cl.duoc.plataforma.ms_pedido.client.InventarioClient;
import cl.duoc.plataforma.ms_pedido.dto.DescuentoStockRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: PedidoService.java
 * Capa de Servicio. Aquí reside la Lógica de Negocio.
 * Se encarga de validar, procesar datos, hacer cálculos, 
 * y llamar a los Repositorios para interactuar con la BD.
 * 
 * @Slf4j: Anotación de Lombok para habilitar logs ('log.info', 'log.error').
 * @RequiredArgsConstructor: Genera el constructor para inyectar dependencias (ej: PedidoRepository).
 * ═══════════════════════════════════════════════════
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final InventarioClient inventarioClient;

    /**
     * @Transactional asegura que si ocurre un error a mitad del proceso, 
     * todos los cambios en la base de datos se deshagan automáticamente (Rollback).
     */
    @Transactional
    public PedidoDto crearPedido(PedidoDto pedidoDto) {
        log.info("Iniciando proceso de creación de pedido para el usuario ID: {}", pedidoDto.getIdUsuario());

        try {
            // 1. Instanciamos la nueva entidad
            Pedido pedido = new Pedido();
            pedido.setIdUsuario(pedidoDto.getIdUsuario());
            pedido.setFechaCreacion(LocalDateTime.now());
            pedido.setEstado("PENDIENTE"); // Estado inicial por defecto

            int totalPedido = 0;

            // 2. Procesamos y agregamos cada uno de los detalles (productos)
            for (DetallePedidoDto dtoDetalle : pedidoDto.getDetalles()) {
                DetallePedido detalle = new DetallePedido();
                detalle.setIdProducto(dtoDetalle.getIdProducto());
                detalle.setCantidad(dtoDetalle.getCantidad());
                detalle.setPrecioUnitario(dtoDetalle.getPrecioUnitario());
                
                // Calculamos el subtotal de este detalle y lo sumamos al total general
                totalPedido += (detalle.getCantidad() * detalle.getPrecioUnitario());
                
                // Usamos el método Helper definido en la entidad para enlazar la relación
                pedido.addDetalle(detalle);
            }

            // 3. Llamada al Microservicio de Inventario para descontar el stock
            try {
                log.info("Comunicando con ms-inventario para descontar stock...");
                List<DescuentoStockRequest> listaDescuentos = pedidoDto.getDetalles().stream()
                        .map(d -> new DescuentoStockRequest(d.getIdProducto(), d.getCantidad()))
                        .collect(Collectors.toList());
                        
                inventarioClient.descontarStock(listaDescuentos);
                log.info("Stock descontado exitosamente en ms-inventario.");
            } catch (Exception e) {
                // Tolerancia a fallos: Registramos el error pero permitimos que el pedido se guarde
                // de lo contrario, si el equipo de inventario no ha terminado, nuestro servicio fallaría.
                log.warn("ATENCION: No se pudo comunicar con ms-inventario para descontar stock. El pedido continuará su curso. Error: {}", e.getMessage());
            }

            // 3. Establecemos el total calculado dinámicamente
            pedido.setTotal(totalPedido);

            // 4. Guardamos en la base de datos (Persistencia). 
            // Esto guarda tanto el Pedido como sus Detalles gracias al 'cascade = CascadeType.ALL'
            Pedido savedPedido = pedidoRepository.save(pedido);
            log.info("Pedido creado exitosamente con ID: {}", savedPedido.getId());

            // 5. Retornamos la respuesta mapeada a DTO
            return mapToDto(savedPedido);
        } catch (Exception e) {
            log.error("Error crítico al crear el pedido: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno al procesar el pedido", e);
        }
    }

    /**
     * @Transactional(readOnly = true) optimiza las consultas de solo lectura en Hibernate.
     */
    @Transactional(readOnly = true)
    public List<PedidoDto> obtenerPedidosPorUsuario(Integer idUsuario) {
        log.info("Consultando pedidos históricos para el usuario ID: {}", idUsuario);
        return pedidoRepository.findByIdUsuario(idUsuario).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<PedidoDto> obtenerPedidoPorId(Long idPedido) {
        log.info("Buscando pedido específico por ID: {}", idPedido);
        return pedidoRepository.findById(idPedido)
                .map(this::mapToDto);
    }

    @Transactional
    public Optional<PedidoDto> actualizarEstado(Long idPedido, String nuevoEstado) {
        log.info("Solicitud para actualizar estado del pedido ID: {} a '{}'", idPedido, nuevoEstado);
        return pedidoRepository.findById(idPedido)
                .map(pedido -> {
                    pedido.setEstado(nuevoEstado);
                    Pedido updatedPedido = pedidoRepository.save(pedido);
                    return mapToDto(updatedPedido);
                });
    }

    /**
     * Función privada para transformar una Entidad JPA (Base de Datos)
     * a un Objeto DTO (Respuesta API).
     */
    private PedidoDto mapToDto(Pedido pedido) {
        List<DetallePedidoDto> detallesDto = pedido.getDetalles().stream().map(d -> 
            DetallePedidoDto.builder()
                .id(d.getId())
                .idProducto(d.getIdProducto())
                .cantidad(d.getCantidad())
                .precioUnitario(d.getPrecioUnitario())
                .build()
        ).collect(Collectors.toList());

        return PedidoDto.builder()
                .id(pedido.getId())
                .idUsuario(pedido.getIdUsuario())
                .fechaCreacion(pedido.getFechaCreacion())
                .estado(pedido.getEstado())
                .total(pedido.getTotal())
                .detalles(detallesDto)
                .build();
    }
}
