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

_Pendiente — se completará a medida que implementemos controllers._

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

**Estamos en:** Backend arrancando correctamente de extremo a extremo:
PostgreSQL vía Docker Compose, Flyway aplicando `V1__init_schema.sql`,
Hibernate validando el modelo de dominio (`Customer`, `Product`, `Order`,
`OrderItem`) sin errores, `GET /api/health` respondiendo y Swagger UI
disponible.

**Incidente resuelto (documentado en ADR-0005):** con Spring Boot 4, la
autoconfiguración de Flyway vive en el módulo `spring-boot-flyway`, no en
`flyway-core` directamente. Sin `spring-boot-starter-flyway`, Flyway queda
en el classpath como librería inerte, sin que Spring Boot la invoque, lo
que provocaba un fallo silencioso (`missing table [customers]`) sin
ninguna pista de Flyway en los logs.

**Stack backend confirmado:** Spring Boot 4.1.1 · Java 21 · PostgreSQL 16 ·
Flyway (vía `spring-boot-starter-flyway`) · Spring Data JPA (modo
`validate`) · springdoc-openapi (Swagger) · Lombok.

**Siguiente paso técnico:** Crear la capa de servicio (`OrderService`) con
la lógica de creación de un pedido (validar stock, calcular total,
descontar stock), los DTOs de entrada/salida, el controlador REST
`POST /api/orders`, y su primer test de integración.
