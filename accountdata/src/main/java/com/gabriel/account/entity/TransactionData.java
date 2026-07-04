package com.gabriel.account.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@Entity
@Table(name = "transaction_data")
public class TransactionData {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int accountId;
    private String type; // DEPOSIT or WITHDRAWAL
    private double amount;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    private Date created;
}
