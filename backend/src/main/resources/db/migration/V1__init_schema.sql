-- V1: Esquema inicial del dominio de Pedidos.
-- Convencion Flyway: V<version>__<descripcion_en_snake_case>.sql
-- Este archivo, una vez aplicado en un entorno, NUNCA se edita: cualquier
-- cambio futuro va en un V2__..., V3__..., etc.

CREATE TABLE customers (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    tax_id      VARCHAR(50)  NOT NULL UNIQUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE TABLE products (
    id          BIGSERIAL PRIMARY KEY,
    sku         VARCHAR(100)   NOT NULL UNIQUE,
    name        VARCHAR(255)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    stock       INTEGER        NOT NULL CHECK (stock >= 0)
);

CREATE TABLE orders (
    id            BIGSERIAL PRIMARY KEY,
    customer_id   BIGINT         NOT NULL REFERENCES customers (id),
    status        VARCHAR(20)    NOT NULL,
    total_amount  NUMERIC(10, 2) NOT NULL DEFAULT 0,
    created_at    TIMESTAMP      NOT NULL DEFAULT now()
);

CREATE TABLE order_items (
    id          BIGSERIAL PRIMARY KEY,
    order_id    BIGINT         NOT NULL REFERENCES orders (id),
    product_id  BIGINT         NOT NULL REFERENCES products (id),
    quantity    INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price  NUMERIC(10, 2) NOT NULL CHECK (unit_price >= 0)
);

-- Indices sobre las claves foraneas: sin ellos, cada busqueda de
-- "pedidos de un cliente" o "lineas de un pedido" haria un escaneo
-- completo de la tabla a medida que crezca.
CREATE INDEX idx_orders_customer_id ON orders (customer_id);
CREATE INDEX idx_order_items_order_id ON order_items (order_id);
CREATE INDEX idx_order_items_product_id ON order_items (product_id);
