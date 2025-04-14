package es.codeurjc.web.nitflex.rest;

import static org.hamcrest.Matchers.containsString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmRestAssuredTest {

    @LocalServerPort
    private int port;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        
        // Create a test user if none exists
        if (userRepository.count() == 0) {
            User user = new User();
            user.setName("Test User");
            user.setEmail("test@example.com");
            userRepository.save(user);
        }
    }

    @Test
    public void whenAddFilmWithoutTitle_thenErrorMessageIsShown() {
        // Create a film request without title
        String jsonBody = "{"
            + "\"title\": \"\","
            + "\"synopsis\": \"Test synopsis\","
            + "\"releaseYear\": 2023,"
            + "\"ageRating\": \"+12\""
            + "}";

        // Send the request and verify the response
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(jsonBody)
        .when()
            .post("/api/films/")
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body(containsString("The title is empty"));

        System.out.println("REST TEST PASSED! Error message shown when creating film without title");
    }
}