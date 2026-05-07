package cl.duoc.plataforma.ms_pedido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: Pedido.java
 * Representa la entidad (tabla) 'pedidos' en la base de datos.
 * Maneja la información general de una transacción de compra.
 * ═══════════════════════════════════════════════════
 */
@Entity
@Table(name = "pedidos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    /**
     * Identificador único del pedido (Clave Primaria).
     * El motor de base de datos lo genera de forma autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Referencia lógica (ID numérico) al microservicio de Usuarios.
     * Según las reglas del proyecto, NO debe ser una clave foránea (Foreign Key)
     * para mantener las "islas de datos" independientes.
     */
    @Column(nullable = false)
    private Integer idUsuario;

    /**
     * Fecha y hora exacta de la creación de la orden.
     */
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Estado del ciclo de vida del pedido.
     * Posibles valores: PENDIENTE, PAGADO, CANCELADO, etc.
     */
    @Column(nullable = false, length = 20)
    private String estado;

    /**
     * Sumatoria del costo de todos los detalles del pedido.
     */
    @Column(nullable = false)
    private Integer total;

    /**
     * Relación Uno a Muchos (1:N) con los Detalles del Pedido.
     * cascade = CascadeType.ALL permite guardar el pedido y sus detalles al mismo tiempo.
     */
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    /**
     * Método auxiliar (Helper) para gestionar la relación bidireccional.
     * Agrega el detalle a la lista y automáticamente enlaza este pedido
     * como el "padre" de ese detalle.
     *
     * @param detalle El objeto DetallePedido a vincular.
     */
    public void addDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
}
