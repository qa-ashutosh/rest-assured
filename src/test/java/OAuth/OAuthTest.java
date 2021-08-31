package OAuth;

import Deserialization_Pojo.Api;
import Deserialization_Pojo.GetCourse;
import Deserialization_Pojo.WebAutomation;
import io.restassured.parsing.Parser;
import io.restassured.path.json.JsonPath;

import java.util.List;

import static io.restassured.RestAssured.*;

public class OAuthTest {
    public static void main(String[] args) {

        // Url for google login
        // (for manual login process: then after login paste the new url in below variable 'String url')
        // https://accounts.google.com/o/oauth2/v2/auth/identifier?scope=https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email&auth_url=https%3A%2F%2Faccounts.google.com%2Fo%2Foauth2%2Fv2%2Fauth&client_id=692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com&response_type=code&redirect_uri=https%3A%2F%2Frahulshettyacademy.com%2FgetCourse.php&flowName=GeneralOAuthFlow

        String url = "https://rahulshettyacademy.com/getCourse.php?code=4%2F0AX4XfWhW4zw4hNRYqZ_RxlDnN7Vimen4jl2dTv-PEq95zomsu-2JmLKbqBLAVyhdL0Z4OA&scope=email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=0&prompt=none#";
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


        // Now we are using POJO class here 'GetCourse'
        GetCourse getCourse = given().queryParam("access_token", accessToken)
                .expect().defaultParser(Parser.JSON)  // to tell the script that what type of response format we are giving to Pojo class
                .when()
                .get("https://rahulshettyacademy.com/getCourse.php").as(GetCourse.class);

        System.out.println(getCourse.getLinkedIn());
        System.out.println(getCourse.getInstructor());

        System.out.println(getCourse.getCourses().getApi().get(1).getCourseTitle());
        System.out.println(getCourse.getCourses().getApi().get(1).getPrice());

        // we dynamically getting the price of SoapUI testing course's price below
        List<Api> apiCourses = getCourse.getCourses().getApi();
        for(int i=0; i< apiCourses.size(); i++){
            if(apiCourses.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices testing"))
                System.out.println(apiCourses.get(i).getPrice());
        }

        // Fetching all course titles of VebAutomation courses
        List<WebAutomation> webAutomationCourses = getCourse.getCourses().getWebAutomation();
        for(int i=0; i< webAutomationCourses.size(); i++){
            System.out.println(webAutomationCourses.get(i).getCourseTitle());
        }
    }

}
