package com.periplus;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.time.Duration;

public class CartTest {
    WebDriver driver;
    WebDriverWait wait;

    private final String EMAIL = "xxx@gmail.com";
    private final String PASSWORD = "xxx";
    private final String BOOK_TITLE = "Love for Imperfect Things";

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    @Test
    public void testLoginSearchAndAddToCart() {
        // 1. Navigate to Periplus website
        driver.get("https://www.periplus.com/");

        // 2. Sign in to account
        WebElement signInBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[contains(text(),'Sign In')]")));
        signInBtn.click();

        // 3. Enter Credentials
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("email")));
        emailInput.sendKeys(EMAIL);

        WebElement passwordInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ps")));
        passwordInput.sendKeys(PASSWORD);

        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.id("button-login")));
        loginBtn.click();

        // 4. Search for book 
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".preloader")));
        WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("filter_name")));
        searchBox.sendKeys(BOOK_TITLE);
        
        WebElement searchButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("button.btnn")));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", searchButton);
        
        // 5. Wait and select For results and select first book
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".row.row-category-grid")));
        
        WebElement firstBookLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("div.row.row-category-grid > div:first-child a[href*='/p/']")));
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", firstBookLink);

        // 6. Add to cart using the correct button
        wait.until(ExpectedConditions.urlContains("/p/"));
        WebElement addToCartBtn = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("button.btn.btn-add-to-cart")));
        addToCartBtn.click();

        // 7. Click on cart icon to view cart
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(
            By.cssSelector("div#show-your-cart a.single-icon[href='https://www.periplus.com/checkout/cart']")));
        ((JavascriptExecutor)driver).executeScript("arguments[0].click();", cartIcon);

        // 8. Verify we're on the cart page
        wait.until(ExpectedConditions.urlToBe("https://www.periplus.com/checkout/cart"));

        // 9. Wait for preloader to disappear and cart page to load completely
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".preloader")));
        wait.until(ExpectedConditions.urlToBe("https://www.periplus.com/checkout/cart"));

        // 10. Verify cart is not empty by checking for cart items
        boolean isCartEmpty = driver.findElements(By.xpath("//div[contains(text(), 'Your shopping cart is empty')]")).size() > 0;
        boolean hasCartItems = driver.findElements(By.cssSelector("div.row-cart-product")).size() > 0;
        
        Assert.assertFalse(isCartEmpty, "Cart should not be empty");
        Assert.assertTrue(hasCartItems, "Cart should contain at least one item");
        
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}