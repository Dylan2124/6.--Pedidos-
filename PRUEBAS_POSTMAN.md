# Pruebas para Postman: Microservicio de Pedidos

Aquí tienes los bloques de código JSON listos para probar el **ms-pedido** (Puerto 8081).

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
- **Body:** *(No requiere)*

### 3. Buscar un Pedido específico por su ID
- **Método:** `GET`
- **URL:** `http://localhost:8081/api/pedidos/1`
- **Body:** *(No requiere)*

### 4. Actualizar Estado de un Pedido
- **Método:** `PUT`
- **URL:** `http://localhost:8081/api/pedidos/1/estado?estado=PAGADO`
- **Body:** *(No requiere)*
