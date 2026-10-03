package com.recetario.repository;

import com.recetario.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository de Recipe. Al extender JpaRepository<Recipe, Long> ya se
 * obtienen gratis las operaciones CRUD básicas (save, findById, findAll,
 * deleteById, etc.) sin escribir ninguna consulta a mano.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Consulta por método: Spring Data JPA genera la consulta SQL
    // automáticamente a partir del nombre del método (sin SQL explícito).
    List<Recipe> findByCuisine(String cuisine);
}
