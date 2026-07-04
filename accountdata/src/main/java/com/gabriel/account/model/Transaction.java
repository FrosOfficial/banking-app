package com.gabriel.account.model;

import lombok.Data;
import java.util.Date;

@Data
public class Transaction {
    private int id;
    private int accountId;
    private String type;
    private double amount;
    private Date created;
}
