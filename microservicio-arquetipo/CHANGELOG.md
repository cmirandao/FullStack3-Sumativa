# Changelog

Todas las modificaciones de `microservicio-arquetipo` serán documentadas en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [1.0.0] - 2026-11-22

### Agregado
- Arquetipo Maven reutilizable configurado con Java 21, Spring Boot 3.3.5, Spring Web, Spring Data JPA, Validation y Lombok.
- Dependencias de conectividad y seguridad para Oracle Cloud Autonomous Database (`ojdbc11`, `oraclepki`, `osdt_cert`, `osdt_core`).
- Instalación local en repositorio Maven (`~/.m2`) mediante `mvn clean install` para estandarizar nuevos microservicios.