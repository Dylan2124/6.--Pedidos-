# Pruebas de Conexión (OpenFeign) - MS Pedido

Este documento detalla cómo probar la integración de `ms-pedido` con otros microservicios mediante Postman. 
La comunicación está protegida por un patrón de Tolerancia a Fallos.

---

## 1. Descontar Stock en Inventario (Al crear pedido)

**Objetivo:** Validar que al crear un nuevo pedido, `ms-pedido` se comunique remotamente con el microservicio de Inventario para restar las unidades compradas.

*   **Método:** `POST`
*   **URL:** `http://localhost:8086/api/pedidos`
*   **Body (JSON):**
    ```json
    {
      "idUsuario": 2,
      "detalles": [
        {
          "idProducto": 101,
          "cantidad": 1,
          "precioUnitario": 250000
        },
        {
          "idProducto": 102,
          "cantidad": 2,
          "precioUnitario": 50000
        }
      ]
    }
    ```

### Resultados Esperados en Consola de MS-PEDIDO:

Si el microservicio de Inventario (puerto 8083) está **ENCENDIDO**:
> *"Comunicando con ms-inventario para descontar stock..."*
> *"Stock descontado exitosamente en ms-inventario."*

Si el microservicio de Inventario está **APAGADO** (Demostración de Tolerancia a Fallos):
> *"Comunicando con ms-inventario para descontar stock..."*
> *"ATENCION: No se pudo comunicar con ms-inventario para descontar stock. El pedido continuará su curso. Error: Connection refused..."*

En ambos casos, Postman debe devolver `201 Created` porque el pedido se guardó correctamente en la base de datos local.
