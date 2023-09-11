package com.timerdar.FamilyFarm.models;

import lombok.Getter;
import lombok.Setter;

public class Consumer {

    @Getter @Setter
    private String consumerName;

    @Getter @Setter
    private String address;

    @Getter @Setter
    private String district;

    @Getter @Setter
    private String phoneNumber;

    public Consumer(){}

    public Consumer(String consumerName, String address, String district, String phoneNumber) {
        this.consumerName = consumerName;
        this.address = address;
        this.district = district;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return consumerName + " " + address + " " + district + " " + phoneNumber;
    }
}
