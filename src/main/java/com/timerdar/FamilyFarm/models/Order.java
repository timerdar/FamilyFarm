package com.timerdar.FamilyFarm.models;

import lombok.Getter;
import lombok.Setter;

public class Order {

    @Getter @Setter
    private String productName;

    @Getter @Setter
    private String consumerName;

    @Getter @Setter
    private float amount;

    public Order(){}

    public Order(String productName, String consumerName, float amount) {
        this.productName = productName;
        this.consumerName = consumerName;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return consumerName + " " + productName  + " " + amount;
    }
}
