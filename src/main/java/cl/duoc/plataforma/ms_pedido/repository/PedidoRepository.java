package cl.duoc.plataforma.ms_pedido.repository;

import cl.duoc.plataforma.ms_pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ═══════════════════════════════════════════════════
 * INTERFAZ: PedidoRepository.java
 * Capa de Acceso a Datos (Repository).
 * JpaRepository provee automáticamente todos los métodos
 * CRUD (save, findById, findAll, delete, etc.) sin necesidad
 * de escribir consultas SQL manualmente.
 * ═══════════════════════════════════════════════════
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    /**
     * Método de búsqueda personalizado (Query Method).
     * Spring Data JPA automáticamente genera la consulta SQL:
     * "SELECT * FROM pedidos WHERE id_usuario = ?"
     * basándose simplemente en el nombre del método.
     * 
     * @param idUsuario ID numérico del usuario.
     * @return Lista de pedidos pertenecientes al usuario.
     */
    List<Pedido> findByIdUsuario(Integer idUsuario);
}
