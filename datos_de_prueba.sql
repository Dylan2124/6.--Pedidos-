-- =========================================================
-- SCRIPT SQL: DATOS DE PRUEBA EXCLUSIVOS PARA PEDIDOS
-- Ejecuta esto en phpMyAdmin sobre la BD: db_pedidos
-- =========================================================

USE db_pedidos;

-- ---------------------------------------------------------
-- CASUÍSTICA DE PEDIDOS
-- ---------------------------------------------------------
INSERT INTO pedidos (id_usuario, fecha_creacion, estado, total) VALUES
(1, NOW(), 'PENDIENTE', 350000),   -- Pedido recién creado, a la espera de pago
(2, '2026-05-01 10:00:00', 'PAGADO', 45000),      -- Pedido exitoso antiguo
(1, '2026-05-05 15:30:00', 'CANCELADO', 120000),  -- Pedido que el usuario anuló o falló
(3, NOW(), 'EN_ENSAMBLAJE', 850000);              -- Pedido ya pagado y pasando a los técnicos

-- ---------------------------------------------------------
-- DETALLES PARA CADA PEDIDO
-- ---------------------------------------------------------
-- Detalles del Pedido 1 (Pendiente)
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(1, 101, 1, 250000),
(1, 105, 2, 50000);

-- Detalles del Pedido 2 (Pagado)
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(2, 201, 1, 45000);

-- Detalles del Pedido 3 (Cancelado)
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(3, 305, 1, 120000);

-- Detalles del Pedido 4 (En Ensamblaje - PC Gamer completo)
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(4, 501, 1, 350000),
(4, 502, 1, 200000),
(4, 503, 2, 150000);
