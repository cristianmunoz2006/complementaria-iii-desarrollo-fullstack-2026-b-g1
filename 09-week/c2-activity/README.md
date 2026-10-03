# Semana 9 — API REST con Spring Boot (Corte 2)

> **CONFIG:** FULL_NAME: `Cristian Andres Muñoz Montenegro` · GITHUB_USER: `cristianmunoz2006`

## Caso: API de Recetario

Se continúa el caso del Recetario (Semanas 3, 6 y 7), ahora como una API
REST completa en Spring Boot con arquitectura en capas:
`controller → service → repository → entity`.

## Estructura

```
09-week/
├── pom.xml
├── postman/
│   └── Recetario-API.postman_collection.json
└── src/main/
    ├── java/com/recetario/
    │   ├── RecetarioApiApplication.java   # arranque + datos de ejemplo
    │   ├── model/Recipe.java               # Entity (+ validaciones)
    │   ├── repository/RecipeRepository.java
    │   ├── service/RecipeService.java      # lógica de negocio
    │   ├── controller/RecipeController.java # endpoints REST + Swagger
    │   └── exception/
    │       ├── ResourceNotFoundException.java
    │       ├── ApiError.java
    │       └── GlobalExceptionHandler.java  # 404 y 400 centralizados
    └── resources/application.properties
```

## Cómo ejecutarlo

Necesitas **JDK 17+** y **Maven** instalados.

```bash
cd 09-week
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`. Al arrancar, se
cargan automáticamente 2 recetas de ejemplo (ids 1 y 2) para poder
probar GET/PUT/DELETE de inmediato.

> Usa una base de datos **H2 en memoria** (no necesitas instalar nada).
> Puedes ver los datos en `http://localhost:8080/h2-console`
> (JDBC URL: `jdbc:h2:mem:recetariodb`, usuario `sa`, sin contraseña).

## Endpoints (CRUD completo)

| Método | Endpoint | Descripción | Éxito | Error |
|---|---|---|---|---|
| GET | `/api/recipes` | Lista todas las recetas | 200 | — |
| GET | `/api/recipes/{id}` | Obtiene una receta por id | 200 | 404 si no existe |
| POST | `/api/recipes` | Crea una receta nueva | 201 | 400 si el body es inválido |
| PUT | `/api/recipes/{id}` | Actualiza una receta existente | 200 | 404 si no existe / 400 si el body es inválido |
| DELETE | `/api/recipes/{id}` | Elimina una receta | 204 | 404 si no existe |

## Swagger

Con la app corriendo, abre:

```
http://localhost:8080/swagger-ui.html
```

Ahí aparecen los 5 endpoints agrupados bajo "Recipes", con la
descripción de cada uno y un botón "Try it out" para probarlos sin
salir del navegador.

## Postman

Importa `postman/Recetario-API.postman_collection.json` en Postman
(Import → File). Incluye:

- Los 5 endpoints del CRUD (`GET` lista, `GET` uno, `POST`, `PUT`,
  `DELETE`).
- **Caso de error 404** ("ERROR 404 - Get Nonexistent Recipe"): un
  `GET /api/recipes/9999`, un id que no existe → responde `404 Not
  Found` con el mensaje del error.
- **Caso de error 400** ("ERROR 400 - Create Invalid Recipe"): un
  `POST` con `name` vacío y `prepTimeMinutes` negativo → responde `400
  Bad Request` con el detalle de qué campos fallaron.

La variable `baseUrl` ya viene configurada en `http://localhost:8080`.

## API reference (English)

The API exposes five endpoints under `/api/recipes` to manage recipes.
`GET /api/recipes` returns the full list of recipes currently stored.
`GET /api/recipes/{id}` returns a single recipe by its id, or a 404
error if that id does not exist. `POST /api/recipes` creates a new
recipe from the JSON body and returns it with a 201 status, or a 400
error if required fields are missing or invalid. `PUT
/api/recipes/{id}` updates an existing recipe's fields, returning 404
if the id is not found. `DELETE /api/recipes/{id}` removes a recipe and
returns a 204 status with no content, or 404 if the recipe does not
exist.

## Cómo se versionó con Git

```bash
git clone <URL-de-tu-fork>
cd <carpeta-del-repo>
# coloca esta entrega dentro de la carpeta 09-week/
git add .
git commit -m "Entrega semana 09 - API REST Spring Boot"
git push origin main
```

## Nota honesta

Este proyecto se escribió y revisó cuidadosamente siguiendo las
convenciones de Spring Boot 3.x (Jakarta, Spring Data JPA, springdoc
2.x), pero **no se pudo compilar ni ejecutar en este entorno** porque
no tiene acceso a internet para descargar las dependencias de Maven.
Ejecútalo tú con `mvn spring-boot:run` y revisa la consola por si tu
entorno (versión de Java, puerto ocupado, etc.) necesita algún ajuste.
