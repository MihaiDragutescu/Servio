package com.servio.models;

import java.util.List;

public class Chef extends Employee {
    public Chef(String firstName, String lastName, String photo, String type, Double salary, String hireDate, String email, List<String> keyWords, String userId) {
        super(firstName, lastName, photo, type, salary, hireDate, email, keyWords, userId);
    }
}
