package com.recetario;

import com.recetario.model.Recipe;
import com.recetario.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecetarioApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecetarioApiApplication.class, args);
    }

    /**
     * Carga un par de recetas de ejemplo al iniciar, para poder probar
     * GET/PUT/DELETE de inmediato sin tener que crear datos a mano primero.
     */
    @Bean
    CommandLineRunner seedData(RecipeRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Recipe("Pasta Carbonara", "Italiana", "Fácil", 10, 15));
                repository.save(new Recipe("Tacos al Pastor", "Mexicana", "Media", 20, 25));
            }
        };
    }
}
