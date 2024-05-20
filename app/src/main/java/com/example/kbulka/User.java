package com.example.kbulka;

public class User {
    private String phoneNumber; // Изменим модификатор доступа на private
    private String password;    // Изменим модификатор доступа на private

    // Конструктор по умолчанию необходим для Firebase
    public User() {}

    public User(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }

    // Геттер для phoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Геттер для password
    public String getPassword() {
        return password;
    }

    // Можно добавить сеттеры (необязательно, если данные не меняются после создания объекта)
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
