package com.recetario.service;

import com.recetario.model.Recipe;
import com.recetario.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio: el Controller nunca llama al Repository directamente,
 * siempre pasa por aquí.
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

    public Optional<Recipe> findById(Long id) {
        return repository.findById(id);
    }

    public Recipe create(Recipe recipe) {
        recipe.setId(null); // por si llega un id en el body, se ignora
        return repository.save(recipe);
    }

    public Optional<Recipe> update(Long id, Recipe data) {
        return repository.findById(id).map(existing -> {
            existing.setName(data.getName());
            existing.setCuisine(data.getCuisine());
            existing.setDifficulty(data.getDifficulty());
            existing.setPrepTimeMinutes(data.getPrepTimeMinutes());
            existing.setCookTimeMinutes(data.getCookTimeMinutes());
            return repository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
