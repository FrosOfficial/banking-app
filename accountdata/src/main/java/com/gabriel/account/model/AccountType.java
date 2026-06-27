package com.gabriel.account.model;

import lombok.Data;

@Data
public class AccountType {
    int id;
    String name;

    @Override
    public String toString() {
        return name;
    }
}
