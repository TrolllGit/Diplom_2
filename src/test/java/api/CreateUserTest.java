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


public class CreateUserTest {
    private User user;
    private String accessToken;

    @Before
    public void setUp(){
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        user = TestData.getUser();
    }

    @After
    public void tearDown(){
        if (accessToken != null){
            given()
                    .filter(new AllureRestAssured())
                    .header("Authorization", accessToken)
                    .delete("/api/auth/user")
                    .then()
                    .statusCode(202);
        }
    }

    @Test
    public void createUniqueUser(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        response.then()
                .statusCode(200)
                .body("success", org.hamcrest.Matchers.equalTo(true))
                .body("user.email", org.hamcrest.Matchers.equalTo(user.getEmail()))
                .body("user.name", org.hamcrest.Matchers.equalTo(user.getName()));

        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    public void createExistingUser(){
        given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register");

        response.then()
                .statusCode(403)
                .body("success", org.hamcrest.Matchers.equalTo(false))
                .body("message", org.hamcrest.Matchers.equalTo("User already exists"));
    }

    @Test
    public void createUserWithoutRequiredField(){
        Response response = given()
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .body(TestData.getUserWithoutEmail())
                .post("/api/auth/register");

        response.then()
                .statusCode(403)
                .body("success", org.hamcrest.Matchers.equalTo(false))
                .body("message", org.hamcrest.Matchers.equalTo("Email, password and name are required fields"));
    }





}
