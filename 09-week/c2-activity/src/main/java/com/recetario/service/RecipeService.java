package com.recetario.service;

import com.recetario.exception.ResourceNotFoundException;
import com.recetario.model.Recipe;
import com.recetario.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Capa de servicio: contiene la lógica de negocio (incluyendo la
 * decisión de lanzar 404 cuando una receta no existe) y orquesta al
 * Repository. El Controller nunca llama al Repository directamente.
 */
@Service
public class RecipeService {

    private final RecipeRepository repository;

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public List<Recipe> findAll() {
        return repository.findAll();
    }

    public Recipe findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con id " + id));
    }

    public Recipe create(Recipe recipe) {
        recipe.setId(null); // por si llega un id en el body, se ignora
        return repository.save(recipe);
    }

    public Recipe update(Long id, Recipe recipeData) {
        Recipe existing = findById(id); // lanza 404 si no existe
        existing.setName(recipeData.getName());
        existing.setCuisine(recipeData.getCuisine());
        existing.setDifficulty(recipeData.getDifficulty());
        existing.setPrepTimeMinutes(recipeData.getPrepTimeMinutes());
        existing.setCookTimeMinutes(recipeData.getCookTimeMinutes());
        return repository.save(existing);
    }

    public void delete(Long id) {
        Recipe existing = findById(id); // lanza 404 si no existe
        repository.delete(existing);
    }
}
