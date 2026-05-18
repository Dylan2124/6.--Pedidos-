-- =========================================================
-- SCRIPT SQL: DATOS DE PRUEBA EXCLUSIVOS PARA PEDIDOS
-- Ejecuta esto en phpMyAdmin sobre la BD: db_pedidos
-- =========================================================

USE db_pedidos;

-- ---------------------------------------------------------
-- CASUÍSTICA DE PEDIDOS Y DETALLES
-- ---------------------------------------------------------

-- Limpiar tablas previas para evitar duplicados si lo vuelves a ejecutar
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE detalles_pedido;
TRUNCATE TABLE pedidos;
SET FOREIGN_KEY_CHECKS = 1;

-- 1. Insertar Pedidos
INSERT INTO pedidos (id_usuario, fecha_creacion, estado, total) VALUES
(1, NOW(), 'PENDIENTE', 500000),  -- Pedido ID 1 (El que estás probando en Pagos)
(2, NOW(), 'PAGADO', 850000),     -- Pedido ID 2
(1, NOW(), 'CANCELADO', 120000),  -- Pedido ID 3
(3, NOW(), 'PAGADO', 1500000),    -- Pedido ID 4
(4, NOW(), 'DESPACHADO', 2100000);-- Pedido ID 5

-- 2. Insertar Detalles de los Pedidos (amarrados a sus respectivos pedido_id)
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(1, 101, 1, 200000),
(1, 102, 2, 150000),
(2, 201, 1, 850000),
(3, 301, 1, 120000),
(4, 401, 1, 1000000),
(4, 402, 1, 500000),
(5, 501, 2, 1050000);
