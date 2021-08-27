package JiraAPIs;

import Files.Utilities;
import io.restassured.RestAssured;
import io.restassured.filter.session.SessionFilter;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import java.io.File;

import static io.restassured.RestAssured.*;

public class JiraTest {
    public static void main(String[] args) {

        RestAssured.baseURI = "http://localhost:8080/";

        // Login scenario
        SessionFilter session = new SessionFilter();
        String response = given()
                .relaxedHTTPSValidation()    //it just to bypass the HTTPs validation on the website (which have HTTPs validations)
                .header("Content-Type","application/json")
                .body("{ \"username\": \"qa.ashutosh3\", \"password\": \"Ashutosh@145\" }").log().all()
                .filter(session)   // session will store in this variable (it should be applied in given() before the when()
                .when().post("rest/auth/1/session")
                .then().log().all().statusCode(200).extract().response().asString();

        // Adding Comment
        String message = "This is new automated comment "+ (int)(Math.random() * 900 + 100);
        String addCommentResponse = given().pathParam("id", "10100")
                .header("Content-Type","application/json")
                .body("{\n" +
                        "    \"body\": \""+message+"\",\n" +
                        "    \"visibility\": {\n" +
                        "        \"type\": \"role\",\n" +
                        "        \"value\": \"Administrators\"\n" +
                        "    }\n" +
                        "}")
                .filter(session) // this will fetch the session id from the above login session
                .when().post("/rest/api/2/issue/{id}/comment")
                .then().log().all().assertThat().statusCode(201).extract().response().asString();
        JsonPath js = Utilities.rawToJson(addCommentResponse);
        String commentID = js.getString("id");


        //Adding attachment
        given().header("X-Atlassian-Token","no-check")
                .pathParam("id", "10100")
   //no need to add this now             //.header("Content-Type","multiPart/form-data")  // need to add this header for the attachment
                .multiPart("file", new File("src/test/java/JiraAPIs/jira.txt"))  // for attachment
                .filter(session)
                .when().post("/rest/api/2/issue/{id}/attachments")
                .then().log().all().assertThat().statusCode(200);


        // Get Issue
        String issueDetails = given().filter(session)
                .pathParam("id","10100")
                .queryParam("fields","comment")  // to receives the limited response (only required (i.e. comment) fields, instead of all fields)
                .get("rest/api/2/issue/{id}")
                .then().log().all().extract().response().asString();

        JsonPath js1 = Utilities.rawToJson(issueDetails);
        int commentCount = js1.get("fields.comment.comments.size()");

        for(int i = 0; i < commentCount; i++){
            String currentCommentID = js1.get("fields.comment.comments["+i+"].id");
            if(currentCommentID.equalsIgnoreCase(commentID)){
                String expectedMessage = js1.getString("fields.comment.comments["+i+"].body");
                System.out.println(expectedMessage);
                Assert.assertEquals(message, expectedMessage);
                break;
            }

        }

    }
}
