# Semana 7 — Entity y repository (JPA)

> **CONFIG:** FULL_NAME: `Cristian Andres Muñoz Montenegro` · GITHUB_USER: `cristianmunoz2006`

## Caso elegido: API de Recetario

Se continúa el caso del Recetario (Semanas 3 y 6), ahora modelando su
`Entity` y su `Repository` con JPA / Spring Data.

## Estructura

```
07-week/
└── src/main/java/com/recetario/
    ├── model/
    │   └── Recipe.java         # Entity (@Entity, @Id, atributos)
    └── repository/
        └── RecipeRepository.java  # extiende JpaRepository + consulta por método
```

## Entity: `Recipe`

`Recipe.java` mapea la tabla `recipe`:

- `@Entity` marca la clase como una tabla de la base de datos.
- `@Id` + `@GeneratedValue(strategy = GenerationType.IDENTITY)` define
  `id` como llave primaria autoincremental.
- Los demás atributos (`name`, `cuisine`, `difficulty`,
  `prepTimeMinutes`, `cookTimeMinutes`) se mapean automáticamente como
  columnas, con el mismo nombre del atributo.

> Nota: se usa `jakarta.persistence` (Spring Boot 3.x). Si el proyecto
> usa Spring Boot 2.x, los imports cambian a `javax.persistence`.

## Repository: `RecipeRepository`

`RecipeRepository.java` extiende `JpaRepository<Recipe, Long>`, lo que
ya da el CRUD completo sin escribir nada más, y agrega una **consulta
por método**:

```java
List<Recipe> findByCuisine(String cuisine);
```

Spring Data JPA interpreta el nombre del método (`findBy` + `Cuisine`)
y genera la consulta SQL automáticamente — no hay que escribir SQL a
mano.

## Operaciones CRUD y para qué se usarían

| Operación | Método heredado | Para qué se usa en este caso |
|---|---|---|
| **Create** | `recipeRepository.save(new Recipe(...))` | Agregar una receta nueva al recetario. |
| **Read** | `recipeRepository.findById(id)` / `findAll()` | Consultar una receta puntual, o listar todas para mostrarlas en el frontend. |
| **Update** | `recipeRepository.save(recipeExistente)` | Modificar una receta ya guardada (p. ej. corregir el tiempo de cocción); JPA actualiza en vez de insertar porque el `id` ya existe. |
| **Delete** | `recipeRepository.deleteById(id)` | Eliminar una receta que ya no debe estar disponible. |
| **Consulta personalizada** | `recipeRepository.findByCuisine("Italian")` | Listar solo las recetas de una cocina específica, sin tener que filtrar en memoria. |

## Cómo se versionó con Git

```bash
git clone <URL-de-tu-fork>
cd <carpeta-del-repo>
# coloca esta entrega dentro de la carpeta 07-week/
git add .
git commit -m "Entrega semana 07"
git push origin main
```
