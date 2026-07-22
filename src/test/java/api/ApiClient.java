package api;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import model.Order;
import model.User;

import static io.restassured.RestAssured.given;

public class ApiClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    @Step("Регистрация пользователя")
    public static Response registerUser(User user){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public static Response loginUser(User user){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(user)
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя")
    public static Response deleteUser(String accessToken){
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .delete("/api/auth/user");
    }

    @Step("Получение списка ингредиентов")
    public static Response getIngredients(){
        return given()
                .baseUri(BASE_URL)
                .get("/api/ingredients");
    }

    @Step("Создание заказа с авторизацией")
    public static Response createOrder(Order order, String accessToken){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public static Response createOrderWithoutAuth(Order order){
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(order)
                .post("/api/orders");
    }
}
