package cl.duoc.plataforma.ms_pedido.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * ═══════════════════════════════════════════════════
 * CLASE: DetallePedido.java
 * Representa la entidad (tabla) 'detalles_pedido'.
 * Guarda los productos específicos que pertenecen a una orden.
 * ═══════════════════════════════════════════════════
 */
@Entity
@Table(name = "detalles_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePedido {

    /**
     * Identificador único del detalle. Autoincremental.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación Muchos a Uno (N:1) con la entidad Pedido.
     * Esta es la clave foránea interna (pedido_id) dentro de nuestra "isla".
     * @ToString.Exclude evita errores de bucle infinito (StackOverflow) al imprimir.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @ToString.Exclude
    private Pedido pedido;

    /**
     * Referencia lógica al microservicio de Catálogo.
     */
    @Column(nullable = false)
    private Integer idProducto;

    /**
     * Cantidad de unidades de este producto en específico.
     */
    @Column(nullable = false)
    private Integer cantidad;

    /**
     * Valor unitario del producto al momento de hacer la compra.
     */
    @Column(nullable = false)
    private Integer precioUnitario;
}
