package com.timerdar.FamilyFarm.models;

import lombok.Getter;
import lombok.Setter;

public class Consumer {
    @Getter @Setter
    private int id;

    @Getter @Setter
    private String consumerName;

    @Getter @Setter
    private String address;

    @Getter @Setter
    private int district;

    @Getter @Setter
    private String phoneNumber;

    public Consumer(){}

    public Consumer(int id, String consumerName, String address, int district, String phoneNumber) {
        this.id = id;
        this.consumerName = consumerName;
        this.address = address;
        this.district = district;
        this.phoneNumber = phoneNumber;
    }
}
