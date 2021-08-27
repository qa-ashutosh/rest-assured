package Fundamentals;

import Files.Payload;
import Files.Utilities;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class DynamicJson {

    String id = "";

    /*@Test
    public void test(){
        System.out.println("This is new automated comment "+ (int)(Math.random() * 900 + 100));
    }*/

    @Test(dataProvider = "BooksData")
    public void addBook(String isbn, String aisle){

        RestAssured.baseURI = "http://216.10.245.166";
        String response = given().header("Content-Type","application/json")
                .body(Payload.addBook(isbn,aisle))
                .when().post("Library/Addbook.php")
                .then().assertThat().statusCode(200).extract().response().asString();

        JsonPath js = Utilities.rawToJson(response);
        id = js.get("ID");
        System.out.println(id);
        deleteBook();
    }

    //@Test
    public void deleteBook(){
        RestAssured.baseURI = "http://216.10.245.166";
        String response = given().header("Content-Type","application/json")
                .body(Payload.deleteBook(id))
                .when().post("Library/DeleteBook.php")
                .then().assertThat().statusCode(200).extract().response().asString();

        JsonPath js = Utilities.rawToJson(response);
        String result = js.get("msg");
        System.out.println(result);
    }

    @DataProvider(name = "BooksData")
    public Object[][] getDate(){

        return new Object[][] {
                {"arsc","6236"},
                {"ttsc","6446"},
                {"ebvs","1538"}
        };
    }

}
