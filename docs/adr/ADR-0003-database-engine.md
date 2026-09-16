# ADR-0003: Motor de base de datos — PostgreSQL

**Estado:** Aceptado
**Fecha:** Fase 1 — Setup inicial

## Contexto
El dominio de NexusFlow (pedidos, inventario, pagos) requiere integridad
transaccional fuerte: no puede confirmarse un pedido si el stock no se
reserva de forma consistente. Se evaluaron PostgreSQL y MySQL como
alternativas relacionales.

## Decisión
Se elige **PostgreSQL** como motor de base de datos para todos los
servicios del proyecto (monolito en Fase 1, y cada microservicio en
Fase 2 bajo el patrón database-per-service).

## Razones
- Soporte robusto y maduro de constraints, transacciones ACID e índices
  avanzados.
- Soporte nativo de tipos `JSONB`, útil para metadata flexible sin
  necesidad de un motor NoSQL adicional.
- Es el motor relacional más extendido en stacks Java/Spring de nivel
  enterprise, lo que lo hace más relevante para el portfolio.

## Consecuencias
- Se requiere Docker (o instalación local de Postgres) para desarrollo.
- El driver JDBC de PostgreSQL y el dialecto de Hibernate quedan fijados
  desde el inicio del proyecto.
