package com.timerdar.FamilyFarm;

import com.timerdar.FamilyFarm.dbs.Consumers;
import com.timerdar.FamilyFarm.dbs.Orders;
import com.timerdar.FamilyFarm.dbs.Products;
import com.timerdar.FamilyFarm.models.Consumer;
import com.timerdar.FamilyFarm.models.Order;
import com.timerdar.FamilyFarm.models.Product;

public class Test {

    public static void main(String[] args) {
        Orders orders = new Orders();
        Consumers consumers = new Consumers();
        Products products = new Products();

        Product product1 = new Product("ПЯ", 5.5f);
        Product product2 = new Product("СП", 550);


        //System.out.println(products.addProduct(product1));
        //System.out.println(products.addProduct(product2));
        //System.out.println(products.getAllProducts());
        //System.out.println(products.changePrice("СП", 600));
        //System.out.println(products.getProductByName("СП"));

        Consumer consumer1 = new Consumer("Марина Д.", "40 лет Окт. 16-25", "Черниковка", "89174246755");
        Consumer consumer2 = new Consumer("АйгульЯнаби", "Янаби 24-41", "Инорс", "89273217617");

        //System.out.println(consumers.addConsumer(consumer1));
        //System.out.println(consumers.addConsumer(consumer2));
        //System.out.println(consumers.getAllConsumers());
        //System.out.println(consumers.getConsumerByName("Марина Д."));
        //System.out.println(consumers.getConsumerByName("Марина дада"));

        Order order1 = new Order("СП", "Марина Д.", 3);
        Order order2 = new Order("ПЯ", "АйгульЯнаби", 100);

        //System.out.println(orders.addOrder(order1));
        //System.out.println(orders.addOrder(order2));
        //System.out.println(orders.getAllOrders());

        //System.out.println(products.getAllProducts());
        //System.out.println(consumers.getAllConsumers());
        //System.out.println(orders.getAllOrders());


    }
}
