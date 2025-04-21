package es.codeurjc.web.nitflex.e2e;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import es.codeurjc.web.nitflex.model.User;
import es.codeurjc.web.nitflex.repository.UserRepository;
import es.codeurjc.web.nitflex.service.UserComponent;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FilmWebE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private WebDriverWait wait;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserComponent userComponent;

    @BeforeEach
    public void setup() {
        // Set up test data - create a user if needed
        if (userRepository.count() == 0) {
            User user = new User();
            user.setName("testUser");
            user.setEmail("test@example.com");
            userRepository.save(user);
        }
        
        // Configure Chrome options
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        
        // Initialize Chrome driver
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    public void teardown() {
        // Close browser
        if (driver != null) {
            driver.quit();
        }
    }


    @Test
    public void whenCreateFilmWithoutTitle_thenErrorMessageIsShownAndFilmNotInList() {

        String synopsis = "A film without a title";
        String releaseYear = "2024";
        String ageRating = "+12";

        try {
 
            driver.get("http://localhost:" + port + "/");

            // Click on "New film" button 
            WebElement newFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
            newFilmButton.click();

            // Fill the form without a title
            WebElement synopsisInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("synopsis")));
            synopsisInput.sendKeys(synopsis);

            WebElement yearInput = driver.findElement(By.name("releaseYear"));
            yearInput.clear();
            yearInput.sendKeys(releaseYear);

            driver.findElement(By.cssSelector("select[name='ageRating'] option[value='" + ageRating + "']")).click();

            // Submit the form
            WebElement submitButton = driver.findElement(By.id("Save"));
            submitButton.click();

            // Wait for the error message to appear
            WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("error-list")));
            assertTrue(errorMessage.isDisplayed(), "Error message should be displayed");

            // Verify the error message contains the expected text
            String errorText = errorMessage.getText();
            assertTrue(errorText.contains("The title is empty"), "Error message should indicate that the title is empty");

            //Home page
            driver.get("http://localhost:" + port + "/");

            // Verify the film is not in the list
            boolean isFilmInList = driver.findElements(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + synopsis + "')]")
            ).size() > 0;
            assertFalse(isFilmInList, "Film without title should not appear in the list");

            System.out.println("E2E TEST PASSED! Film without title was not created and error message was shown.");
        } catch (Exception e) {
            System.err.println("Error in test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    public void whenCreateFilmAndDelete_thenFilmDisappears() {
        // Create a unique title to avoid conflicts
        String filmTitle = "Test Film " + System.currentTimeMillis();
        
        try {
            // Navigate to the home page
            driver.get("http://localhost:" + port + "/");
            
            // Click on "New film" button (using its ID from the template)
            WebElement newFilmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("create-film")));
            newFilmButton.click();
            
            // Fill the form
            WebElement titleInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("title")));
            titleInput.sendKeys(filmTitle);
            
            WebElement synopsisInput = driver.findElement(By.name("synopsis"));
            synopsisInput.sendKeys("Film to test deletion");
            
            WebElement yearInput = driver.findElement(By.name("releaseYear"));
            yearInput.clear();
            yearInput.sendKeys("2024");
            
            // Select age rating from dropdown
            driver.findElement(By.cssSelector("select[name='ageRating'] option[value='+12']")).click();
            
            // Submit the form (using the ID from the template)
            WebElement submitButton = driver.findElement(By.id("Save"));
            submitButton.click();
            
            // Wait for film details page to load
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("film-title")));
            
            // Go back to all films
            WebElement allFilmsButton = driver.findElement(By.id("all-films"));
            allFilmsButton.click();
            
            // Verify film is in the list (using the class from the template)
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")));
            assertTrue(isFilmInList(filmTitle), "Film should be in the list after creation");
            
            // Click on the film to go to details page
            driver.findElement(By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")).click();
            
            // Click on the Remove button on the film details page
            WebElement removeButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("remove-film")));
            removeButton.click();
            
            // Wait for confirmation or redirect back to films list
            wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/"),
                ExpectedConditions.presenceOfElementLocated(By.id("create-film"))
            ));
            
            // Navigate back to films list if not already there
            if (!driver.getCurrentUrl().endsWith("/")) {
                driver.get("http://localhost:" + port + "/");
            }
            
            try {
                Thread.sleep(1000); // Small wait to ensure page is fully loaded
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            
            assertFalse(isFilmInList(filmTitle), "Film should not be in the list after deletion");
            
            System.out.println("E2E TEST PASSED! Film '" + filmTitle + "' was created and successfully deleted");
        } catch (Exception e) {
            System.err.println("Error in test: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }}
    
    private boolean isFilmInList(String filmTitle) {
        try {
            return driver.findElements(
                By.xpath("//a[contains(@class, 'film-title') and contains(text(), '" + filmTitle + "')]")
            ).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}