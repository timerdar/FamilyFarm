package dto;

import lombok.Getter;
import lombok.Setter;

@Setter@Getter
public class Product {

    private final String name;
    private final Double price;
    private final Boolean eval;

    public Product(String name, Double price, boolean eval){
        this.name = name;
        this.price = price;
        this.eval = eval;
    }

    @Override
    public String toString(){
        return name + " " + price;
    }

}
