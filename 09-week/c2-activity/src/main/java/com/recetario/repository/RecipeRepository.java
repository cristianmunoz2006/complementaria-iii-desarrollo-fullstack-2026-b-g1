package com.recetario.repository;

import com.recetario.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    // Consulta por método (reutilizada de la Semana 7)
    List<Recipe> findByCuisine(String cuisine);
}
