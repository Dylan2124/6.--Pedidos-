package cl.duoc.plataforma.ms_pedido.repository;

import cl.duoc.plataforma.ms_pedido.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByIdUsuario(Integer idUsuario);
}
