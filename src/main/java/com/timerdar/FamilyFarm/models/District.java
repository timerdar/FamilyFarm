package com.timerdar.FamilyFarm.models;

import lombok.Getter;
import lombok.Setter;

public class District {

    @Getter @Setter
    private int id;

    @Getter @Setter
    private String districtName;

    public District() {}

    public District(int id, String districtName) {
        this.id = id;
        this.districtName = districtName;
    }
}
