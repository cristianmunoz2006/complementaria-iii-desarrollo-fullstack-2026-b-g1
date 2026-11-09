package com.recetario.controller;

import com.recetario.model.Recipe;
import com.recetario.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * URL con sustantivo ("/api/recipes"), nunca verbos como "/getRecipes" o
 * "/crearReceta" — la acción la define el método HTTP, no la URL.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    // GET /api/recipes -> listar
    @GetMapping
    public List<Recipe> getAll() {
        return service.findAll();
    }

    // GET /api/recipes/{id} -> obtener una
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/recipes -> crear
    @PostMapping
    public ResponseEntity<Recipe> create(@RequestBody Recipe recipe) {
        Recipe saved = service.create(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // PUT /api/recipes/{id} -> actualizar
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> update(@PathVariable Long id, @RequestBody Recipe recipe) {
        return service.update(id, recipe)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/recipes/{id} -> borrar
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
