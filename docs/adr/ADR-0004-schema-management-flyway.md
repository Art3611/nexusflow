# ADR-0004: Gestión del esquema de BD — Flyway (en vez de Hibernate ddl-auto)

**Estado:** Aceptado
**Fecha:** Fase 1 — Setup inicial

## Contexto
Hibernate puede generar y modificar el esquema automáticamente
(`ddl-auto: update`), lo cual es rápido para prototipar pero no deja
ningún historial versionado de cambios, y no es una práctica aceptable en
un entorno de producción real.

## Decisión
Se usa **Flyway** como única fuente de verdad del esquema de base de
datos, mediante migraciones SQL versionadas (`V1__descripcion.sql`,
`V2__descripcion.sql`, ...). Hibernate se configura en modo
`ddl-auto: validate`, es decir, solo verifica que las entidades `@Entity`
coincidan con las tablas creadas por Flyway, sin modificarlas nunca.

## Razones
- Historial de cambios de esquema reproducible y auditable, igual que el
  historial de commits de código.
- Es el estándar de facto en proyectos Spring Boot de nivel profesional.
- La curva de aprendizaje adicional es baja: son archivos `.sql` con una
  convención de nombres.

## Consecuencias
- Cada cambio en el modelo de datos requiere escribir manualmente el
  archivo de migración correspondiente (no se genera solo).
- Si las entidades Java y las migraciones SQL divergen, la aplicación no
  arranca (fallo explícito en vez de comportamiento silencioso incorrecto).
