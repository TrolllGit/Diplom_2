package utils;

import com.github.javafaker.Faker;
import model.User;

public class TestData {
    private static final Faker faker = new Faker();

    public static User getUser() {
        String email = faker.internet().emailAddress();
        String password = faker.internet().password(8, 12);
        String name = faker.name().firstName();
        return new User(email, password, name);
    }

    public static User getUserWithoutEmail() {
        String password = faker.internet().password(8, 12);
        String name = faker.name().firstName();
        return new User(null, password, name);
    }
}
