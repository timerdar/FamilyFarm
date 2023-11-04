package dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

public class Order {
    @Getter @Setter
    private final Consumer consumer;
    @Getter @Setter
    private final Product product;
    @Getter @Setter
    private final Date start_data;
    @Getter @Setter
    private final String status;
    @Getter @Setter
    private final float amount;
    @Setter @Getter
    private final float sum;

    public Order(Consumer consumer, Product product, Date start_data, String status, float amount, float sum) {
        this.consumer = consumer;
        this.product = product;
        this.start_data = start_data;
        this.status = status;
        this.amount = amount;
        this.sum = sum;
    }
}
