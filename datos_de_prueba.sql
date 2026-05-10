-- =========================================================
-- SCRIPT SQL: DATOS DE PRUEBA (DUMMY DATA)
-- Puedes copiar y pegar estos bloques directamente en 
-- la pestaña "SQL" de tu phpMyAdmin en XAMPP.
--
-- IMPORTANTE: Ejecuta los microservicios en Java AL MENOS 
-- UNA VEZ antes de inyectar estos datos, para que Spring Boot 
-- se encargue de crear las tablas vacías automáticamente.
-- =========================================================

-- ---------------------------------------------------------
-- 1. BASE DE DATOS: db_pedidos (Para probar GET de ms-pedido)
-- ---------------------------------------------------------
USE db_pedidos;

-- Insertar 2 pedidos históricos
INSERT INTO pedidos (id_usuario, fecha_creacion, estado, total) VALUES
(1, NOW(), 'PENDIENTE', 350000),
(2, NOW(), 'PAGADO', 45000);

-- Insertar los productos dentro del pedido 1
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(1, 101, 1, 250000),
(1, 105, 2, 50000);

-- Insertar productos dentro del pedido 2
INSERT INTO detalles_pedido (pedido_id, id_producto, cantidad, precio_unitario) VALUES
(2, 201, 1, 45000);


-- ---------------------------------------------------------
-- 2. BASE DE DATOS: db_pagos (Para probar GET de ms-pagos)
-- ---------------------------------------------------------
USE db_pagos;

-- Simular que el Pedido 2 de arriba ya fue pagado exitosamente
INSERT INTO transacciones_pago (id_pedido, monto, metodo_pago, fecha_pago, estado_pago) VALUES
(2, 45000, 'WEBPAY', NOW(), 'APROBADO');

-- Asociar la factura a esa transacción (asumiendo que la transacción anterior quedó con ID 1)
INSERT INTO facturas (transaccion_id, rut_cliente, url_documento) VALUES
(1, '19.123.456-7', 'https://storage.plataforma.com/facturas/2.pdf');


-- ---------------------------------------------------------
-- 3. BASE DE DATOS: db_compatibilidad 
-- (El diccionario maestro. Muy útil para inyectar todo de golpe)
-- ---------------------------------------------------------
USE db_compatibilidad;

-- Reglas de Sockets compatibles
INSERT INTO reglas_socket (tipo_componente, nombre_socket, generacion_soportada) VALUES
('CPU', 'AM4', 'Ryzen Serie 5000'),
('PLACA_MADRE', 'AM4', 'Ryzen Serie 5000'),
('CPU', 'LGA1700', 'Intel Core 12va y 13va Gen'),
('PLACA_MADRE', 'LGA1700', 'Intel Core 12va y 13va Gen');

-- Reglas de Fuentes de Poder recomendadas
INSERT INTO reglas_energia (consumo_watts_min, consumo_watts_max, fuente_recomendada_watts) VALUES
(0, 300, 500),
(301, 500, 750),
(501, 1000, 1000);
