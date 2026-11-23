# Proyecto Fullstack 3: Sistema de Foros en Línea

Este proyecto implementa una arquitectura basada en microservicios independientes con Spring Boot 3, un arquetipo Maven reutilizable, un frontend en Angular 22, patrones de diseño empresariales, persistencia en **Oracle Cloud Autonomous Database**, despliegue en contenedores con Docker y estrategia de control de versiones **Trunk Based Development (TBD)**.

---

## Arquitectura del Sistema

El sistema se compone de microservicios desacoplados, un arquetipo base, el frontend web y el script relacional de base de datos:

1. **`microservicio-arquetipo`**:
   - Plantilla Maven reutilizable (`maven-archetype`) preconfigurada con Java 21, Spring Boot 3.3.5, Spring Web, Data JPA, Validation, Lombok y dependencias de seguridad/conexión para Oracle Cloud (`ojdbc11`, `oraclepki`, `osdt_cert`, `osdt_core`).
2. **`usuarios` (Puerto `8081`)**:
   - Gestión de perfiles, roles y control de acceso (`ADMIN`, `MODERADOR`, `USUARIO`).
   - Autenticación (`POST /api/usuarios/login`), soporte de Basic Auth en `SecurityConfig`, protección de credenciales (`password` solo escritura) y respuestas estandarizadas con el **Patrón Builder** (`ApiResponse.Builder`).
3. **`foro` (Puerto `8082`)**:
   - Administración de categorías, publicaciones, búsqueda y comentarios.
   - Moderación y baneo de publicaciones/comentarios basada en roles (`ADMIN`, `MODERADOR`, `USUARIO`) mediante `@PreAuthorize`, soporte de Basic Auth y respuestas construidas con `ApiResponse.Builder`.
4. **`foro-frontend` (Puerto `4200` en dev / `80` en Nginx)**:
   - Interfaz web **Foro Vanguard** en Angular 22 con modo oscuro sobrio, 5 categorías, validación de contraseñas (8–24 caracteres, letras/números y carácter especial), `authGuard`, `roleGuard` y ocultamiento de acciones privadas sin sesión.

---

## Patrones de Diseño y Lineamientos

- **Patrón Builder:** Implementado mediante la clase genérica `ApiResponse<T>` (`ApiResponse.Builder`) en los controladores de `usuarios` y `foro` para construir respuestas JSON consistentes (`exito`, `mensaje`, `datos`, `timestamp`).
- **Patrón Singleton y Arquitectura en Capas:** Gestión de beans únicos en Spring (`@Service`, `@Repository`) bajo el modelo `Controller -> Service -> Repository -> Model/DTO`, y servicios `providedIn: 'root'` en Angular.
- **Patrón Observer:** Implementado con `BehaviorSubject` en los servicios del frontend para reflejar en tiempo real la sesión activa, publicaciones y moderación.
- **Estrategia de Branching:** **Trunk Based Development (TBD)** con ramas de corta duración (`feature/*`) integradas hacia `main`.

---

## Tecnologías y Stack Tecnológico

- **Backend:** Java 21, Spring Boot 3.3.5, Apache Maven 3
- **Frontend:** Angular 22, TypeScript, Bootstrap 5, Bootstrap Icons, Zone.js
- **Seguridad:** Spring Security (`@PreAuthorize`, `httpBasic`, control de acceso basado en roles) y guards en Angular
- **Persistencia:** Spring Data JPA / Hibernate
- **Base de Datos:** Oracle Cloud Autonomous Database (Wallet de seguridad y `ojdbc11`)
- **Calidad y Pruebas:** JUnit 5, Mockito, MockMvc, Spring Security Test (`@WithMockUser`) y Postman
- **Contenedores y Cloud:** Docker, Nginx y Docker Hub / Cloud (`camo86`)

---

## Estructura del Repositorio

```text
FullStack3-Sumativa/
│
├── microservicio-arquetipo/ # Arquetipo Maven base para microservicios Spring Boot 3
│   ├── src/
│   ├── CHANGELOG.md
│   └── pom.xml
│
├── usuarios/                # Microservicio de gestión de usuarios y login (Puerto 8081)
│   ├── src/
│   ├── Wallet_DBSumativa/   # Wallet de Oracle Cloud (local)
│   ├── Dockerfile
│   ├── CHANGELOG.md
│   └── pom.xml
│
├── foro/                    # Microservicio de categorías, temas, comentarios y moderación (Puerto 8082)
│   ├── src/
│   ├── Wallet_DBSumativa/   # Wallet de Oracle Cloud (local)
│   ├── Dockerfile
│   ├── CHANGELOG.md
│   └── pom.xml
│
├── foro-frontend/           # Aplicación web Angular 22 (Foro Vanguard)
│   ├── src/
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── CHANGELOG.md
│   ├── README.md
│   └── package.json
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

3. Importante (PATH en zsh): Si `ng` o `java` no son reconocidos en la terminal, agrega las rutas a `~/.zshrc` y recarga:
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
   ```powershell
   npm install -g @angular/cli
   ```

4. Verificar instalación:
   ```powershell
   java -version; mvn -v; node -v; ng version; docker --version
   ```

---

## Configuración de la Base de Datos (Oracle Cloud)

1. Ejecuta `script_foro_bd.sql` en tu instancia de **Oracle Cloud Autonomous Database**.
2. Coloca la carpeta de la Wallet (`Wallet_DBSumativa`) dentro de `usuarios/` y `foro/`.
3. Verifica en `application.properties` de ambos microservicios la conexión al servicio `dbsumativa_high` en el puerto `1522`.

---

## Instalación del Arquetipo Maven

Para registrar el arquetipo en tu repositorio local (`~/.m2`):

### macOS / Linux:
```bash
cd microservicio-arquetipo
mvn clean install
```

### Windows (PowerShell):
```powershell
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

### 2. Levantar los microservicios y frontend en desarrollo
Abre terminales independientes:

- **Terminal 1 (`usuarios` - Puerto `8081`):**
  - **macOS / Linux:** `cd usuarios && mvn spring-boot:run`
  - **Windows:** `cd usuarios; mvn spring-boot:run`
- **Terminal 2 (`foro` - Puerto `8082`):**
  - **macOS / Linux:** `cd foro && mvn spring-boot:run`
  - **Windows:** `cd foro; mvn spring-boot:run`
- **Terminal 3 (`foro-frontend` - Puerto `4200`):**
  - **macOS / Linux:** `cd foro-frontend && ng serve --open`
  - **Windows:** `cd foro-frontend; ng serve --open`

---

## Docker y Despliegue

### Construir imágenes localmente
```bash
# 1. Microservicio usuarios
cd usuarios && mvn clean package -DskipTests && docker build -t foro-usuarios:1.0 .

# 2. Microservicio foro
cd ../foro && mvn clean package -DskipTests && docker build -t foro-servicio:1.0 .

# 3. Frontend Angular con Nginx
cd ../foro-frontend && docker build -t foro-frontend:1.0 .
```

### Imágenes en Docker Hub / Cloud
Las imágenes publicadas bajo la cuenta **`camo86`** pueden consultarse en Docker Hub y descargarse/ejecutarse con `docker pull` / `docker run`:

- `camo86/foro-usuarios:1.0`
- `camo86/foro-servicio:1.0`
- `camo86/foro-frontend:1.0`

---

## Autores y Versión

- **Asignatura:** Desarrollo Fullstack 3
- **Versión del Proyecto:** 2.0.0