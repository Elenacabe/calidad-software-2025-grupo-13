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
import static org.hamcrest.Matchers.equalTo;

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
    public void whenAddFilmWithoutImage_thenFilmCanBeRetrievedById() {
        String jsonBody = "{"
            + "\"title\": \"Test Film\","
            + "\"synopsis\": \"A test film without an image\","
            + "\"releaseYear\": 2023,"
            + "\"ageRating\": \"+12\""
            + "}";

        // Extract the ID as Integer 
        Integer createdFilmId = 
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonBody)
            .when()
                .post("/api/films/")
            .then()
                .statusCode(HttpStatus.CREATED.value()) // Verify the status code is created
                .extract()
                .path("id");

        // Verify details
        given()
            .when()
                .get("/api/films/" + createdFilmId)
            .then()
                .statusCode(HttpStatus.OK.value()) 
                .body("id", equalTo(createdFilmId)) 
                .body("title", equalTo("Test Film")) 
                .body("synopsis", equalTo("A test film without an image")) 
                .body("releaseYear", equalTo(2023)) 
                .body("ageRating", equalTo("+12")); 

        System.out.println("REST TEST PASSED! Film created and retrieved successfully by ID.");
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


    @Test
    public void whenAddFilmAndEditTitle_thenTitleIsUpdated() {
        // Create the request body for a new film
        String jsonBody = "{"
            + "\"title\": \"Test Film\","
            + "\"synopsis\": \"A test film to edit\","
            + "\"releaseYear\": 2023,"
            + "\"ageRating\": \"+12\""
            + "}";

        // Send the POST request to create the film
        Integer createdFilmId = 
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonBody)
            .when()
                .post("/api/films/")
            .then()
                .statusCode(HttpStatus.CREATED.value()) // Verify the status code is 201 Created
                .extract()
                .path("id");

        // Create the request body for updating the film title
        String updatedJsonBody = "{"
            + "\"title\": \"Test Film - part 2\","
            + "\"synopsis\": \"A test film to edit\","
            + "\"releaseYear\": 2023,"
            + "\"ageRating\": \"+12\""
            + "}";

        // Send the PUT request to update the film title
        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(updatedJsonBody)
        .when()
            .put("/api/films/" + createdFilmId)
        .then()
            .statusCode(HttpStatus.OK.value()) // Verify the status code is 200 OK
            .body("title", equalTo("Test Film - part 2")); // Verify the title was updated

        // Retrieve the updated film and verify the title
        given()
            .when()
                .get("/api/films/" + createdFilmId)
            .then()
                .statusCode(HttpStatus.OK.value()) // Verify the status code is 200 OK
                .body("id", equalTo(createdFilmId))
                .body("title", equalTo("Test Film - part 2")) // Verify the updated title
                .body("synopsis", equalTo("A test film to edit")) // Verify the synopsis remains unchanged
                .body("releaseYear", equalTo(2023)) // Verify the release year remains unchanged
                .body("ageRating", equalTo("+12")); // Verify the age rating remains unchanged

        System.out.println("REST TEST PASSED! Film created, updated, and verified successfully.");
    }


    @Test
    public void whenAddAndDeleteFilm_thenFilmIsNotAvailable() {
        // Create the request body for a new film
        String jsonBody = "{"
            + "\"title\": \"Test Film to Delete\","
            + "\"synopsis\": \"A test film that will be deleted\","
            + "\"releaseYear\": 2023,"
            + "\"ageRating\": \"+12\""
            + "}";

        // Send the POST request to create the film
        Integer createdFilmId = 
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(jsonBody)
            .when()
                .post("/api/films/")
            .then()
                .statusCode(HttpStatus.CREATED.value()) // Verify the status code is 201 Created
                .extract()
                .path("id");

        // Send the DELETE request to remove the film
        given()
            .when()
                .delete("/api/films/" + createdFilmId)
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value()); // Verify the status code is 204 No Content

        // Attempt to retrieve the deleted film
        given()
            .when()
                .get("/api/films/" + createdFilmId)
            .then()
                .statusCode(HttpStatus.NOT_FOUND.value()); // Verify the status code is 404 Not Found

        System.out.println("REST TEST PASSED! Film created, deleted, and confirmed unavailable.");
    }
}