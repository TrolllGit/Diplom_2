package api;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.TestData;

import static io.restassured.RestAssured.given;

public class LoginUserTest {
    private User user;
    private String accessToken;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        user = TestData.getUser();

        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        accessToken = response.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown(){
        if (accessToken != null) {
            given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", accessToken)
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }

    @Test
    public void loginExistingUser(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(new User(user.getEmail(), user.getPassword(), null))
                .post("/api/auth/login");

        response.then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.equalTo(true))
                .body("user.email", org.hamcrest.Matchers.equalTo(user.getEmail()))
                .body("user.name", org.hamcrest.Matchers.equalTo(user.getName()))
                .body("accessToken", org.hamcrest.Matchers.notNullValue())
                .body("refreshToken", org.hamcrest.Matchers.notNullValue());
    }

    @Test
    public void loginWithWrongCredentials(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(new User(user.getEmail(), "wrong", null))
                .post("/api/auth/login");

        response.then()
                .statusCode(401)
                .body("success", org.hamcrest.Matchers.equalTo(false))
                .body("message", org.hamcrest.Matchers.equalTo("email or password are incorrect"));
    }
}
