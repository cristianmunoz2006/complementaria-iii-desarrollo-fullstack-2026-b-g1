# Semana 14 — App fullstack segura y probada (Corte 3)

> **CONFIG:** FULL_NAME: `Cristian Andres Muñoz Montenegro` · GITHUB_USER: `cristianmunoz2006`

## Caso: Recetario seguro

El Recetario (Semanas 3, 6–9) ahora con **login JWT**: la API protege sus
endpoints, y el frontend tiene una pantalla de login y una vista con las
recetas que solo se muestra si el login fue correcto.

## Estructura

```
C3-activity/
├── docker-compose.yml          # levanta backend + frontend con un comando
├── backend/                    # API Spring Boot + JWT
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/recetario/
│       │   ├── RecetarioSecureApplication.java
│       │   ├── config/         # SecurityConfig (JWT + CORS), UserConfig
│       │   ├── security/       # JwtService, JwtAuthFilter
│       │   ├── controller/     # AuthController (login), RecipeController
│       │   ├── service/ · repository/ · model/
│       └── test/java/com/recetario/AuthSecurityTest.java
└── frontend/                   # HTML + CSS + JS (Bootstrap 4.6.2 local)
    ├── Dockerfile              # nginx
    ├── index.html
    ├── assets/css/style.css
    ├── logic/api.js            # login() y getRecipes() (fáciles de probar)
    ├── logic/app.js            # interfaz: login, lista, estados
    ├── library/bootstrap-4.6.2/  # copia aquí tu carpeta de Bootstrap (ver abajo)
    ├── test/api.test.js        # pruebas del frontend
    └── package.json
```

> **Bootstrap:** copia tu carpeta `bootstrap-4.6.2` (la misma de las
> actividades anteriores) dentro de `C3-activity/frontend/library/`. Si falta,
> la app funciona igual pero sin estilos.

## Flujo de autenticación (JWT)

1. El frontend envía `POST /api/auth/login` con `{ "username", "password" }`.
2. Si son correctos, la API responde `200` con `{ "token": "<JWT>" }`
   (firmado con HMAC-SHA256, vence en 1 hora). Si no, responde `401`.
3. El frontend guarda el token en `sessionStorage` y lo envía en cada
   petición: `Authorization: Bearer <token>`.
4. `JwtAuthFilter` valida el token en cada petición. Si falta o es
   inválido, los endpoints protegidos responden `401`.

Usuario de demostración: `admin` / `admin123` (definido en
`backend/src/main/resources/application.properties`).

### Endpoints

| Método | Endpoint | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/login` | Público | Devuelve el token JWT |
| GET | `/api/recipes` | Con token | Lista las recetas |
| GET | `/api/recipes/{id}` | Con token | Obtiene una receta |
| POST | `/api/recipes` | Con token | Crea una receta |
| PUT | `/api/recipes/{id}` | Con token | Actualiza una receta |
| DELETE | `/api/recipes/{id}` | Con token | Borra una receta |

### CORS

El frontend (`http://localhost:8081`) y la API (`http://localhost:8080`)
tienen **orígenes distintos**, así que el navegador bloquea las llamadas
a menos que la API lo permita. `SecurityConfig` define un
`CorsConfigurationSource` que acepta solo los orígenes de la propiedad
`app.cors.allowed-origins` (por defecto `8081`, y `5500` para Live
Server), con los métodos `GET/POST/PUT/DELETE/OPTIONS` y los headers
`Authorization` y `Content-Type`. La petición *preflight* (`OPTIONS`) se
responde antes de exigir el token.

> Si abres `index.html` con doble clic (`file://`) el origen es `null` y
> CORS lo bloquea: sírvelo con Docker, Live Server o
> `python -m http.server 8081` desde `frontend/`.

## Cómo ejecutarlo

> Los comandos parten desde `14-week/02-optional-activity/` (la carpeta que
> contiene a `C3-activity/`).

### Con Docker (recomendado)

Necesitas Docker Desktop (o Docker Engine + Compose).

```bash
cd C3-activity
docker compose up --build
```

- Frontend: <http://localhost:8081>
- API: <http://localhost:8080>

Para detener: `Ctrl + C` y luego `docker compose down`.

### Sin Docker

```bash
# Terminal 1: backend (JDK 17+ y Maven)
cd C3-activity/backend
mvn spring-boot:run

# Terminal 2: frontend (cualquier servidor estático en el puerto 8081 o 5500)
cd C3-activity/frontend
python -m http.server 8081
```

## Pruebas

### Backend (`mvn test`)

`AuthSecurityTest` levanta la app y verifica:

| Prueba | Resultado esperado |
|---|---|
| Login con credenciales válidas | `200` + token |
| **Login con contraseña incorrecta** (error) | `401` |
| **Endpoint protegido sin token** (error) | `401` |
| **Endpoint protegido con token falso** (error) | `401` |
| Endpoint protegido con token válido | `200` + lista |
| Preflight CORS desde `http://localhost:8081` | `200` + header `Access-Control-Allow-Origin` |

```bash
cd C3-activity/backend
mvn test
```

### Frontend (`npm test`)

`test/api.test.js` usa el test runner incluido en Node (sin instalar
dependencias; requiere Node 18+). Prueba `login()`, `getRecipes()` y
`totalMinutes()` con un `fetch` simulado, incluyendo los **casos de
error**: credenciales incorrectas (`401`), backend apagado y token
vencido.

```bash
cd C3-activity/frontend
npm test
```

## Security & run

The API is secured with JSON Web Tokens: a client sends its username and
password to `POST /api/auth/login`, and if they are valid the server
returns a signed JWT that expires after one hour. The frontend stores
that token and sends it in the `Authorization: Bearer` header of every
request, while a filter on the server validates it and answers `401`
when the token is missing or invalid. Because the frontend and the API
run on different origins, CORS is configured so that only the allowed
frontend origins can call the API from the browser. The backend tests
(JUnit and MockMvc) cover the login, the protected endpoints, the `401`
error cases and the CORS preflight. The frontend tests (the Node test
runner with a mocked `fetch`) cover the login, the data request and its
error cases, such as wrong credentials, an unreachable server and an
expired token. The backend and the frontend each have their own
Dockerfile, and a `docker-compose.yml` file starts both containers
together. To run everything with Docker, execute `docker compose up
--build` inside the `C3-activity` folder, then open `http://localhost:8081`
and sign in with the demo user `admin` and password `admin123`.

## Notas de seguridad

- El usuario `admin/admin123` y el secreto JWT por defecto son solo para
  la demo. En un proyecto real los usuarios van en base de datos y el
  secreto se pasa por la variable `JWT_SECRET`.
- CSRF está desactivado a propósito: la API es *stateless* y usa un
  token en el header, no cookies de sesión.

## Cómo se versionó con Git

```bash
git clone <https://github.com/cristianmunoz2006/complementaria-iii-desarrollo-fullstack-2026-b-g1.git>
cd complementaria-iii-desarrollo-fullstack-2026-b-g1
# coloca esta entrega dentro de: 14-week/02-optional-activity/C3-activity/
git add .
git commit -m "Entrega semana 14 - App fullstack segura (JWT, pruebas, Docker)"
git push origin main
```
