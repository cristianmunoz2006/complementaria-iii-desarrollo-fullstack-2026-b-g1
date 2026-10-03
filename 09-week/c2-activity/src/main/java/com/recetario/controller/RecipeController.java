package com.recetario.controller;

import com.recetario.model.Recipe;
import com.recetario.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
 * Capa Controller: solo HTTP (parsear la petición, llamar al Service,
 * devolver la respuesta). No contiene lógica de negocio.
 */
@RestController
@RequestMapping("/api/recipes")
@Tag(name = "Recipes", description = "CRUD de recetas del Recetario")
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar todas las recetas")
    public List<Recipe> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener una receta por id (404 si no existe)")
    public ResponseEntity<Recipe> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear una nueva receta (400 si el body es inválido)")
    public ResponseEntity<Recipe> create(@Valid @RequestBody Recipe recipe) {
        Recipe saved = service.create(recipe);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una receta existente (404 si no existe)")
    public ResponseEntity<Recipe> update(@PathVariable Long id, @Valid @RequestBody Recipe recipe) {
        return ResponseEntity.ok(service.update(id, recipe));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una receta (404 si no existe)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
