package dbs;

import dto.Consumer;
import org.apache.commons.io.input.buffer.PeekableInputStream;

import java.sql.*;

public class ConsumerDB extends DatabaseController{

    public String addConsumer(Consumer consumer){
        String subQuery = "select id from district where district = '" + consumer.getDistrict() + "'";
        String query = "insert into consumer(name, street, room, district_id, phone)" +
                "values(?, ?, ?, ?, ?)";

        try(Connection connection = getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(subQuery);
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            int district_id = 0;
            if (resultSet.next()){
                district_id = resultSet.getInt(1);
            }

            preparedStatement.setString(1, consumer.getName());
            preparedStatement.setString(2, consumer.getStreet());
            preparedStatement.setString(3, consumer.getRoom());
            preparedStatement.setInt(4, district_id);
            preparedStatement.setString(5, consumer.getPhone());

            preparedStatement.executeUpdate();

            return "Добавлен заказчик:\n" + consumer;
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }
}
