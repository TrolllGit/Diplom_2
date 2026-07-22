package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateUserTest {
    private String accessToken;

    @Before
    public void setUp(){
    }

    @After
    public void tearDown(){
        if (accessToken != null){
            ApiClient.deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка успешной регистрации нового пользователя с валидными данными")
    public void createUniqueUser(){
        User user = User.getRandomUser();

        Response response = ApiClient.registerUser(user);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue());

        accessToken = response.jsonPath().getString("accessToken");
    }

    @Test
    @DisplayName("Создание существующего пользователя")
    @Description("Проверка ошибки при попытке зарегистрировать пользователя с уже существующим email")
    public void createExistingUser(){
        User user = User.getRandomUser();

        Response firstResponse = ApiClient.registerUser(user);
        accessToken = firstResponse.jsonPath().getString("accessToken");

        Response secondResponse = ApiClient.registerUser(user);

        secondResponse.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка ошибки при регистрации без обязательного поля email")
    public void createUserWithoutRequiredField(){
        User user = User.getUserWithoutEmail();
        Response response = ApiClient.registerUser(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Проверка ошибки при регистрации без обязательного поля password")
    public void createUserWithoutPassword(){
        User user = User.getUserWithoutPassword();
        Response response = ApiClient.registerUser(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Проверка ошибки при регистрации без обязательного поля name")
    public void createUserWithoutName(){
        User user = User.getUserWithoutName();
        Response response = ApiClient.registerUser(user);

        response.then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
