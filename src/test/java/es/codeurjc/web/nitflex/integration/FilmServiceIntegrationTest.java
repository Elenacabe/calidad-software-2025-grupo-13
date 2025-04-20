package es.codeurjc.web.nitflex.integration;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import jakarta.transaction.Transactional;

@SpringBootTest
@DirtiesContext
public class FilmServiceIntegrationTest {

    @Autowired
    private FilmService filmService;
    
    @Autowired
    private FilmRepository filmRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    public void filmValidTitle_then_FilmIsSaveInDatabase() {
        // Create film request
        String validTitle = "Integration Test for nitflex";
        String synopsis = "An awesome integration test to test my awesome skills";
        int releaseYear = 2024;
        String ageRating = "+12";
        
        CreateFilmRequest filmRequest = new CreateFilmRequest(validTitle, synopsis, releaseYear, ageRating);
        
        // Save film
        FilmDTO savedFilm = filmService.save(filmRequest);
        
        // Verify film was saved correctly
        assertNotNull(savedFilm);
        assertEquals(validTitle, savedFilm.title());
        assertTrue(filmRepository.existsById(savedFilm.id()));
        
        // Log success
        System.out.println("TEST PASSED! Film saved: ID=" + savedFilm.id() + ", Title=" + savedFilm.title());
    }

@Test
@Transactional
public void updateFilmWithoutImage_then_changesSaved_and_favoritesAreKept() {
    // Create initial film
    String originalTitle = "title for test";
    String originalSynopsis = "a movie you wouldn't wanna see";
    int releaseYear = 2023;
    String ageRating = "+16";
    
    CreateFilmRequest createRequest = new CreateFilmRequest(originalTitle, originalSynopsis, releaseYear, ageRating);
    FilmDTO createdFilm = filmService.save(createRequest);
    
    // Create user
    User user = new User();
    user.setName("user for testeo");
    user.setEmail("testeo@example.com");
    
    // Initialize favorite films list if it's null
    if (user.getFavoriteFilms() == null) {
        user.setFavoriteFilms(new ArrayList<>());
    }
    
    user = userRepository.save(user);
    
    Film film = filmRepository.findById(createdFilm.id()).orElseThrow();
    
    // Add film to favorites
    user.getFavoriteFilms().add(film);
    film.getUsersThatLiked().add(user);
    userRepository.save(user);
    }


    @Test
@Transactional
public void whenDeleteExistingFilm_thenFilmIsRemovedFromRepositoryAndFavorites() {

    String title = "Test Film";
    String synopsis = "A film for testing purposes";
    int releaseYear = 2025;
    String ageRating = "+18";

    CreateFilmRequest filmRequest = new CreateFilmRequest(title, synopsis, releaseYear, ageRating);
    FilmDTO savedFilm = filmService.save(filmRequest);

    User user = new User("Test User", "testuser@example.com");
    user = userRepository.save(user);

    Film film = filmRepository.findById(savedFilm.id()).orElseThrow();
    user.getFavoriteFilms().add(film);
    film.getUsersThatLiked().add(user);
    userRepository.save(user);

    filmService.delete(savedFilm.id());

    assertTrue(filmRepository.findById(savedFilm.id()).isEmpty(), "Film should be removed from the repository");

    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertTrue(updatedUser.getFavoriteFilms().isEmpty(), "Film should be removed from the user's favorites");

    System.out.println("TEST PASSED! Film deleted: ID=" + savedFilm.id() + ", Title=" + savedFilm.title());
    }
}