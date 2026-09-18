# CLAUDE.md — Contexto Vivo del Proyecto NexusFlow

> Este archivo es la "memoria" del proyecto. Se actualiza en cada paso importante
> para poder retomar el contexto sin perder decisiones ni estado técnico.
> Última actualización: Fase 0 — Setup inicial.

---

## 📌 1. Descripción del Proyecto

**NexusFlow** es un sistema de gestión de pedidos y logística B2B (Order
Management System). Simula el backend/frontend que una consultora
desarrollaría para un cliente real que vende a otras empresas: gestión de
pedidos, inventario, clientes, pagos y notificaciones, con evolución
planificada desde monolito hasta microservicios orquestados con eventos.

**Objetivo del proyecto:** portfolio profesional que demuestre progresión
arquitectónica real (no un CRUD de juguete) para procesos de selección de
consultoras/ingenierías de software.

---

## 🗺️ 2. Roadmap por Fases

### Fase 1 — Monolito bien estructurado (EN CURSO)
- [x] Elección de proyecto y definición de alcance
- [x] Setup de repositorio GitHub y estructura de documentación
- [x] Scaffold backend (Spring Boot 4.1.1, Java 21, PostgreSQL, Flyway)
- [ ] Scaffold frontend (Angular)
- [x] Modelo de dominio: Pedidos, Inventario, Clientes
- [ ] Autenticación y autorización (Spring Security + JWT)
- [ ] CRUD de negocio con reglas (no trivial)
- [ ] Tests unitarios e integración
- [ ] CI básico (GitHub Actions)

### Fase 2 — Microservicios + Apache Kafka (PENDIENTE)
- [ ] Descomposición: `order-service`, `inventory-service`,
      `payment-service`, `notification-service`
- [ ] Patrón Saga (coreografiada) para transacciones distribuidas
- [ ] Patrón Outbox (consistencia BD-eventos)
- [ ] Database-per-service

### Fase 3 — Dockerización y despliegue local (PENDIENTE)
- [ ] Docker Compose (servicios + Kafka + BDs)
- [ ] API Gateway
- [ ] Documentación de despliegue

---

## 🏗️ 3. Arquitectura Actual

**Estado:** Fase 0 (pre-código). Aún no se ha definido estructura de
carpetas ni se ha inicializado ningún proyecto.

**Decisión de repositorio:** Monorepo durante la Fase 1
(`backend/`, `frontend/`, `docs/`). Se reevaluará al iniciar la Fase 2 si
conviene separar en polyrepo por microservicio (se documentará como ADR
cuando llegue el momento).

---

## 📐 4. Decisiones Tomadas (resumen — detalle completo en `docs/adr/`)

| ID | Decisión | Estado |
|---|---|---|
| ADR-0001 | Proyecto elegido: NexusFlow (OMS B2B) | ✅ Aceptado |
| ADR-0002 | Monorepo en Fase 1, reevaluar en Fase 2 | ✅ Aceptado |
| ADR-0003 | PostgreSQL como motor de base de datos | ✅ Aceptado |
| ADR-0004 | Flyway para gestión de esquema (Hibernate en modo `validate`) | ✅ Aceptado |
| ADR-0005 | Uso de `spring-boot-starter-flyway` (no `flyway-core` suelto) por la modularización de Spring Boot 4 | ✅ Aceptado |

---

## 🗃️ 5. Modelos de Datos

**Customer**: id, name, email (unique), taxId (unique), createdAt

**Product**: id, sku (unique), name, price (BigDecimal), stock

**Order**: id, customer (→Customer), status (enum: PENDING, CONFIRMED,
PAID, SHIPPED, DELIVERED, CANCELLED), totalAmount, createdAt, items (→OrderItem, cascade ALL + orphanRemoval)

**OrderItem**: id, order (→Order), product (→Product), quantity, unitPrice
(precio congelado en el momento de la compra, no se recalcula si cambia
`Product.price`)

Migración: `V1__init_schema.sql` (tablas `customers`, `products`, `orders`,
`order_items`, con FKs, CHECK constraints e índices sobre claves foráneas).

---

## 🔌 6. Endpoints Creados

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/health` | Health check de infraestructura |
| POST | `/api/orders` | Crea un pedido (valida cliente, valida y descuenta stock, calcula total) |
| GET | `/api/orders/{id}` | Consulta un pedido por id |

Errores manejados centralmente por `GlobalExceptionHandler`:
- `ResourceNotFoundException` → 404
- `InsufficientStockException` → 409
- Errores de validación (`@Valid`) → 400 con detalle de campos

---

## 📂 7. Estrategia de Documentación en GitHub

Para que el repositorio en sí mismo sea parte del portfolio:

- **README.md**: cara pública del proyecto (qué es, stack, cómo levantarlo,
  capturas/diagramas). Se escribe y actualiza al final de cada fase.
- **CLAUDE.md** (este archivo): contexto técnico vivo, uso interno de trabajo.
- **docs/adr/**: Architecture Decision Records, uno por decisión relevante,
  formato `ADR-000X-titulo-corto.md` (contexto → decisión → consecuencias).
- **CHANGELOG.md**: formato Keep a Changelog, actualizado por versión/hito.
- **Conventional Commits**: todos los commits siguen
  `tipo(scope): descripción` (`feat`, `fix`, `refactor`, `docs`, `test`,
  `chore`, etc.).
- **GitHub Projects (Kanban)**: tablero con columnas To Do / In Progress /
  Done, vinculado a Issues. Los Milestones representan las Fases 1/2/3.
- **Branching:** `main` protegida, ramas `feature/xxx`, merge vía Pull
  Request (aunque se trabaje en solitario, simula flujo de equipo real).

---

## ✅ 8. Estado Actual / Próximo Paso

**Estamos en:** Primer flujo de negocio completo: `POST /api/orders` crea
un pedido validando cliente y stock, congelando precios en `OrderItem`,
descontando stock (vía dirty checking dentro de una transacción) y
calculando el total. `GET /api/orders/{id}` permite consultarlo.
Manejo centralizado de errores con `GlobalExceptionHandler` (404/409/400).
3 tests de integración cubren el camino feliz y los dos casos de error
principales (stock insuficiente, cliente inexistente).

**Stack backend confirmado:** Spring Boot 4.1.1 · Java 21 · PostgreSQL 16 ·
Flyway (vía `spring-boot-starter-flyway`) · Spring Data JPA (modo
`validate`) · springdoc-openapi (Swagger) · Lombok.

**Siguiente paso técnico:** Verificar manualmente en Swagger/Postman,
ejecutar los tests, y decidir entre seguir con más endpoints CRUD
(clientes, productos) o introducir Spring Security (JWT) antes de seguir
ampliando el dominio.
