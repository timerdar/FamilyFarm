package dto;

import lombok.Getter;
import lombok.Setter;

public class Product {
    @Getter @Setter
    private final String name;
    @Setter @Getter
    private final Double price;

    public Product(String name, Double price){
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString(){
        return name + " " + price;
    }

}
