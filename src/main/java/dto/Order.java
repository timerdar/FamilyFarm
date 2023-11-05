package dto;

import lombok.Getter;
import lombok.Setter;
import org.mapdb.StoreTrivial;

import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

public class Order {
    @Getter @Setter
    private final String consumer_name;
    @Getter @Setter
    private final String  product_name;
    @Getter @Setter
    private final java.sql.Date start_data;
    @Getter @Setter
    private final float amount;

    // TODO: 05.11.2023 Сделать toString
    public Order(String consumer_name, String product_name, float amount) {
        this.consumer_name = consumer_name;
        this.product_name = product_name;
        Date d = new Date();
        this.start_data = new java.sql.Date(d.getTime());
        this.amount = amount;
    }

    @Override
    public String toString() {
        return consumer_name + " " + product_name + " " + start_data + " " + amount;
    }
}
