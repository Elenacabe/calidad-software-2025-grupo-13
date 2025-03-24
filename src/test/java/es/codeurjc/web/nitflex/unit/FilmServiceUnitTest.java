package es.codeurjc.web.nitflex.unit;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import es.codeurjc.web.nitflex.dto.film.CreateFilmRequest;
import es.codeurjc.web.nitflex.dto.film.FilmDTO;
import es.codeurjc.web.nitflex.dto.film.FilmMapper;
import es.codeurjc.web.nitflex.model.Film;
import es.codeurjc.web.nitflex.repository.FilmRepository;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.FilmService;
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
}