INSERT INTO pedidos (id_usuario, fecha_creacion, estado, total) VALUES
(1, '2026-05-10 10:00:00', 'PENDIENTE', 500000),
(2, '2026-05-11 11:30:00', 'PAGADO', 850000),
(1, '2026-05-12 14:15:00', 'CANCELADO', 120000),
(3, '2026-05-15 09:20:00', 'PAGADO', 1500000),
(4, '2026-05-16 16:45:00', 'DESPACHADO', 2100000);

INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(1, 101, 1, 200000),
(1, 102, 2, 150000),
(2, 201, 1, 850000),
(3, 301, 1, 120000),
(4, 401, 1, 1000000),
(4, 402, 1, 500000),
(5, 501, 2, 1050000);
