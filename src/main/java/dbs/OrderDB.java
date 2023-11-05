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

    /**
     * Возвращает список заказов из view undone_orders и delivery
     * @param view вид заказов для вывода
     * @return список заказов (для delivery с суммой)
     */
    public String getOrdersList(String view){
        String table, label;
        label = switch (view) {
            default -> {
                table = "ERROR";
                yield "ERROR";
            }
            case "undone" -> {
                table = "undone_orders";
                yield "Незакрытые заказы:\n\n";
            }
            case "delivery" -> {
                table = "delivery";
                yield "В доставке:\n\n";
            }
        };
        StringBuilder list = new StringBuilder(label);
        String query = "select * from " + table + " where consumer_id = ?;";
        String allConsumers = "select distinct consumer_id from " + table + ";";
        String getCons = "select name from consumer where id = ?;";
        String getProd = "select name from product where id = ?;";

        //Получаем список consumer_id у кого есть заказ
        try (Connection connection = getConnection();
        Statement consumers = connection.createStatement()){
            ResultSet resultSet = consumers.executeQuery(allConsumers);
            while (resultSet.next()){

                int consumer_id = resultSet.getInt(1);

                PreparedStatement consumName = connection.prepareStatement(getCons);
                consumName.setInt(1, consumer_id);
                ResultSet consum = consumName.executeQuery();
                consum.next();
                String consumer_name = consum.getString(1);

                list.append(consumer_name);
                list.append("\n");

                //Список заказов каждого consumer_id, у которых есть заказы
                PreparedStatement orderSt = connection.prepareStatement(query);
                orderSt.setInt(1, consumer_id);
                ResultSet ordersRS = orderSt.executeQuery();
                while (ordersRS.next()){
                    //Получить название каждого продукта из заказов
                    PreparedStatement getProdSt = connection.prepareStatement(getProd);
                    getProdSt.setInt(1, ordersRS.getInt("product_id"));
                    ResultSet productRS = getProdSt.executeQuery();
                    productRS.next();

                    list.append(ordersRS.getInt("id"));
                    list.append(") ");
                    list.append(productRS.getString(1));
                    list.append(" ");
                    list.append(ordersRS.getFloat("amount"));
                    if(view.equals("delivery")){
                        list.append(" ");
                        list.append(ordersRS.getFloat("sum"));
                        list.append(" руб");
                    }
                    list.append("\n");
                }
                list.append("\n");
            }
            return list.toString();
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }


    /**
     * Меняет статус выбранных id заказов в "В доставке"
     * @param ids массив order_id
     * @return Полный список заказов, которые сейчас в доставке
     */
    public String moveToDelivery(String[] ids){
        String query = "update \"order\" set status_id = 2 where id = ?";

        try (Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            for (String id:ids){
                preparedStatement.setInt(1, Integer.parseInt(id));
                preparedStatement.executeUpdate();
            }

            return getOrdersList("delivery");
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }
}
