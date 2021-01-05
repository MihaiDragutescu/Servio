package com.servio.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private String orderId;
    private String orderName;
    private List<Dish> orderedDishes = new ArrayList<>();
    private String orderTime;
    private String hallName;
    private int tableNumber;
    private String waiterName;
    private Double orderPrice;
    private String orderStatus;

    public Order() {

    }

    public Order(String orderId, String orderName, List<Dish> orderedDishes, String orderTime, String hallName, int tableNumber, String waiterName, Double orderPrice, String orderStatus) {
        this.orderId = orderId;
        this.orderName = orderName;
        this.orderedDishes = orderedDishes;
        this.orderTime = orderTime;
        this.hallName = hallName;
        this.tableNumber = tableNumber;
        this.waiterName = waiterName;
        this.orderPrice = orderPrice;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public List<Dish> getOrderedDishes() {
        return orderedDishes;
    }

    public void setOrderedDishes(List<Dish> orderedDishes) {
        this.orderedDishes = orderedDishes;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public void setWaiterName(String waiterName) {
        this.waiterName = waiterName;
    }

    public Double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(Double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
