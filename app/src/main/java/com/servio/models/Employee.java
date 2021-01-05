package com.servio.models;

import java.io.Serializable;
import java.util.List;

public class Employee implements Serializable {
    private String firstName;
    private String lastName;
    private String photo;

    protected String type;

    private Double salary;
    private String hireDate;
    private String email;
    private List<String> keyWords;
    private String userID;

    Employee(String firstName, String lastName, String photo, String type, Double salary, String hireDate, String email, List<String> keyWords, String userID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photo;
        this.type = type;
        this.salary = salary;
        this.hireDate = hireDate;
        this.email = email;
        this.keyWords = keyWords;
        this.userID = userID;
    }

    public Employee() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getType() {
        return type;
    }

    public Double getSalary() {
        return salary;
    }

    public String getHireDate() {
        return hireDate;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userID;
    }

}
