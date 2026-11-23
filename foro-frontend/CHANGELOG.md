# Changelog

Todas las modificaciones de `foro-frontend` serán documentadas en este archivo.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/),
y este proyecto se adhiere a [Semantic Versioning](https://semver.org/lang/es/).

## [1.0.0] - 2026-11-22

### Agregado
- Aplicación web **Foro Vanguard** en Angular 22 con Bootstrap 5 y Bootstrap Icons.
- Paleta oscura sobria de baja fatiga visual (`#161922` y `#1e222f` con acentos azul/morado mate) y diseño adaptable a distintos tamaños de pantalla.
- Servicios Singleton con patrón Observer (`AuthService` y `ForoService`) gestionando 5 categorías, temas, comentarios y moderación/baneo.
- Vistas de autenticación y cuenta (`Login`, `Registro`, `Recuperar contraseña` y `Mi perfil`) con validación estricta de contraseña (8 a 24 caracteres, letras y números, y al menos un carácter especial).
- Protección de rutas con `authGuard` y `roleGuard` (`ADMIN`, `MODERADOR`, `USUARIO`) y ocultamiento en navegación de opciones que requieren sesión o rol.
- Configuración de `zone.js` en `main.ts` y `angular.json`, además de `Dockerfile` multi-etapa con Nginx (`nginx.conf`) para despliegue en contenedores.