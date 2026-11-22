# Proyecto Fullstack 3: Sistema de Foros en Línea

Este proyecto implementa una arquitectura basada en microservicios independientes con Spring Boot 3, un arquetipo Maven reutilizable, patrones de diseño empresariales, persistencia en **Oracle Cloud Autonomous Database** y estrategia de control de versiones **Trunk Based Development (TBD)**

---

## Arquitectura del Sistema

El backend se divide en microservicios desacoplados, acompañados de un arquetipo base y el script relacional de base de datos:

1. **`microservicio-arquetipo`**:
   - Plantilla Maven reutilizable (`maven-archetype`) preconfigurada con Java 21, Spring Boot 3.3.5, Spring Web, Data JPA, Validation, Lombok y dependencias de seguridad/conexión para Oracle Cloud (`ojdbc11`, `oraclepki`, `osdt_cert`, `osdt_core`).
2. **`usuarios` (Puerto `8081`)**:
   - Gestión de perfiles, roles y control de acceso (`ADMIN`, `MODERADOR`, `USUARIO`).
   - Autenticación (`POST /api/usuarios/login`), protección de credenciales (`password` solo escritura) y respuestas estandarizadas con el **Patrón Builder** (`ApiResponse.Builder`).
3. **`foro` (Puerto `8082`)**:
   - Administración de categorías, publicaciones, búsqueda y comentarios.
   - Moderación y baneo de publicaciones/comentarios basada en roles (`ADMIN`, `MODERADOR`, `USUARIO`) mediante `@PreAuthorize` y respuestas construidas con `ApiResponse.Builder`.

---

## Patrones de Diseño y Lineamientos

- **Patrón Builder:** Implementado mediante la clase genérica `ApiResponse<T>` en los controladores de `usuarios` y `foro` para construir respuestas JSON consistentes (`exito`, `mensaje`, `datos`, `timestamp`).
- **Patrón Singleton y Arquitectura en Capas:** Gestión de beans únicos en Spring (`@Service`, `@Repository`) bajo el modelo `Controller -> Service -> Repository -> Model/DTO`.
- **Estrategia de Branching:** **Trunk Based Development (TBD)** con ramas de corta duración (`feature/*`) integradas frecuentemente hacia `main`.

---

## Tecnologías y Stack Tecnológico

- **Backend:** Java 21, Spring Boot 3.3.5, Apache Maven 3
- **Seguridad:** Spring Security (`@PreAuthorize`, control de acceso basado en roles)
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** Oracle Cloud Autonomous Database (Wallet de seguridad y `ojdbc11`)
- **Calidad y Pruebas:** JUnit 5, Mockito, MockMvc, Spring Security Test (`@WithMockUser`) y Postman
- **Contenedores y Cloud:** Docker (preparado para despliegue en Docker Cloud)

---

## Estructura del Repositorio

```text
FullStack3-Sumativa/
│
├── microservicio-arquetipo/ # Arquetipo Maven base para microservicios Spring Boot 3
│   ├── src/
│   └── pom.xml
│
├── usuarios/                # Microservicio de gestión de usuarios y login (Puerto 8081)
│   ├── src/
│   ├── Wallet_DBSumativa/   # Wallet de Oracle Cloud (local)
│   ├── CHANGELOG.md
│   └── pom.xml
│
├── foro/                    # Microservicio de categorías, temas, comentarios y moderación (Puerto 8082)
│   ├── src/
│   ├── Wallet_DBSumativa/   # Wallet de Oracle Cloud (local)
│   ├── CHANGELOG.md
│   └── pom.xml
│
├── script_foro_bd.sql       # Script SQL de creación de tablas y datos base en Oracle Cloud
└── README.md                # Documentación principal del proyecto
```

---

## Requisitos Previos e Instalación por Sistema Operativo

### macOS (Apple Silicon / Intel con Homebrew)

1. Instalar herramientas base:
```bash
   brew install openjdk@21 maven node git
   brew install --cask docker visual-studio-code postman
```

2. Instalar Angular CLI globalmente:
```bash
   npm install -g @angular/cli
```

3. Importante (PATH en zsh): Si ng o java no son reconocidos en la terminal, agrega las rutas a ~/.zshrc y recarga:
```bash
   echo 'export PATH="/opt/homebrew/bin:$(npm config get prefix)/bin:$PATH"' >> ~/.zshrc
   source ~/.zshrc
```

4. Verificar instalación:
```bash
   java -version && mvn -v && node -v && ng version && docker --version
```

### Windows (PowerShell / Git Bash)

1. Instalar JDK 21 (OpenJDK / Temurin), Apache Maven, Node.js (LTS), Git, Docker Desktop y Postman.

2. Configurar las variables de entorno del sistema (`JAVA_HOME`, `MAVEN_HOME` y agregar sus carpetas `\bin` al `Path`).

3. Instalar Angular CLI en PowerShell:
```bash
   npm install -g @angular/cli
```

4. Verificar instalación:
```bash
   java -version; mvn -v; node -v; ng version; docker --version
```

---

## Configuración de la Base de Datos (Oracle Cloud)

Ambos microservicios utilizan una conexión segura a Oracle Cloud Autonomous Database mediante un Wallet interno.

Se debe colocar la carpeta del Wallet (ej. Wallet_DBSumativa) en la ruta correspondiente de recursos de cada microservicio.

Los parámetros de conexión en los archivos de configuración (application.yml o application.properties) utilizan el servicio dbsumativa_high sobre el puerto 1522.

---

## Instalación del Arquetipo Maven

Para registrar el arquetipo en tu repositorio local (~/.m2) y poder generar nuevos microservicios:

### macOS / Linux:
```bash
   cd microservicio-arquetipo
   mvn clean install
```

### Windows (PowerShell):
```bash
   cd microservicio-arquetipo
   mvn clean install
```

---

## Ejecución, Compilación y Pruebas

### 1. Compilar (solo Build `.jar`)
Ejecuta dentro de `usuarios` o `foro`:

| Acción | macOS / Linux | Windows (PowerShell) |
| :--- | :--- | :--- |
| Build con tests | `mvn clean package` | `mvn clean package` |
| Build sin tests | `mvn clean package -DskipTests` | `mvn clean package -DskipTests` |
| Solo pruebas unitarias | `mvn test` (o `./mvnw test`) | `mvn test` (o `.\mvnw.cmd test`) |

### 2. Levantar los microservicios en desarrollo
Abre dos terminales independientes:

**Terminal 1 (`usuarios` - Puerto `8081`):**
- **macOS / Linux:** `cd usuarios && mvn spring-boot:run`
- **Windows:** `cd usuarios; mvn spring-boot:run`

**Terminal 2 (`foro` - Puerto `8082`):**
- **macOS / Linux:** `cd foro && mvn spring-boot:run`
- **Windows:** `cd foro; mvn spring-boot:run`

---

## Autores y Versión

**Asignatura:** Desarrollo Fullstack 3
**Versión del Proyecto:** 2.0.0