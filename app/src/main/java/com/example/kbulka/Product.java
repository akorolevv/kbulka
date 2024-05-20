package com.example.kbulka;

public class Product {
    private String name;
    private int imageResId;
    private int quantity; // Количество, которое добавляет пользователь
    private String quantityString; // Количество в виде строки для отображения в TextView
    private int currentQuantity; // Количество из базы данных

    // Конструктор
    public Product(String name, int imageResId, int currentQuantity) {
        this.name = name;
        this.imageResId = imageResId;
        this.quantity = 0; // Начальное значение для количества, добавляемого пользователем
        this.quantityString = "0";
        this.currentQuantity = currentQuantity;
    }

    // Геттеры и сеттеры для name и imageResId
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    // Геттеры и сеттеры для quantity
    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Геттеры и сеттеры для quantityString
    public String getQuantityString() {
        return quantityString;
    }

    public void setQuantityString(String quantityString) {
        this.quantityString = quantityString;
    }

    // Геттеры и сеттеры для currentQuantity
    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
}