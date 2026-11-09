package com.recetario;

import com.recetario.model.Recipe;
import com.recetario.repository.RecipeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RecetarioSecureApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecetarioSecureApplication.class, args);
    }

    /** Carga recetas de ejemplo para que la vista del frontend tenga datos. */
    @Bean
    CommandLineRunner seedData(RecipeRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new Recipe("Pasta Carbonara", "Italiana", "Fácil", 10, 15));
                repository.save(new Recipe("Tacos al Pastor", "Mexicana", "Media", 20, 25));
                repository.save(new Recipe("Ceviche", "Peruana", "Media", 20, 0));
            }
        };
    }
}
