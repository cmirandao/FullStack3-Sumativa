# Changelog

Todas las modificaciones notables de este microservicio serán documentadas en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [1.1.0] - 2026-11-08

### Agregado
- Implementación de Spring Security y control de acceso basado en roles (`ADMIN`, `USUARIO`).
- Protección de credenciales sensibles ocultando el campo `password` en las respuestas GET mediante `@JsonProperty(Access.WRITE_ONLY)`.

### Cambios
- Actualización de la versión del SDK de compilación a Java 21 y ajuste del plugin de Maven para garantizar compatibilidad con Lombok.

## [1.0.0] - 2026-11-01

### Agregado
- Estructuración inicial del microservicio con Spring Boot y Java 21.
- Configuración de persistencia con Oracle Cloud Autonomous Database mediante Wallet de seguridad (`Wallet_DBSumativa`) y driver JDBC.
- Endpoints principales para la gestión del dominio.
- Manejo centralizado de excepciones con `@ControllerAdvice` y validaciones de payloads.
- Pruebas unitarias e de integración con JUnit 5 y MockMvc (`BUILD SUCCESS`).

### Cambios
- Optimización de inyección de dependencias por constructor (`private final`) para cumplir con los estándares de calidad de código.
- Eliminación de anotaciones redundantes en las interfaces de repositorio (`@Repository`).

### Correcciones
- Corrección de tipado en los retornos de los controladores (`ResponseEntity` parametrizado).
- Depuración de advertencias de compilación en entorno de desarrollo (VS Code).