package com.servio.models;

import java.io.Serializable;
import java.util.List;

public class Dish implements Serializable {
    private String dishName;
    private Double dishWeight;
    private Double dishPrice;
    private int dishQuantity;
    private String dishIngredients;
    private String dishId;
    private String dishCategory;
    private List<String> keyWords;

    public Dish(String dishName, Double dishWeight, Double dishPrice, int dishQuantity, String dishIngredients, String dishId, String dishCategory, List<String> keyWords) {
        this.dishName = dishName;
        this.dishWeight = dishWeight;
        this.dishPrice = dishPrice;
        this.dishQuantity = dishQuantity;
        this.dishIngredients = dishIngredients;
        this.dishId = dishId;
        this.dishCategory = dishCategory;
        this.keyWords = keyWords;
    }

    public Dish(Dish dishCopy) {
        this.dishName = dishCopy.dishName;
        this.dishWeight = dishCopy.dishWeight;
        this.dishPrice = dishCopy.dishPrice;
        this.dishQuantity = dishCopy.dishQuantity;
        this.dishIngredients = dishCopy.dishIngredients;
        this.dishId = dishCopy.dishId;
        this.dishCategory = dishCopy.dishCategory;
        this.keyWords = dishCopy.keyWords;
    }

    public Dish() {
    }

    public String getDishName() {
        return dishName;
    }

    public Double getDishWeight() {
        return dishWeight;
    }

    public Double getDishPrice() {
        return dishPrice;
    }

    public int getDishQuantity() {
        return dishQuantity;
    }

    public void setDishQuantity(int quantity) {
        dishQuantity = quantity;
    }

    public String getDishIngredients() {
        return dishIngredients;
    }

    public String getDishId() {
        return dishId;
    }

    public String getDishCategory() {
        return dishCategory;
    }

}
