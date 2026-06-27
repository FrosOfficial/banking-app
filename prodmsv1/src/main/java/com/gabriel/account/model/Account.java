package com.gabriel.account.model;

import lombok.Data;

@Data
public class Account {
    int id;
    String name;
    String description;
    int accountTypeId;
    String accountTypeName;
    double balance;
    String photo;

    @Override
    public String toString() {
        return id + " - " + name + " (₱" + balance + ")";
    }
}