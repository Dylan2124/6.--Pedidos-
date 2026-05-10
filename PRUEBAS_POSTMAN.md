# Pruebas para Postman (Microservicios)

Aquí tienes los bloques de código JSON listos para **llegar, copiar y pegar** en la pestaña **"Body" (Raw -> JSON)** de Postman.

---

## MICROSERVICIO: PEDIDOS (Puerto 8081)

### 1. Crear un nuevo Pedido
- **Método:** `POST`
- **URL:** `http://localhost:8081/api/pedidos`
- **Body JSON:**
```json
{
  "idUsuario": 1,
  "detalles": [
    {
      "idProducto": 101,
      "cantidad": 1,
      "precioUnitario": 150000
    },
    {
      "idProducto": 105,
      "cantidad": 2,
      "precioUnitario": 25000
    }
  ]
}
```

### 2. Buscar todos los pedidos de un Usuario
- **Método:** `GET`
- **URL:** `http://localhost:8081/api/pedidos/usuario/1`
- **Body:** *(No requiere Body, solo enviar)*

### 3. Buscar un Pedido específico por su ID
- **Método:** `GET`
- **URL:** `http://localhost:8081/api/pedidos/1`
- **Body:** *(No requiere Body)*

### 4. Actualizar Estado de un Pedido (Simulación manual)
- **Método:** `PUT`
- **URL:** `http://localhost:8081/api/pedidos/1/estado?estado=PAGADO`
- **Body:** *(No requiere Body, el parámetro va en la URL)*

---

## MICROSERVICIO: PAGOS (Puerto 8082)

### 5. Procesar el Pago de un Pedido
*(Recuerda iniciar el servidor de Pagos también. Cambia el "idPedido" por el número que te haya devuelto la creación del pedido).*
- **Método:** `POST`
- **URL:** `http://localhost:8082/api/pagos`
- **Body JSON:**
```json
{
  "idPedido": 1,
  "monto": 200000,
  "metodoPago": "WEBPAY",
  "rutCliente": "19.123.456-7"
}
```

### 6. Ver el historial de pagos de un Pedido
- **Método:** `GET`
- **URL:** `http://localhost:8082/api/pagos/pedido/1`
- **Body:** *(No requiere Body)*
