package api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestData;

import java.util.List;

import static io.restassured.RestAssured.given;

public class CreateOrderTest {
    private String accessToken;
    private String email;
    private List<String> ingredients;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        User user = TestData.getUser();
        email = user.getEmail();

        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        accessToken = response.jsonPath().getString("accessToken");

        Response ingredientsResponse = given()
                .filter(new AllureRestAssured())
                .get("/api/ingredients");

        ingredients = ingredientsResponse.jsonPath().getList("data._id");
   }

   @After
    public void tearDown(){
        if(accessToken != null){
            given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", accessToken)
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
   }

    @Test
    public void createOrderWithAuth(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body("{\"ingredients\":[\"" + ingredients.get(0) + "\",\"" + ingredients.get(1) + "\"]}")
                .post("/api/orders");

        response.then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.equalTo(true))
                .body("order.number", org.hamcrest.Matchers.notNullValue())
                .body("order.owner.email", org.hamcrest.Matchers.equalTo(email));
    }

    @Test
    public void createOrderWithoutAuth(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body("{\"ingredients\":[\"" + ingredients.get(0) + "\",\"" + ingredients.get(1) + "\"]}")
                .post("/api/orders");

        response.then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.equalTo(true))
                .body("name", org.hamcrest.Matchers.notNullValue())
                .body("order.number", org.hamcrest.Matchers.notNullValue());
    }

    @Test
    public void createOrderWithIngredients(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body("{\"ingredients\":[\"" + ingredients.get(0) + "\"]}")
                .post("/api/orders");

        response.then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.equalTo(true))
                .body("order.number", org.hamcrest.Matchers.notNullValue())
                .body("order.owner.email", org.hamcrest.Matchers.equalTo(email));
  }

    @Test
    public void createOrderWithoutIngredients(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body("{\"ingredients\":[]}")
                .post("/api/orders");

        response.then()
                .statusCode(400)
                .body("success", org.hamcrest.Matchers.equalTo(false))
                .body("message", org.hamcrest.Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    public void createOrderWithWrongHash(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body("{\"ingredients\":[\"wrong_hash\"]}")
                .post("/api/orders");

        response.then()
                .statusCode(500);
    }

}
