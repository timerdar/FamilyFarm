package com.timerdar.FamilyFarm.models;

import lombok.Getter;
import lombok.Setter;

public class Order {

    @Getter @Setter
    private int orderId;

    @Getter @Setter
    private int productId;

    @Getter @Setter
    private float amount;

    @Getter @Setter
    private int totalCost;

    @Getter @Setter
    private boolean done = false;

    @Getter @Setter
    private String note;

    public Order(){}

    public Order(int orderId, int productId, float amount, int totalCost, boolean done, String note) {
        this.orderId = orderId;
        this.productId = productId;
        this.amount = amount;
        this.totalCost = totalCost;
        this.done = done;
        this.note = note;
    }
}
