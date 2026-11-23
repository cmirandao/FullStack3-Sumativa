# Foro Vanguard (`foro-frontend`)

Aplicación frontend desarrollada en **Angular 22** con **Bootstrap 5** y **Bootstrap Icons** para el sistema de foros en línea (**Desarrollo Full Stack III**).

## Características implementadas
- **Modo oscuro sobrio:** Superficies en tonos oscuros de baja fatiga visual con acentos azul y morado mate, sin fondos blancos.
- **Foro y 5 categorías:** Filtrado por categoría, búsqueda por texto, creación de publicaciones y comentarios.
- **Control de sesión y roles:** Ocultamiento de opciones restringidas cuando no hay sesión iniciada y protección de rutas mediante `authGuard` y `roleGuard` (`ADMIN`, `MODERADOR`, `USUARIO`).
- **Validación de contraseñas:** Reglas aplicadas en registro y modificación de perfil (8 a 24 caracteres, letras y números, y al menos un carácter especial).

## Desarrollo local

```bash
npm install
ng serve --open
```

## Compilación para producción

```bash
npm run build -- --configuration production
```

## Docker

```bash
docker build -t foro-frontend:1.0 .
docker run -d -p 4200:80 foro-frontend:1.0
```