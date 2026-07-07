package com.example.demo.config;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Seeds a handful of starter categories on first boot so the
 * frontend's "Category" dropdowns aren't empty out of the box.
 * Safe to run on every startup - it only inserts categories that
 * don't already exist.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    public DataSeeder(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) {
        List<String> defaults = List.of(
                "Computer Science",
                "Biology",
                "Physics",
                "Mathematics",
                "Economics",
                "Medicine"
        );

        for (String name : defaults) {
            boolean exists = categoryRepository.findAll().stream()
                    .anyMatch(c -> c.getName().equalsIgnoreCase(name));
            if (!exists) {
                categoryRepository.save(new Category(name));
            }
        }
    }
}
