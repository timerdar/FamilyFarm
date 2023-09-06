package com.timerdar.FamilyFarm.models;


import lombok.Getter;
import lombok.Setter;

public class Product {

    @Getter @Setter
    private String productName = null;

    @Getter @Setter
    private float price;

    public Product(){}

    public Product(String productName, float price) {
        this.productName = productName;
        this.price = price;
    }

    @Override
    public String toString() {
        return "{\"productName\":" + productName + "\", \"price\":" + price + '}';
    }
}
