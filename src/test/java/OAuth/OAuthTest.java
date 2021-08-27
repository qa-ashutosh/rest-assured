package OAuth;

import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static io.restassured.RestAssured.*;

public class OAuthTest {
    public static void main(String[] args) {

       /* System.setProperty("webdriver.chrome.driver","/Users/ashu/Tools/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.get("https://accounts.google.com/o/oauth2/v2/auth/identifier?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&auth_url=https%3A%2F%2Faccounts.google.com%2Fo%2Foauth2%2Fv2%2Fauth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https%3A%2F%2Frahulshettyacademy.com%2FgetCourse.php&flowName=GeneralOAuthFlow");
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys("ashutosh");
        driver.findElement(By.cssSelector("input[type='email']")).sendKeys(Keys.ENTER);
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys("ashutosh");
        driver.findElement(By.cssSelector("input[type='password']")).sendKeys(Keys.ENTER);
        String url = driver.getCurrentUrl();*/

        String url = "https://rahulshettyacademy.com/getCourse.php?code=4%2F0AX4XfWi2d1ciZieM04l27bDWpNADK6nL6g3tjqk8HQfiC7J2tXVltnT9803S6Uro9qj0LA&scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=0&prompt=consent#";
        String partialCode = url.split("code=")[1];
        String code = partialCode.split("&scope")[0];
        System.out.println(code);


        String accessTokenResponse = given().urlEncodingEnabled(false)    //Marked encoding as false, so that special characters in code didn't change to numeric values
                .queryParams("code", code)
                .queryParams("client_id","692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .queryParams("client_secret","erZOWM9g3UtwNRj340YYaK_W")
                .queryParams("redirect_uri","https://rahulshettyacademy.com/getCourse.php")
                .queryParams("grant_type","authorization_code")
                .when().log().all()
                .post("https://www.googleapis.com/oauth2/v4/token").asString();

        JsonPath js = new JsonPath(accessTokenResponse);
        String accessToken = js.getString("access_token");


        String response = given().queryParam("access_token", accessToken)
                .when().log().all()
                .get("https://rahulshettyacademy.com/getCourse.php").asString();

        System.out.println(response);

    }

}
