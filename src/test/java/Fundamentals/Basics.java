package Fundamentals;

import Files.Payload;
import Files.Utilities;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Basics {

    public static void main(String[] args) {
        //given- all input details
        //when- submit the API (we define these in this step- resource and http methods)
        //then- validate the response
        RestAssured.baseURI = "https://rahulshettyacademy.com";
        String response = given().log().all().queryParam("key","qaclick123")
                        .header("Content-Type","application/json")
                        .body(Payload.addPlace())
                        //.body(new String(Files.readAllBytes(Paths.get("Path of Json file")))) //this is a way to directly pass the json file instead of Payload class
                        .when().post("maps/api/place/add/json")
                        .then().assertThat()
                        .statusCode(200)
                        .body("scope",equalTo("APP"))
                        .header("Server",equalTo("Apache/2.4.18 (Ubuntu)")).extract().response().asString();

        System.out.println(response);
        JsonPath js = new JsonPath(response); //for parsing json
        String placeID = js.getString("place_id");
        System.out.println(placeID);

        //Update place

        String newAddress = "70 Summer walk, USA";
        given().log().all().queryParam("key","qaclick123")
                .header("Content-Type","application/json")
                .body(Payload.updatePlace(placeID, newAddress))
                .when().put("maps/api/place/update/json")
                .then().assertThat().log().all()
                .statusCode(200)
                .body("msg",equalTo("Address successfully updated"));

        // Get place
        String updatedResponse = given().log().all().queryParam("key","qaclick123")
                                .queryParam("place_id",placeID)
                                .when().get("maps/api/place/get/json")
                                .then().assertThat().log().all()
                                .statusCode(200).extract().response().asString();

        JsonPath js1 = Utilities.rawToJson(updatedResponse);
        String updatedAddress = js1.getString("address");
        System.out.println(newAddress);
        Assert.assertEquals(updatedAddress, newAddress);
    }
}
