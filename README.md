# Proyecto Fullstack 3: Arquitectura de Microservicios y Oracle Cloud

Repositorio oficial correspondiente a la entrega de la asignatura **Desarrollo Fullstack 3**. Este proyecto implementa una arquitectura basada en microservicios independientes con Spring Boot, persistencia segura en la nube mediante Oracle Cloud Autonomous Database y aseguramiento de calidad con pruebas unitarias.

---

## Arquitectura del Sistema

El sistema se divide en dos microservicios principales ejecutados de forma desacoplada:

1. **`usuarios` (Puerto `8081`)**:
   - Gestión de perfiles, roles y control de acceso por roles (`ADMIN`, `USUARIO`).
   - Manejo de autenticación y protección de credenciales sensibles (`password` con acceso de solo escritura `@JsonProperty`).
   - Validaciones estrictas de payloads y manejo centralizado de excepciones con `@ControllerAdvice`.

2. **`foro` (Puerto `8082`)**:
   - Administración de publicaciones, categorías y comentarios.
   - Seguridad y moderación basada en roles (`ADMIN`, `MODERADOR`, `USUARIO`) utilizando restricciones con `@PreAuthorize`.

---

## Tecnologías y Stack Tecnológico

- **Backend:** Java 21, Spring Boot 3.3.5
- **Seguridad:** Spring Security (Control de acceso basado en roles y `@PreAuthorize`)
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** Oracle Cloud Autonomous Database (Conectada mediante Wallet de seguridad y controlador `ojdbc11`)
- **Calidad y Pruebas:** JUnit 5, Mockito, MockMvc, Spring Security Test (`@WithMockUser`)
- **Control de Versiones y Documentación:** Git, Markdown, Changelogs independientes por microservicio

---

## Estructura del Repositorio

```text
FullStack3-Sumativa/
│
├── usuarios/                # Microservicio de gestión de usuarios (Puerto 8081)
│   ├── src/
│   ├── CHANGELOG.md         # Historial de cambios del microservicio usuarios
│   └── pom.xml
│
├── foro/                    # Microservicio de gestión del foro (Puerto 8082)
│   ├── src/
│   ├── CHANGELOG.md         # Historial de cambios del microservicio foro
│   └── pom.xml
│
└── README.md                # Documentación principal del proyecto
```

---

### Configuración de la Base de Datos (Oracle Cloud)

Ambos microservicios utilizan una conexión segura a Oracle Cloud Autonomous Database mediante un Wallet interno.

Se debe colocar la carpeta del Wallet (ej. Wallet_DBSumativa) en la ruta correspondiente de recursos de cada microservicio.

Los parámetros de conexión en los archivos de configuración (application.yml o application.properties) utilizan el servicio dbsumativa_high sobre el puerto 1522.

---

### Ejecución y Pruebas

1. **Compilar y ejecutar pruebas unitarias**
Cada microservicio cuenta con pruebas automatizadas que validan el comportamiento de los controladores y servicios. Para ejecutar las pruebas en cualquiera de los microservicios, navega a su carpeta y utiliza Maven:

```Bash
cd usuarios
./mvnw test

# O para el microservicio de foro:
cd ../foro
./mvnw test
```

2. **Ejecutar los microservicios en desarrollo**
Para levantar los servicios localmente, utiliza el plugin de Spring Boot en dos terminales independientes:

**Terminal 1 (Usuarios):**

```Bash
cd usuarios
./mvnw spring-boot:run
```

**Terminal 2 (Foro):**

```Bash
cd foro
./mvnw spring-boot:run
```

---

### Autores y Versión

**Asignatura:** Desarrollo Fullstack 3
**Versión del Proyecto:** 1.0.0