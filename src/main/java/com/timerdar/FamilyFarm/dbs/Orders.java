package com.timerdar.FamilyFarm.dbs;

import com.timerdar.FamilyFarm.models.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
public class Orders extends DatabaseController{

    //Получить заказы по productId
    public ArrayList<Order> getOrdersByProduct(String productName){
        String query = "select * from orders " +
                "where product_name = '" + productName + "'";

        ArrayList<Order> ordersList = new ArrayList<>();
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(query)){
            while (resultSet.next()){
                Order order = new Order(resultSet.getString(1), resultSet.getString(2), resultSet.getFloat(3));
                ordersList.add(order);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if (ordersList.isEmpty()){
            throw new NoSuchElementException("Нет заказов с продуктом " + (new Products().getProductByName(productName).getProductName()));
        }

        return ordersList;
    }

    //Добавить заказ. Возвращает созданный заказ.
    public Order addOrder(Order order){
        String query = "insert into orders(product_name, consumer_name, amount) " +
                "values(?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, order.getProductName());
            preparedStatement.setString(2, order.getConsumerName());
            preparedStatement.setFloat(3, order.getAmount());

            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
            //throw new IllegalArgumentException("Невозможно добавить заказ. Подобный заказ уже существует." +
                    //" Измените существующий.");
        }

        return order;
    }

    //Получить все заказы по consumer_name
    public ArrayList<Order> getOrdersByConsumerName(String consumerName){
        String query = "select * from orders " +
                "where consumer_name = '" + consumerName + "'";

        ArrayList<Order> ordersList = new ArrayList<>();

        try(Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            while (resultSet.next()){
                Order order = new Order(resultSet.getString(1), resultSet.getString(2), resultSet.getFloat(3));
                ordersList.add(order);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(ordersList.isEmpty()){
            throw new NoSuchElementException("Данный заказчик ничего не заказывал.");
        }

        return ordersList;
    }

    //Получить виды продуктов, которые заказаны
    public ArrayList<String> getOrderedProductsList(){
        String query = "select distinct product_name " +
                "from orders";

        ArrayList<String> productsList = new ArrayList<>();

        try (Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            while (resultSet.next()){
                productsList.add(resultSet.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if(productsList.isEmpty()){
            throw new NoSuchElementException("Ничего не заказано.");
        }

        return productsList;
    }

    //Получить кол-во заказанных продуктов данного вида
    public int getAmountOfOrderedProduct(String productName){
        String query = "select sum(amount) from orders " +
                "where product_name = '" + productName + "'";

        int sumAmount = 0;
        try (Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            if (resultSet.next()){
                sumAmount = resultSet.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if (sumAmount == 0){
            throw new NoSuchElementException("Данный продукт не заказывали.");
        }
        return sumAmount;
    }

    public ArrayList<Order> getAllOrders(){
        String query = "select * from orders";

        ArrayList<Order> ordersList = new ArrayList<>();

        try(Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            while(resultSet.next()){
                Order order = new Order(resultSet.getString(1), resultSet.getString(2), resultSet.getFloat(3));
                ordersList.add(order);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if (ordersList.isEmpty()){
            throw new NoSuchElementException("Список заказов пуст.");
        }

        return ordersList;
    }
}
