# Microservicio: Pedidos

## 1. Descripción del Microservicio
Este microservicio es responsable de la consolidación de la orden de compra una vez que el usuario confirma el carrito. Su objetivo principal es generar un registro inmutable de la transacción, incluyendo todos sus detalles (productos, cantidades, precios y estado).

**Rol dentro del Sistema:**
Recibe la información validada y confirmada, y crea el registro de la compra (Pedido). Este servicio interactúa de forma lógica (sin claves foráneas duras) con el microservicio de **Usuarios** (para saber quién compra) y el microservicio de **Catálogo** (para saber qué compra).

---

## 2. Base de Datos: `db_pedidos`

De acuerdo con la rúbrica del proyecto, este microservicio posee su propia base de datos independiente, la cual no comparte tablas con otros microservicios.

### 2.1 Tablas y Estructura

#### Tabla: `pedidos`
Almacena la cabecera principal de la transacción.

| Atributo | Tipo de Dato | Descripción |
| :--- | :--- | :--- |
| `id_pedido` | Primary Key | Identificador único y autoincremental del pedido. |
| `id_usuario` | Integer | Referencia lógica al MS de Usuarios (NO es clave foránea). |
| `fecha_creacion`| Datetime | Fecha y hora en la que se realizó la orden de compra. |
| `estado` | Varchar | Estado actual del pedido (Ej: PENDIENTE, PAGADO, CANCELADO). |
| `total` | Decimal/Integer | Monto total a pagar por el pedido. |

#### Tabla: `detalles_pedido`
Almacena el detalle individual de cada producto incluido en el pedido.

| Atributo | Tipo de Dato | Descripción |
| :--- | :--- | :--- |
| `id_detalle` | Primary Key | Identificador único y autoincremental del detalle. |
| `pedido_id` | Integer | Foreign Key que referencia a `pedidos(id_pedido)`. |
| `id_producto` | Integer | Referencia lógica al MS de Catálogo (NO es clave foránea). |
| `cantidad` | Integer | Cantidad comprada del producto. |
| `precio_unitario`| Integer | Precio del producto en el momento de la compra. |

### 2.2 Relaciones Internas
* **1 a N:** Un registro en la tabla `pedidos` puede tener muchos registros en la tabla `detalles_pedido`.

---

## 3. Tecnologías
* **Lenguaje:** Java 21
* **Framework:** Spring Boot (Spring Web, Spring Data JPA, Validation)
* **Base de Datos:** MySQL
* **Otros:** Lombok
