package dbs;

import dto.Consumer;
import org.apache.commons.io.input.buffer.PeekableInputStream;

import java.sql.*;

public class ConsumerDB extends DatabaseController{

    /**
     * Метод для добавления заказчика в бд. Проверяет, есть ли такой район в бд, если есть, подставляет district_id. Если нет, добавляет сначала район, потом подставляет его dustrict_id.
     *
     * @param consumer заказчик, которого нужно добавить (Имя, Улица, ДОМ/КВ, Район, Телефон)
     * @return возвращает строку о добавлении данного пользователя или описание ошибки
     */
    public String addConsumer(Consumer consumer){
        String count = "select count(id) from district where district = '" + consumer.getDistrict() + "'"; // для проверки, есть ли такой район в бд
        String subQuery = "select id from district where district = '" + consumer.getDistrict() + "'"; //для получения district_id по названию района
        String query = "insert into consumer(name, street, room, district_id, phone)" +
                "values(?, ?, ?, ?, ?)"; //добавление заказчкика

        try(Connection connection = getConnection();
            Statement countStatement = connection.createStatement();
            Statement statement = connection.createStatement();
            PreparedStatement preparedStatement = connection.prepareStatement(query)){

            //проверка наличия района и добавление в ином случае
            ResultSet countRs = countStatement.executeQuery(count);
            if (countRs.next()){
                if (countRs.getInt(1) == 0){
                    PreparedStatement addDistrict = connection.prepareStatement("insert into district(district) values(?)");
                    addDistrict.setString(1, consumer.getDistrict());
                    addDistrict.executeUpdate();
                }
            }

            //получение district_id по названию для след. запроса
            ResultSet resultSet = statement.executeQuery(subQuery);
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

    /**
     * Метод удаляет запись о заказчике и добавляет новое
     * @param consumer новые данные заказчика (проверка идет по имени)
     * @return возвращает строку о добавлении пользователя с новыми данными
     */
    public String changeConsumer(Consumer consumer){
        String deleteQuery = "delete from consumer where name = ?";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)){

            preparedStatement.setString(1, consumer.getName());
            preparedStatement.executeUpdate();

            return addConsumer(consumer);
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    /**
     * Метод выдает список заказчиков определенного района
     * @param district район, по которому ищем заказчиков
     * @return возварщает список заказчиков
     */
    public String consumerListByDistrict(String district){
        StringBuilder list = new StringBuilder("Список заказчиков из района " + district + ":\n");
        String districtQuery = "select id from district where district = '" + district + "'";
        String query = "select name, street, room, phone " +
                "from consumer " +
                "where district_id = ?";

        try (Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(districtQuery);
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            int district_id = 0;
            if (resultSet.next()){
                district_id = resultSet.getInt("id");
            }

            preparedStatement.setInt(1, district_id);

            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                list.append("\n");
                list.append(rs.getString(1));
                list.append(" ");
                list.append(rs.getString(2));
                list.append(" ");
                list.append(rs.getString(3));
                list.append(" ");
                list.append(rs.getString(4));
            }
            return list.toString();
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    /**
     * Метод для просмотра списка районов доставки
     * @return список районов
     */
    public String getDistrictsList(){
        StringBuilder list = new StringBuilder("Районы доставки:\n");
        String query = "select district from district";

        try(Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query)){

            while (resultSet.next()){
                list.append("\n");
                list.append(resultSet.getString(1));
            }
            return list.toString();
        }catch (SQLException e){
            return "Ошибка:\n" + e.getMessage();
        }
    }
}
