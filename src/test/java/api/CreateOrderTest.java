package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class CreateOrderTest {
    private String accessToken;
    private List<String> ingredients;

    @Before
    public void setUp(){
        User user = User.getRandomUser();
        Response registerResponse = ApiClient.registerUser(user);
        accessToken = registerResponse.jsonPath().getString("accessToken");

        Response ingredientsResponse = ApiClient.getIngredients();
        ingredients = ingredientsResponse.jsonPath().getList("data._id");
   }

   @After
    public void tearDown(){
        if(accessToken != null){
            ApiClient.deleteUser(accessToken);
            accessToken = null;
        }
   }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка успешного создания заказа авторизованным пользователем")
    public void createOrderWithAuth(){
        Order order = new Order(Arrays.asList(ingredients.get(0), ingredients.get(1)));
        Response response = ApiClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("order.owner.email", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка создания заказа гостем без токена авторизации")
    public void createOrderWithoutAuth(){
        Order order = new Order(Arrays.asList(ingredients.get(0), ingredients.get(1)));
        Response response = ApiClient.createOrderWithoutAuth(order);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    @Description("Проверка создания заказа с одним валидным ингредиентом")
    public void createOrderWithIngredients(){
        Order order = new Order(Collections.singletonList(ingredients.get(0)));
        Response response = ApiClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("order.number", notNullValue());
  }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка ошибки при создании заказа без списка ингредиентов")
    public void createOrderWithoutIngredients(){
        Order order= new Order(Collections.emptyList());
        Response response = ApiClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка ошибки при использовании невалидного хеша ингредиентов")
    public void createOrderWithWrongHash(){
        Order order = new Order(Collections.singletonList("61cccccc0c5a71d1f82001bdafffffffa6f"));
        Response response = ApiClient.createOrder(order, accessToken);

        response.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

}
