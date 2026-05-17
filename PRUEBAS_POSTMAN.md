# 🧪 Guía de Pruebas de API y Control de Errores: Microservicio de Pedidos (`ms-pedido`)

Esta guía detalla la suite completa de pruebas para validar el correcto funcionamiento, las reglas de negocio y el control de excepciones en el microservicio **ms-pedido** (Puerto `8086`).

---

## ⚙️ Configuración del Entorno de Pruebas
* **Base URL:** `http://localhost:8086`
* **Content-Type:** `application/json`
* **Base de Datos:** MySQL (`db_pedidos` en puerto `3306`)

---

## 🎯 Resumen de Validaciones de Negocio (Bean Validation) y Control de Recursos
El microservicio utiliza **Jakarta Bean Validation** (JSR 380) para asegurar la integridad de los datos de entrada y el tipo de retorno **`java.util.Optional`** para el control de recursos existentes:

| Campo / Operación | Restricción / Estructura | Código HTTP Esperado | Mensaje de Error / Comportamiento |
| :--- | :--- | :--- | :--- |
| `idUsuario` | `@NotNull` | `400 Bad Request` | `"El idUsuario es obligatorio"` |
| `detalles` | `@NotEmpty` | `400 Bad Request` | `"El pedido debe contener al menos un detalle"` |
| `detalles[i].idProducto` | `@NotNull` | `400 Bad Request` | `"El idProducto es obligatorio"` |
| `detalles[i].cantidad` | `@NotNull`, `@Min(1)` | `400 Bad Request` | `"La cantidad debe ser mayor a 0"` |
| `detalles[i].precioUnitario` | `@NotNull`, `@Min(1)` | `400 Bad Request` | `"El precio unitario debe ser mayor a 0"` |
| **Buscar/Actualizar inexistente** | `Optional.orElse(404)` | `404 Not Found` | Cuerpo de respuesta vacío |

---

# 🟢 Escenarios Exitosos (Happy Paths)
Pruebas diseñadas para comprobar que el flujo operativo estándar de la aplicación funciona correctamente.

### 1. Crear un Pedido Válido y Calcular Total Dinámicamente
Valida que el pedido se guarde en la BD, que el estado por defecto sea `"PENDIENTE"`, y que la sumatoria total sea calculada como `(cantidad * precioUnitario)`.

* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "idUsuario": 1,
    "detalles": [
      {
        "idProducto": 101,
        "cantidad": 2,
        "precioUnitario": 15000
      },
      {
        "idProducto": 102,
        "cantidad": 3,
        "precioUnitario": 5000
      }
    ]
  }
  ```
* **Respuesta Esperada (HTTP 201 Created):**
  ```json
  {
    "id": 1,
    "idUsuario": 1,
    "fechaCreacion": "2026-05-17T03:20:00",
    "estado": "PENDIENTE",
    "total": 45000,
    "detalles": [
      {
        "id": 1,
        "idProducto": 101,
        "cantidad": 2,
        "precioUnitario": 15000
      },
      {
        "id": 2,
        "idProducto": 102,
        "cantidad": 3,
        "precioUnitario": 5000
      }
    ]
  }
  ```
  *(Nota: El `total` se autocalcula de forma interna: `(2 * 15000) + (3 * 5000) = 30000 + 15000 = 45000`)*

### 2. Buscar un Pedido Específico por su ID
* **Método:** `GET`
* **URL:** `http://localhost:8086/api/pedidos/1`
* **Respuesta Esperada (HTTP 200 OK):**
  *(Retorna el objeto JSON completo del pedido con el ID `1`).*

### 3. Obtener Historial de Pedidos por ID de Usuario
* **Método:** `GET`
* **URL:** `http://localhost:8086/api/pedidos/usuario/1`
* **Respuesta Esperada (HTTP 200 OK):**
  ```json
  [
    {
      "id": 1,
      "idUsuario": 1,
      "fechaCreacion": "2026-05-17T03:20:00",
      "estado": "PENDIENTE",
      "total": 45000,
      "detalles": [...]
    }
  ]
  ```

### 4. Actualizar Estado de un Pedido Existente
* **Método:** `PUT`
* **URL:** `http://localhost:8086/api/pedidos/1/estado?estado=PAGADO`
* **Respuesta Esperada (HTTP 200 OK):**
  *(Retorna el pedido con el campo `"estado"` actualizado a `"PAGADO"`).*

---

# 🔴 Escenarios de Error: Validaciones de Datos (HTTP 400 Bad Request)
Casos diseñados para forzar los límites del sistema utilizando datos incorrectos, vacíos o fuera de rango. Todos son capturados por el `MethodArgumentNotValidException` de `GlobalExceptionHandler`.

