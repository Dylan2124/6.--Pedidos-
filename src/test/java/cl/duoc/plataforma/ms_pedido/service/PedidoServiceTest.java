package cl.duoc.plataforma.ms_pedido.service;

import cl.duoc.plataforma.ms_pedido.client.InventarioClient;
import cl.duoc.plataforma.ms_pedido.dto.DetallePedidoDto;
import cl.duoc.plataforma.ms_pedido.dto.PedidoDto;
import cl.duoc.plataforma.ms_pedido.model.DetallePedido;
import cl.duoc.plataforma.ms_pedido.model.Pedido;
import cl.duoc.plataforma.ms_pedido.repository.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private PedidoService pedidoService;

    // ── PRUEBAS: crearPedido ────────────────────────────────────────────────

    @Test
    void testCrearPedido_Success_ShouldDescountStockAndSave() {
        // Dado
        DetallePedidoDto detailDto = DetallePedidoDto.builder()
                .idProducto(10)
                .cantidad(2)
                .precioUnitario(1500)
                .build();

        PedidoDto requestDto = PedidoDto.builder()
                .idUsuario(1)
                .detalles(Collections.singletonList(detailDto))
                .build();

        // Creamos la entidad que retornará el repositorio mock
        Pedido savedPedido = new Pedido();
        savedPedido.setId(100L);
        savedPedido.setIdUsuario(1);
        savedPedido.setFechaCreacion(LocalDateTime.now());
        savedPedido.setEstado("PENDIENTE");
        savedPedido.setTotal(3000); // 2 * 1500

        DetallePedido detailEntity = new DetallePedido();
        detailEntity.setId(1L);
        detailEntity.setIdProducto(10);
        detailEntity.setCantidad(2);
        detailEntity.setPrecioUnitario(1500);
        savedPedido.addDetalle(detailEntity);

        doNothing().when(inventarioClient).descontarStock(anyList());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(savedPedido);

        // Cuando
        PedidoDto result = pedidoService.crearPedido(requestDto);

        // Entonces
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(1, result.getIdUsuario());
        assertEquals("PENDIENTE", result.getEstado());
        assertEquals(3000, result.getTotal());
        assertEquals(1, result.getDetalles().size());
        assertEquals(10, result.getDetalles().get(0).getIdProducto());
        assertEquals(2, result.getDetalles().get(0).getCantidad());
        assertEquals(1500, result.getDetalles().get(0).getPrecioUnitario());

        verify(inventarioClient, times(1)).descontarStock(anyList());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_InventoryFails_ShouldSaveAnyway() {
        // Dado
        DetallePedidoDto detailDto = DetallePedidoDto.builder()
                .idProducto(10)
                .cantidad(2)
                .precioUnitario(1500)
                .build();

        PedidoDto requestDto = PedidoDto.builder()
                .idUsuario(1)
                .detalles(Collections.singletonList(detailDto))
                .build();

        Pedido savedPedido = new Pedido();
        savedPedido.setId(100L);
        savedPedido.setIdUsuario(1);
        savedPedido.setFechaCreacion(LocalDateTime.now());
        savedPedido.setEstado("PENDIENTE");
        savedPedido.setTotal(3000);

        DetallePedido detailEntity = new DetallePedido();
        detailEntity.setId(1L);
        detailEntity.setIdProducto(10);
        detailEntity.setCantidad(2);
        detailEntity.setPrecioUnitario(1500);
        savedPedido.addDetalle(detailEntity);

        // Simula que el cliente de inventario lanza una excepción
        doThrow(new RuntimeException("ms-inventario offline")).when(inventarioClient).descontarStock(anyList());
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(savedPedido);

        // Cuando
        PedidoDto result = pedidoService.crearPedido(requestDto);

        // Entonces
        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(3000, result.getTotal());
        
        // Debe guardarse el pedido a pesar del fallo de inventario (tolerancia a fallos)
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCrearPedido_DatabaseSaveFails_ShouldThrowRuntimeException() {
        // Dado
        DetallePedidoDto detailDto = DetallePedidoDto.builder()
                .idProducto(10)
                .cantidad(2)
                .precioUnitario(1500)
                .build();

        PedidoDto requestDto = PedidoDto.builder()
                .idUsuario(1)
                .detalles(Collections.singletonList(detailDto))
                .build();

        when(pedidoRepository.save(any(Pedido.class))).thenThrow(new RuntimeException("Database error"));

        // Cuando y Entonces
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            pedidoService.crearPedido(requestDto);
        });

        assertTrue(exception.getMessage().contains("Error interno al procesar el pedido"));
    }

    // ── PRUEBAS: obtenerPedidosPorUsuario ───────────────────────────────────

    @Test
    void testObtenerPedidosPorUsuario_ShouldReturnList() {
        // Dado
        Pedido p1 = new Pedido();
        p1.setId(1L);
        p1.setIdUsuario(10);
        p1.setTotal(5000);

        Pedido p2 = new Pedido();
        p2.setId(2L);
        p2.setIdUsuario(10);
        p2.setTotal(8000);

        when(pedidoRepository.findByIdUsuario(10)).thenReturn(Arrays.asList(p1, p2));

        // Cuando
        List<PedidoDto> results = pedidoService.obtenerPedidosPorUsuario(10);

        // Entonces
        assertEquals(2, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals(2L, results.get(1).getId());
        verify(pedidoRepository, times(1)).findByIdUsuario(10);
    }

    // ── PRUEBAS: obtenerPedidoPorId ──────────────────────────────────────────

    @Test
    void testObtenerPedidoPorId_Found_ShouldReturnDto() {
        // Dado
        Pedido p = new Pedido();
        p.setId(100L);
        p.setIdUsuario(10);
        p.setTotal(5000);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(p));

        // Cuando
        Optional<PedidoDto> result = pedidoService.obtenerPedidoPorId(100L);

        // Entonces
        assertTrue(result.isPresent());
        assertEquals(100L, result.get().getId());
        verify(pedidoRepository, times(1)).findById(100L);
    }

    @Test
    void testObtenerPedidoPorId_NotFound_ShouldReturnEmpty() {
        // Dado
        when(pedidoRepository.findById(100L)).thenReturn(Optional.empty());

        // Cuando
        Optional<PedidoDto> result = pedidoService.obtenerPedidoPorId(100L);

        // Entonces
        assertFalse(result.isPresent());
        verify(pedidoRepository, times(1)).findById(100L);
    }

    // ── PRUEBAS: actualizarEstado ────────────────────────────────────────────

    @Test
    void testActualizarEstado_Found_ShouldUpdateAndSave() {
        // Dado
        Pedido p = new Pedido();
        p.setId(100L);
        p.setIdUsuario(10);
        p.setEstado("PENDIENTE");
        p.setTotal(5000);

        Pedido saved = new Pedido();
        saved.setId(100L);
        saved.setIdUsuario(10);
        saved.setEstado("PAGADO");
        saved.setTotal(5000);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(p));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(saved);

        // Cuando
        Optional<PedidoDto> result = pedidoService.actualizarEstado(100L, "PAGADO");

        // Entonces
        assertTrue(result.isPresent());
        assertEquals("PAGADO", result.get().getEstado());
        verify(pedidoRepository, times(1)).findById(100L);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testActualizarEstado_NotFound_ShouldReturnEmpty() {
        // Dado
        when(pedidoRepository.findById(100L)).thenReturn(Optional.empty());

        // Cuando
        Optional<PedidoDto> result = pedidoService.actualizarEstado(100L, "PAGADO");

        // Entonces
        assertFalse(result.isPresent());
        verify(pedidoRepository, times(1)).findById(100L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}
