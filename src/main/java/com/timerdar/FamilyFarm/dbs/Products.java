package com.timerdar.FamilyFarm.dbs;

import com.timerdar.FamilyFarm.models.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Products extends DatabaseController{

    public boolean exists(String productName){
        String query = "select count(product_name) from products where product_name = '" + productName + "'";

        int found = 0;

        try (Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            if (resultSet.next()){
                found = resultSet.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return found != 0;
    }

    //Возвращает Product по product_name
    public Product getProductByName(String productName){
        String query = "select * " +
                "from products " +
                "where product_name = '" + productName + "'";

        Product product = null;

        try(Statement statement = getConnection().createStatement()){
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()){
                product = new Product(resultSet.getString(1), resultSet.getFloat(2));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        if (product == null){
            throw new NoSuchElementException("Продукт с названием " + productName + " не найден.");
        }

        return product;
    }

    //возвращает список из всех Product в таблице
    public ArrayList<Product> getAllProducts(){
        String query = "select product_name, price from products order by product_name asc";

        ArrayList<Product> productsList = new ArrayList<>();

        try(Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            while (resultSet.next()){
                Product product = new Product(resultSet.getString(1), resultSet.getFloat(2));
                productsList.add(product);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if (productsList.isEmpty()){
            throw new NoSuchElementException("Список продуктов пуст.");
        }
        return productsList;
    }

    //добавляет новый продукт и возвращает добавленный Product
    public Product addProduct(Product product){
        if (!exists(product.getProductName())){
            String query = "insert into products(product_name, price)" +
                    " values(?, ?)";


            try(Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)){

                preparedStatement.setString(1, product.getProductName());
                preparedStatement.setFloat(2, product.getPrice());

                preparedStatement.executeUpdate();
            }catch (SQLException e){
                throw new IllegalArgumentException("Невозможно добавить новый продукт.");
            }
        }

        return getProductByName(product.getProductName());
    }

    //меняет стоимость существующего продукта и возвращает объект Product с измененной стоимостью
    public Product changePrice(String productName, float newPrice){
        String query = "update products " +
                "set price = ? " +
                "where product_name = ?";

        Product product = getProductByName(productName);
        if (product.getPrice() == newPrice){
            return product;
        }

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setFloat(1, newPrice);
            preparedStatement.setString(2, productName);

            preparedStatement.executeUpdate();
        }catch (SQLException e){
            throw new NoSuchElementException("Продукт с названием " + productName + " не найден.");
        }

        return getProductByName(productName);
    }
}
