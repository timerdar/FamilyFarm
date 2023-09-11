package com.timerdar.FamilyFarm.dbs;

import com.timerdar.FamilyFarm.models.Consumer;

import java.sql.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class Consumers extends DatabaseController{

    //Добавляет в таблицу Consumer
    public Consumer addConsumer(Consumer consumer){

        if (!exists(consumer.getConsumerName())){
            String query = "insert into consumers(consumer_name, address, district, phone_num)" +
                    "values(?, ?, ?, ?)";

            try(Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query)){

                preparedStatement.setString(1, consumer.getConsumerName());
                preparedStatement.setString(2, consumer.getAddress());
                preparedStatement.setString(3, consumer.getDistrict());
                preparedStatement.setString(4, consumer.getPhoneNumber());


                preparedStatement.executeUpdate();
            }catch (SQLException e){
                throw new IllegalArgumentException("Невозможно добавить нового заказчика.");
            }
        }

        return getConsumerByName(consumer.getConsumerName());
    }

    public boolean exists(String consumer_name){
        String query = "select count(consumer_name) from consumers where consumer_name = '" + consumer_name + "'";

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

    public Consumer getConsumerByName(String consumer_name){
        String query = "select consumer_name, address, district, phone_num " +
                "from consumers " +
                "where consumer_name = '" + consumer_name + "'";

        Consumer consumer = null;

        try(Statement statement = getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);

            if (resultSet.next()){
                consumer = new Consumer(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3),
                        resultSet.getString(4));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if (consumer == null){
            throw new NoSuchElementException("Заказчик с именем \"" + consumer_name + "\" не найден.");
        }

        return consumer;
    }


    //Возвращает список из всех Consumers в таблице
    public ArrayList<Consumer> getAllConsumers(){
        String query = "select consumer_name, address, district, phone_num " +
                "from consumers order by consumer_name asc";

        ArrayList<Consumer> consumersList = new ArrayList<>();

        try(Statement statement = getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(query)){
            while (resultSet.next()){
                Consumer consumer = new Consumer(resultSet.getString(1), resultSet.getString(2),
                        resultSet.getString(3), resultSet.getString(4));
                consumersList.add(consumer);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        if (consumersList.isEmpty()){
            throw new NoSuchElementException("Список заказчиков пуст.");
        }

        return consumersList;
    }
}
