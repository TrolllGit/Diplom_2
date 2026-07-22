package model;

import com.github.javafaker.Faker;

public class User {
    private String email;
    private String password;
    private String name;

    public User(String email, String password, String name){
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public String getEmail(){
        return email;
    }

    public String getPassword(){
        return password;
    }

    public String getName(){
        return name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setName(String name){
        this.name = name;
    }

    public static User getRandomUser(){
        Faker faker = new Faker();
        return new User(faker.internet().emailAddress(), faker.internet().password(8, 12), faker.name().firstName());
    }

    public static User getUserWithoutEmail(){
        Faker faker = new Faker();
        return new User(null, faker.internet().password(8, 12), faker.name().firstName());
    }

    public static User getUserWithoutPassword(){
        Faker faker = new Faker();
        return new User(faker.internet().emailAddress(), null, faker.name().firstName());
    }

    public static User getUserWithoutName(){
        Faker faker = new Faker();
        return new User(faker.internet().emailAddress(), faker.internet().password(8, 12), null);
    }
}
