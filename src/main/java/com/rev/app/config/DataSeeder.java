package com.rev.app.config;

import com.rev.app.entity.Genre;
import com.rev.app.repository.IGenreRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final IGenreRepository genreRepository;

    public DataSeeder(IGenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @Override
    public void run(String... args) {
        if (genreRepository.count() == 0) {
            List<Genre> genres = List.of(
                    Genre.builder().genreName("Pop").build(),
                    Genre.builder().genreName("Rock").build(),
                    Genre.builder().genreName("Melody").build(),
                    Genre.builder().genreName("Hip-Hop").build(),
                    Genre.builder().genreName("Jazz").build(),
                    Genre.builder().genreName("Classical").build());
            genreRepository.saveAll(genres);
            System.out.println("✅ Genres seeded successfully: " + genres.size() + " genres added.");
        } else {
            System.out.println("ℹ️ Genres already present in database. Skipping seed.");
        }
    }
}
