package Fundamentals;

import Files.Payload;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

public class ComplexJson {

    public static void main(String[] args) {

        JsonPath js = new JsonPath(Payload.coursePriceMockResponse());

        // 1. Print number of courses return by API
        int courseCount = js.getInt("courses.size()");
        System.out.println(courseCount);

        //2. Print the purchase amount
        int purchaseAmount = js.getInt("dashboard.purchaseAmount");
        System.out.println(purchaseAmount);

        //3. Print title of first course
        String firstCourseTitle = js.getString("courses[0].title");
        System.out.println(firstCourseTitle);

        //4. Print all courses title and their respective prices
        for (int i = 0; i < courseCount; i++ ){
            String courseDetails = js.get("courses["+i+"].title") +" - "+ js.get("courses["+i+"].price");
            System.out.println(courseDetails);
        }

        //5. Print the number of copies sold by RPA course
        for (int i = 0; i < courseCount; i++ ){
            String courseTitle = js.get("courses["+i+"].title");
            if(courseTitle.equalsIgnoreCase("RPA")){
                int copiesSoldByRPA = js.getInt("courses["+i+"].copies");
                System.out.println(copiesSoldByRPA);
                break;
            }
        }

        //6. Verify if the sum of all courses Prices (i.e. price * copies) match with Purchase Amount
        int allCoursesTotalPrice = 0;
        for (int i = 0; i < courseCount; i++ ){
            int coursePrice = js.getInt("courses["+i+"].price");
            int copiesSold = js.getInt("courses["+i+"].copies");
            int courseTotalPrice = coursePrice * copiesSold;
            allCoursesTotalPrice += courseTotalPrice;
            System.out.println(allCoursesTotalPrice);
        }
        Assert.assertEquals(purchaseAmount, allCoursesTotalPrice);

    }
}
