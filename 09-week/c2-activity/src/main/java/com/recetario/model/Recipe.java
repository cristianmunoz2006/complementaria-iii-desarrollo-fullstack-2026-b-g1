package com.recetario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Entity que mapea la tabla "recipe". Las anotaciones de validación
 * (@NotBlank, @NotNull, @Min) son las que disparan el error 400 cuando
 * el body de la petición no cumple las reglas (ver GlobalExceptionHandler).
 */
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La cocina es obligatoria")
    private String cuisine;

    @NotBlank(message = "La dificultad es obligatoria")
    private String difficulty;

    @NotNull(message = "El tiempo de preparación es obligatorio")
    @Min(value = 0, message = "El tiempo de preparación no puede ser negativo")
    private Integer prepTimeMinutes;

    @NotNull(message = "El tiempo de cocción es obligatorio")
    @Min(value = 0, message = "El tiempo de cocción no puede ser negativo")
    private Integer cookTimeMinutes;

    public Recipe() {
        // constructor vacío requerido por JPA
    }

    public Recipe(String name, String cuisine, String difficulty,
                   Integer prepTimeMinutes, Integer cookTimeMinutes) {
        this.name = name;
        this.cuisine = cuisine;
        this.difficulty = difficulty;
        this.prepTimeMinutes = prepTimeMinutes;
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public Integer getPrepTimeMinutes() {
        return prepTimeMinutes;
    }

    public void setPrepTimeMinutes(Integer prepTimeMinutes) {
        this.prepTimeMinutes = prepTimeMinutes;
    }

    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }
}
