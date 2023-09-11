package com.timerdar.FamilyFarm.models;

import lombok.Getter;
import lombok.Setter;

//Current delivery order
public class CDOrder extends Order{

    @Getter @Setter
    int totalCost;
}
