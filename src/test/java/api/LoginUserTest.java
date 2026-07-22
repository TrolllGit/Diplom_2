package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginUserTest {
    private User user;
    private String accessToken;

    @Before
    public void setUp(){
        user = User.getRandomUser();
        Response registerResponse = ApiClient.registerUser(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");
    }

    @After
    public void tearDown(){
        if (accessToken != null) {
            ApiClient.deleteUser(accessToken);
            accessToken = null;
        }
    }

    @Test
    @DisplayName("Вход по существующим пользователем")
    @Description("Проверка успешной авторизации зарегистрированного пользователя")
    public void loginExistingUser(){
        User credentials = new User(user.getEmail(), user.getPassword(), null);

        Response response = ApiClient.loginUser(credentials);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    @Description("Проверка ошибки при вводе неверного пароля")
    public void loginWithWrongPassword(){
        User credentials = new User(user.getEmail(), "error123", null);

        Response response = ApiClient.loginUser(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным email")
    @Description("Проверка ошибки при вводе несуществующего email")
    public void loginWithWrongEmail(){
        User credentials = new User(user.getPassword(), "error@test.ru", null);

        Response response = ApiClient.loginUser(credentials);

        response.then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}
