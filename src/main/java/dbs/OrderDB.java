package dbs;

import dto.Order;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderDB extends DatabaseController{

    /**
     * Метод проверяет, есть ли такой заказчик в бд, есть ли такой продукт в бд и в случае наличия обоих добавляет новый заказ. Иначе выводит ошибку, что чего-то нет.
     * @param order Заказ (Имя заказчика, имя продукта, дата добавления заказа, кол-во)
     * @return Текст о том, что заказ добавлен или ошибку, что нет в других таблица данных
     */

    // TODO: 05.11.2023 ДОБАВИТЬ ВОЗМОЖНОСТЬ ДОБАВЛЕНИЯ НЕСКОЛЬКИХ ПОЗИЦИЙ ЗАКАЗА ДЛЯ ОДНОГО ПОЛЬЗОВАТЕЛЯ
    // TODO: 05.11.2023 ДОБАВИТЬ В ТАБЛИЦУ ТРИГЕР ДЛЯ РАСЧЕТА ЗНАЧЕНИЯ ИТОГОВОЙ СУММЫ ПРИ ДОБАВЛЕНИИ ИЛИ ИЗМЕНЕНИИ ЗАКАЗА
    public String addOrder(@NotNull Order order){
        String existsConsumer = "select count(name) from consumer where name = '" + order.getConsumer_name() + "';";
        String existsProduct = "select count(name) from product where name = '" + order.getProduct_name() + "';";
        String getConsumerId = "select id from consumer where name = '" + order.getConsumer_name() + "';";
        String getProductId = "select id from product where name = '" + order.getProduct_name() + "';";
        String query = "insert into \"order\"(product_id, consumer_id, start_data, amount) values(?, ?, ?, ?);";

        try (Connection connection = getConnection();
             Statement existCons = connection.createStatement();
             Statement existProd = connection.createStatement();
             Statement getCons = connection.createStatement();
             Statement getProd = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(query)
        ){

            ResultSet consumers = existCons.executeQuery(existsConsumer);
            if (consumers.next()){
                if (consumers.getInt(1) == 0){
                    throw new Exception("Нет заказчика в базе. Сначала необходимо добавить заказчика.\n/add_consumer");
                }
            }

            ResultSet products = existProd.executeQuery(existsProduct);
            if (products.next()){
                if (products.getInt(1) == 0){
                    throw new Exception("Нет продукта в базе. Сначала необходимо добавить продукт.\n/add_product");
                }
            }

            ResultSet consumer = getCons.executeQuery(getConsumerId);
            consumer.next();
            int consumer_id = consumer.getInt(1);

            ResultSet product = getProd.executeQuery(getProductId);
            product.next();
            int product_id = product.getInt(1);

            preparedStatement.setInt(1, product_id);
            preparedStatement.setInt(2, consumer_id);
            preparedStatement.setDate(3, order.getStart_data());
            preparedStatement.setFloat(4, order.getAmount());

            preparedStatement.executeUpdate();

            return "Заказ добавлен:\n" + order;
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }
}
