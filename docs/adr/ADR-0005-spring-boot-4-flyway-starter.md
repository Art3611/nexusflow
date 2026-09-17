# ADR-0005: Uso de `spring-boot-starter-flyway` (Spring Boot 4 modulariza la autoconfiguración)

**Estado:** Aceptado
**Fecha:** Fase 1 — Setup del backend

## Contexto
Al arrancar la aplicación con `flyway-core` y `flyway-database-postgresql`
como dependencias directas (patrón válido en Spring Boot 3.x), la
aplicación arrancaba sin errores pero **Flyway nunca se ejecutaba**: no
aparecía el banner de Flyway en los logs, no se aplicaba ninguna
migración, y Hibernate fallaba después con
`SchemaManagementException: Schema validation: missing table [customers]`
al no encontrar las tablas.

Diagnóstico: en Spring Boot 4, el antiguo jar monolítico
`spring-boot-autoconfigure` se dividió en módulos independientes por
tecnología. La autoconfiguración que detecta Flyway en el classpath y
ejecuta las migraciones al arrancar (`FlywayAutoConfiguration`) se movió a
un módulo propio, `spring-boot-flyway`, que **no se incluye
automáticamente** solo por depender de `flyway-core`. El resultado es un
fallo silencioso: la librería está presente y es funcional, pero Spring
Boot nunca la invoca.

## Decisión
Se declara la dependencia `spring-boot-starter-flyway` (en vez de
`flyway-core` suelto) en el `pom.xml`, manteniendo además
`flyway-database-postgresql` como dependencia explícita para el soporte
específico de PostgreSQL.

## Cómo se diagnosticó
No se asumió la causa a la primera: se comparó el log de arranque
completo (incluyendo el classpath real usado por la JVM) contra la
documentación oficial de modularización de Spring Boot 4, confirmando que
`spring-boot-flyway-4.1.1.jar` estaba ausente del classpath aunque
`flyway-core` sí estaba presente.

## Consecuencias
- Cualquier otra librería que antes se autoconfigurase "mágicamente" solo
  por estar en el classpath (patrón de Spring Boot 3.x) debe revisarse:
  en Spring Boot 4 puede requerir su starter específico.
- Este mismo patrón de diagnóstico (comparar el classpath real del log de
  arranque contra lo esperado) se reutilizará ante fallos similares en el
  resto del proyecto.