### 5. Error: Crear Pedido sin ID de Usuario (`idUsuario` Nulo)
* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "detalles": [
      {
        "idProducto": 101,
        "cantidad": 1,
        "precioUnitario": 15000
      }
    ]
  }
  ```
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "status": 400,
    "timestamp": "2026-05-17T03:21:00.123456",
    "errors": {
      "idUsuario": "El idUsuario es obligatorio"
    }
  }
  ```

### 6. Error: Crear Pedido sin Productos (`detalles` Vacío)
* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "idUsuario": 1,
    "detalles": []
  }
  ```
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "status": 400,
    "timestamp": "2026-05-17T03:22:00.123456",
    "errors": {
      "detalles": "El pedido debe contener al menos un detalle"
    }
  }
  ```

### 7. Error: Enviar Producto sin ID (`idProducto` Nulo)
* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "idUsuario": 1,
    "detalles": [
      {
        "cantidad": 2,
        "precioUnitario": 10000
      }
    ]
  }
  ```
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "status": 400,
    "timestamp": "2026-05-17T03:23:00.123456",
    "errors": {
      "detalles[0].idProducto": "El idProducto es obligatorio"
    }
  }
  ```

### 8. Error: Enviar Cantidad Inválida (Nulo, Cero o Negativo)
* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "idUsuario": 1,
    "detalles": [
      {
        "idProducto": 101,
        "cantidad": -5,
        "precioUnitario": 15000
      }
    ]
  }
  ```
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "status": 400,
    "timestamp": "2026-05-17T03:24:00.123456",
    "errors": {
      "detalles[0].cantidad": "La cantidad debe ser mayor a 0"
    }
  }
  ```

### 9. Error: Enviar Precio Unitario Inválido (Nulo, Cero o Negativo)
Valida la restricción crítica que impide que un artículo cueste 0 pesos o tenga precios negativos.

* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "idUsuario": 1,
    "detalles": [
      {
        "idProducto": 101,
        "cantidad": 2,
        "precioUnitario": -500
      }
    ]
  }
  ```
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "status": 400,
    "timestamp": "2026-05-17T03:25:00.123456",
    "errors": {
      "detalles[0].precioUnitario": "El precio unitario debe ser mayor a 0"
    }
  }
  ```

### 10. Error de Validación Múltiple Combinado (Varios Errores a la vez)
Prueba la capacidad del `GlobalExceptionHandler` para acumular todos los fallos en una sola respuesta detallada en lugar de detenerse en el primero.

* **Método:** `POST`
* **URL:** `http://localhost:8086/api/pedidos`
* **Body JSON:**
  ```json
  {
    "detalles": [
      {
        "idProducto": 101,
        "cantidad": 0,
        "precioUnitario": -100
      }
    ]
  }
  ```
* **Respuesta de Error Esperada (HTTP 400 Bad Request):**
  ```json
  {
    "status": 400,
    "timestamp": "2026-05-17T03:26:00.123456",
    "errors": {
      "idUsuario": "El idUsuario es obligatorio",
      "detalles[0].cantidad": "La cantidad debe ser mayor a 0",
      "detalles[0].precioUnitario": "El precio unitario debe ser mayor a 0"
    }
  }
  ```

---

# 🔴 Escenarios de Error: Recursos Inexistentes (HTTP 404 Not Found)
Casos diseñados para validar que cuando se busca o se intenta modificar un recurso que no existe, la API responda de forma semántica y segura.

### 11. Error: Buscar un Pedido Inexistente
* **Método:** `GET`
* **URL:** `http://localhost:8086/api/pedidos/9999`
* **Respuesta de Error Esperada (HTTP 404 Not Found):**
  *(Cuerpo de respuesta vacío)*

### 12. Error: Actualizar Estado de un Pedido Inexistente
* **Método:** `PUT`
* **URL:** `http://localhost:8086/api/pedidos/9999/estado?estado=PAGADO`
* **Respuesta de Error Esperada (HTTP 404 Not Found):**
  *(Cuerpo de respuesta vacío)*

---

## 💡 Tips de Pruebas Automatizadas en Postman
Para cada una de estas peticiones en Postman, puedes añadir los siguientes scripts en la pestaña **Tests** para automatizar la verificación del correcto control de errores:

### Validar que la respuesta sea un error 400 Bad Request y contenga los mensajes correctos:
```javascript
pm.test("Status code is 400", function () {
    pm.response.to.have.status(400);
});

pm.test("Tiene estructura de errores de validación", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property("status", 400);
    pm.expect(jsonData).to.have.property("errors");
    pm.expect(jsonData).to.have.property("timestamp");
});
```

### Validar que un pedido inválido no se cree por tener precio unitario negativo:
```javascript
pm.test("Precio unitario negativo capturado", function () {
    var jsonData = pm.response.json();
    pm.expect(jsonData.errors["detalles[0].precioUnitario"]).to.eql("El precio unitario debe ser mayor a 0");
});
```

### Validar que la respuesta para un pedido inexistente sea un 404 Not Found:
```javascript
pm.test("Status code is 404", function () {
    pm.response.to.have.status(404);
});
```
