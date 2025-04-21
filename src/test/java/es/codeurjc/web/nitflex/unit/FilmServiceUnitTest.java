package es.codeurjc.web.nitflex.unit;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
import es.codeurjc.web.nitflex.service.exceptions.FilmNotFoundException;
import es.codeurjc.web.nitflex.utils.ImageUtils;

public class FilmServiceUnitTest {

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageUtils imageUtils;

    @Mock
    private FilmMapper filmMapper;

    @InjectMocks
    private FilmService filmService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void whenSaveFilmWithValidTitle_thenFilmIsSavedInRepository() {
        // Arrange
        String validTitle = "Life is a test";
        String synopsis = "Is this test testing me or you? or are you me or am I you? Bruh, that's a test.";
        int releaseYear = 2023;
        String ageRating = "+18";
        
        
        CreateFilmRequest filmRequest = new CreateFilmRequest(validTitle, synopsis, releaseYear, ageRating);
        
        Film film = new Film();
        film.setTitle(validTitle);
        film.setSynopsis(synopsis);
        film.setReleaseYear(releaseYear);
        film.setAgeRating(ageRating);
        
        Film savedFilm = new Film();
        savedFilm.setId(1L);
        savedFilm.setTitle(validTitle);
        savedFilm.setSynopsis(synopsis);
        savedFilm.setReleaseYear(releaseYear);
        savedFilm.setAgeRating(ageRating);
        
        FilmDTO filmDTO = new FilmDTO(1L, validTitle, synopsis, releaseYear, ageRating, new ArrayList<>(), new ArrayList<>());
        
        // Configure mocks
        when(filmMapper.toDomain(filmRequest)).thenReturn(film);
        when(filmRepository.save(film)).thenReturn(savedFilm);
        when(filmMapper.toDTO(savedFilm)).thenReturn(filmDTO);
        
        // Act
        FilmDTO result=filmService.save(filmRequest);
        
        // Assert
        verify(filmRepository).save(film);

        //log
        System.out.println("-                                                             - Movie test: " + 
                          "ID=" + result.id() + 
                          ", " + result.title() + 
                          ", " + result.synopsis() + 
                          ", " + result.releaseYear() + 
                          ", Only" + result.ageRating() + " -                                                 ");
    }

    @Test
    public void whenSaveFilmWithEmptyTitle_thenThrowsExceptionAndDoesNotSave() {
        // Arrange
        String emptyTitle = "";
        String synopsis = "This is a test synopsis";
        int releaseYear = 2024;
        String ageRating = "PG";
        
        CreateFilmRequest filmRequest = new CreateFilmRequest(emptyTitle, synopsis, releaseYear, ageRating);
        
       
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            filmService.save(filmRequest);
        });
        
        
        assertEquals("The title is empty", exception.getMessage());
        
        
        verify(filmRepository, never()).save(any(Film.class));
        
        System.out.println("-                                                             - Empty title test: Exception thrown as expected: " + 
                          exception.getMessage() + " -                                                 ");
    }

    @Test
public void whenDeleteExistingFilm_thenDeletedFromRepositoryAndUserFavorites() {
    Long existingFilmId = 1L;
    
    Film existingFilm = new Film();
    existingFilm.setId(existingFilmId);
    existingFilm.setTitle("Existing Movie");
    
    User user1 = new User("User 1", "user1@example.com");
    user1.setId(1L);
    user1.getFavoriteFilms().add(existingFilm);
    
    User user2 = new User("User 2", "user2@example.com");
    user2.setId(2L);
    user2.getFavoriteFilms().add(existingFilm);
    
    existingFilm.getUsersThatLiked().add(user1);
    existingFilm.getUsersThatLiked().add(user2);
    
    when(filmRepository.findById(existingFilmId)).thenReturn(Optional.of(existingFilm));
    
    filmService.delete(existingFilmId);
    
    verify(userRepository).save(user1);
    verify(userRepository).save(user2);
    verify(filmRepository).deleteById(existingFilmId);
    
    assertEquals(0, user1.getFavoriteFilms().size());
    assertEquals(0, user2.getFavoriteFilms().size());
    
    System.out.println("-                                                             - Delete existing film test: Film successfully deleted from repository and user favorites -                                                 ");
}

    @Test
    public void whenDeleteNonExistentFilm_thenThrowsException() {
        Long nonExistentFilmId = 999L;

        doThrow(new FilmNotFoundException(nonExistentFilmId))
            .when(filmRepository).findById(nonExistentFilmId);

        Exception exception = assertThrows(FilmNotFoundException.class, () -> {
            filmService.delete(nonExistentFilmId);
        });

        assertEquals("Film not found with id: " + nonExistentFilmId, exception.getMessage());

        verify(filmRepository).findById(nonExistentFilmId);
    }
}