package es.codeurjc.web.nitflex.integration;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmSimpleDTO;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.utils.ImageUtils;
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
    
    @Autowired
    private ImageUtils imageUtils;
    
    private boolean areSameBlob(Blob blob1, Blob blob2) {
        if (blob1 == blob2) return true;
        if (blob1 == null || blob2 == null) return false;
        
        try {
            if (blob1.length() != blob2.length()) return false;
            
            byte[] bytes1 = blob1.getBytes(1, (int) blob1.length());
            byte[] bytes2 = blob2.getBytes(1, (int) blob2.length());
            
            return Arrays.equals(bytes1, bytes2);
        } catch (SQLException e) {
            throw new RuntimeException("Error comparing blobs", e);
        }
    }
    
    @Test
    public void filmValidTitle_then_FilmIsSaveInDatabase() {
        String validTitle = "Integration Test for nitflex";
        String synopsis = "An awesome integration test to test my awesome skills";
        int releaseYear = 2024;
        String ageRating = "+12";
        
        CreateFilmRequest filmRequest = new CreateFilmRequest(validTitle, synopsis, releaseYear, ageRating);
        
        FilmDTO savedFilm = filmService.save(filmRequest);
        
        assertNotNull(savedFilm);
        assertEquals(validTitle, savedFilm.title());
        assertTrue(filmRepository.existsById(savedFilm.id()));
        
        System.out.println("TEST PASSED! Film saved: ID=" + savedFilm.id() + ", Title=" + savedFilm.title());
    }

    @Test
    @Transactional
    public void updateFilmWithoutImage_then_changesSaved_and_favoritesAreKept() {
        String originalTitle = "title for test";
        String originalSynopsis = "a movie you wouldn't wanna see";
        int releaseYear = 2023;
        String ageRating = "+16";
        
        CreateFilmRequest createRequest = new CreateFilmRequest(originalTitle, originalSynopsis, releaseYear, ageRating);
        FilmDTO createdFilm = filmService.save(createRequest);
        
        User user = new User();
        user.setName("user for testeo");
        user.setEmail("testeo@example.com");
        
        if (user.getFavoriteFilms() == null) {
            user.setFavoriteFilms(new ArrayList<>());
        }
        
        user = userRepository.save(user);
        
        Film film = filmRepository.findById(createdFilm.id()).orElseThrow();
        
        user.getFavoriteFilms().add(film);
        film.getUsersThatLiked().add(user);
        userRepository.save(user);
    }

    @Test
@Transactional
public void updateFilmWithImage_then_changesSaved_and_imageRemains() {
    String originalTitle = "The Blob That Wouldn't Die";
    String originalSynopsis = "A story about a persistent blob in a database";
    int originalYear = 2022;
    String originalRating = "+12";
    
    CreateFilmRequest createRequest = new CreateFilmRequest(originalTitle, originalSynopsis, originalYear, originalRating);
    
    MultipartFile sampleImage = new MockMultipartFile(
        "image.jpg", 
        "image.jpg",
        "image/jpeg", 
        "test image content".getBytes()
    );
    
    FilmDTO createdFilm = filmService.save(createRequest, sampleImage);
    assertNotNull(createdFilm);
    assertNotNull(createdFilm.id());
    
    Film originalFilm = filmRepository.findById(createdFilm.id()).orElseThrow();
    Blob originalImage = originalFilm.getPosterFile();
    assertNotNull(originalImage, "Image should exist");
    
    User user = new User("Cinephile User", "movies_rock@example.com");
    userRepository.save(user);
    
    user.getFavoriteFilms().add(originalFilm);
    originalFilm.getUsersThatLiked().add(user);
    userRepository.save(user);
    
    FilmSimpleDTO updateRequest = new FilmSimpleDTO(
        createdFilm.id(),
        "The Blob That Wouldn't Die: The Sequel",
        "The blob returns with a vengeance",
        2023,
        "+16"
    );
    
    FilmDTO updatedFilm = filmService.update(createdFilm.id(), updateRequest);
    
    assertNotNull(updatedFilm);
    assertEquals("The Blob That Wouldn't Die: The Sequel", updatedFilm.title());
    assertEquals("The blob returns with a vengeance", updatedFilm.synopsis());
    assertEquals(2023, updatedFilm.releaseYear());
    assertEquals("+16", updatedFilm.ageRating());
    
    Film updatedFilmEntity = filmRepository.findById(createdFilm.id()).orElseThrow();
    
    // Detailed blob comparison with diagnostics
    try {
        // Compare blob details
        System.out.println("Original Image Length: " + originalImage.length());
        System.out.println("Updated Image Length: " + updatedFilmEntity.getPosterFile().length());
        
        // Convert blobs to byte arrays for detailed comparison
        byte[] originalBytes = originalImage.getBytes(1, (int)originalImage.length());
        byte[] updatedBytes = updatedFilmEntity.getPosterFile().getBytes(1, (int)updatedFilmEntity.getPosterFile().length());
        
        System.out.println("Original Bytes Length: " + originalBytes.length);
        System.out.println("Updated Bytes Length: " + updatedBytes.length);
        
        // Print first few bytes for comparison
        System.out.print("Original Bytes (first 10): ");
        for (int i = 0; i < Math.min(10, originalBytes.length); i++) {
            System.out.print(originalBytes[i] + " ");
        }
        System.out.println();
        
        System.out.print("Updated Bytes (first 10): ");
        for (int i = 0; i < Math.min(10, updatedBytes.length); i++) {
            System.out.print(updatedBytes[i] + " ");
        }
        System.out.println();
        
        // Actual comparison
        assertTrue(areSameBlob(originalImage, updatedFilmEntity.getPosterFile()), 
                   "The blob image content should remain unchanged");
    } catch (SQLException e) {
        fail("Error comparing blobs: " + e.getMessage());
    }
    
    User updatedUser = userRepository.findById(user.getId()).orElseThrow();
    assertTrue(updatedUser.getFavoriteFilms().contains(updatedFilmEntity), 
              "Our cinephile still loves this movie");
    
    System.out.println("DIRECTOR'S CUT! 'The Blob' successfully got a sequel! ID=" + updatedFilm.id() + 
                      ", New box office hit: '" + updatedFilm.title() + 
                      "' - Same blob, new adventures!");
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