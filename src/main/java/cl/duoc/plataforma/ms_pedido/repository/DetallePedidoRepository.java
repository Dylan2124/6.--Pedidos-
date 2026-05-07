package cl.duoc.plataforma.ms_pedido.repository;

import cl.duoc.plataforma.ms_pedido.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
}
