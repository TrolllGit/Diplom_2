package api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.User;
import model.Order;

import static io.restassured.RestAssured.given;

public class ApiClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    public static Response registerUser(User user){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/auth/register");
    }

    public static Response loginUser(User user){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/auth/login");
    }

    public static Response deleteUser(String accessToken){
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .delete("/api/auth/user");
    }

    public static Response getIngredients(){
        return given()
                .baseUri(BASE_URL)
                .get("/api/ingredients");
    }

    public static Response createOrder(Order order, String accessToken){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .post("/api/orders");
    }

    public static Response createOrderWithoutAuth(Order order){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(order)
                .post("/api/orders");
    }
}
