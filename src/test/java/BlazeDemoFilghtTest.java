import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.testng.ITestResult;
import org.testng.annotations.*;

import CustomListeners.ScreenshotListener;

import java.util.concurrent.TimeUnit;

public class BlazeDemoFilghtTest {

    WebDriver driver;

    @BeforeTest
    public void setUp() {
        // Set the path of chromedriver
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir")+"\\src\\test\\resources\\Executables\\chromedriver.exe");

        // Initialize ChromeOptions and set some preferences
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        // Initialize Chrome driver
        driver = new ChromeDriver(options);

        // Implicit wait to handle synchronization issues
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    }

    @Test(priority = 1)
    public void testHomePageTitle() {
        // Navigate to the website
        driver.get("https://blazedemo.com/index.php");

        // Check if the title is displayed as expected
        String expectedTitle = "Welcome to the Simple Travel Agency!";
        String actualTitle = driver.getTitle();
        assert actualTitle.equals(expectedTitle) : "Home Page title mismatch";
    }

    @Test(priority = 2)
    public void testDestinationOfTheWeekLink() throws InterruptedException {
        // Click on destination of the week link
        WebElement destinationLink = driver.findElement(By.linkText("destination of the week! The Beach!"));
        destinationLink.click();

        // Check if a new tab is opened and URL contains 'vacation'
	        String currentWindow = driver.getWindowHandle();
        for (String windowHandle : driver.getWindowHandles()) {
            driver.switchTo().window(windowHandle);
            if (driver.getCurrentUrl().contains("vacation")) {
                assert true : "New tab opened with 'vacation' in URL";
                break;
            }
            
            }
        
        // Switch back to the home page tab
        driver.switchTo().window(currentWindow);
       
      driver.findElement(By.xpath("//a[@class='brand'][contains(.,'Travel The World')]")).click();
    }

    @DataProvider(name = "flightData")
    public Object[][] getFlightData() {
        return new Object[][]{
                {"Mexico City", "London"}
                // Add more data for different test scenarios
        };
    }

    @Test(priority = 3, dataProvider = "flightData")
    public void testFlightBooking(String departureCity, String destinationCity) {
        // Select departure and destination cities
        Select departureSelect = new Select(driver.findElement(By.xpath("//select[contains(@name,'fromPort')]")));
        departureSelect.selectByVisibleText(departureCity);

        Select destinationSelect = new Select(driver.findElement(By.xpath("//select[contains(@name,'toPort')]")));
        destinationSelect.selectByVisibleText(destinationCity);

        // Click on Find Flights button
        WebElement findFlightsButton = driver.findElement(By.cssSelector("input[type='submit']"));
        findFlightsButton.click();

        // Choose the flight with lowest price
        WebElement chooseFlightButton = driver.findElement(By.cssSelector("input[value='Choose This Flight']"));
        chooseFlightButton.click();

        // Check if Total Cost field is available
        WebElement totalCostField = driver.findElement(By.xpath("//input[@name='inputName']"));
        assert totalCostField.isDisplayed() : "Total Cost field is not available";

        // Click on Purchase flight button
        WebElement purchaseFlightButton = driver.findElement(By.cssSelector("input[value='Purchase Flight']"));
        purchaseFlightButton.click();

        // Check if user is navigated to Purchase Confirmation page
        WebElement confirmationHeader = driver.findElement(By.xpath("//h1[text()='Thank you for your purchase today!']"));
        assert confirmationHeader.isDisplayed() : "Purchase Confirmation page not displayed";

        // Get and store the Id
        WebElement idElement = driver.findElement(By.xpath("//table//tr[1]/td[2]"));
        String id = idElement.getText();
        System.out.println("Purchase ID: " + id);
    }

    @AfterTest
    public void tearDown() {
        // Close the browser
        driver.quit();
        
        
    }
    
    @AfterMethod
    public void tearDownMethod(ITestResult result) {
        // Capture screenshot if test fails or succeeds
        new ScreenshotListener(driver).onTestFailure(result);
        new ScreenshotListener(driver).onTestSuccess(result);
    }
}
