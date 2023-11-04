package dbs;

import dto.Product;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.sql.*;

public class ProductDB extends DatabaseController{

    public String addProduct(Product product){

        String query = "insert into product(name, price) values(?, ?);";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getPrice());

            preparedStatement.executeUpdate();

            return "Добавлен продукт:\n\n" + product;
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }

    }

    public String  productList(){
        StringBuilder list = new StringBuilder("Прайс-лист: \n\n");

        String query = "select * from product";

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                list.append(rs.getString(2));
                list.append("\t");
                list.append(rs.getDouble(3));
                list.append(" руб/шт(кг)\n");

            }

            return list.toString();
        } catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }


    public String changePrice(Product product){
        String query = "update product set price = ? where name = ?";

        try (Connection connection = getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setDouble(1, product.getPrice());
            preparedStatement.setString(2, product.getName());

            preparedStatement.executeUpdate();
            return "Изменен продукт:\n\n" + product;
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    public String deleteProduct(String name){
        String query = "delete from product where name = ?;";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();

            return "Продукт " + name + " удален";
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

}
