package dbs;

import dto.Product;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductDB extends DatabaseController{

    public SendMessage addProduct(Product product, long chatId){
        String query = "insert into product(name, price) values(?, ?);";
        SendMessage message = new SendMessage();


        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());

            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }

        message.setChatId(chatId);
        message.setText("Добавлен продукт:\n" + product);
        return message;
    }

}
