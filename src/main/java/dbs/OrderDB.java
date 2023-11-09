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
     * Выводит список незакрытых заказов по продуктам
     * @return Список заказов
     */
    public String getOrdersListByProducts(){
        StringBuilder list = new StringBuilder("Список заказов, сгруппированный по продукции:\n\n");

        String products = "select distinct product_id from undone_orders";
        String sum = "select sum(amount) from undone_orders where product_id = ?";
        String ordersList = "select id, consumer_id, amount from undone_orders where product_id = ?";
        String consumerName = "select name from consumer where id = ?";
        String productName = "select name from product where id = ?";

        try(Connection connection = getConnection();
            Statement products_statement = connection.createStatement()){

            ResultSet products_ids = products_statement.executeQuery(products);

            while (products_ids.next()){
                int product_id = products_ids.getInt(1);

                //получение имени продукта
                PreparedStatement product_name_statement = connection.prepareStatement(productName);
                product_name_statement.setInt(1, product_id);
                ResultSet product_name_rs = product_name_statement.executeQuery();
                product_name_rs.next();
                String product_name = product_name_rs.getString(1);

                //получение суммы заказанных позиций
                PreparedStatement sum_statement = connection.prepareStatement(sum);
                sum_statement.setInt(1, product_id);
                ResultSet sum_rs = sum_statement.executeQuery();
                sum_rs.next();
                double sum_number = sum_rs.getDouble(1);

                list.append(product_name);
                list.append(" ");
                list.append(sum_number);
                list.append("\n");

                //получение списка заказов по каждой позиции
                PreparedStatement order_list_statement = connection.prepareStatement(ordersList);
                order_list_statement.setInt(1, product_id);
                ResultSet orders_rs = order_list_statement.executeQuery();
                while (orders_rs.next()){
                    int order_id = orders_rs.getInt(1);
                    int consumer_id = orders_rs.getInt(2);
                    double amount = orders_rs.getInt(3);

                    //получение имени заказчика и кол-ва данной позиции
                    PreparedStatement consumer_name_statement = connection.prepareStatement(consumerName);
                    consumer_name_statement.setInt(1, consumer_id);
                    ResultSet consumer_name_rs = consumer_name_statement.executeQuery();
                    consumer_name_rs.next();
                    String consumer_name = consumer_name_rs.getString(1);

                    list.append(order_id);
                    list.append(") ");
                    list.append(consumer_name);
                    list.append(" ");
                    list.append(amount);
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

    /**
     * Список заказов в доставку. Группировка заказчиков по району, группировка заказов по заказчикам
     * @return Список заказов в String
     */
    public String getListToDelivery(){
        StringBuilder list = new StringBuilder("Список заказов и заказчиков по районам для доставки:\n\n");

        String orderList = "select product_id, amount, \"sum\", comment from delivery where consumer_id = ?;";
        String consumersList = "select * from consumer where id in (select distinct consumer_id from delivery) and district_id = ?;";
        String productName = "select name from product where id = ?;";
        String districtName = "select district from district where id = ?;";
        String districtsList = "select distinct district_id from consumer where id in (select distinct consumer_id from delivery);";
        String totalSumCounter = "select * from sum_counter(?)";

        //получение списка id районов
        try(Connection connection = getConnection();
        Statement district_list_statement = connection.createStatement()) {
            ResultSet district_list_rs = district_list_statement.executeQuery(districtsList);
            while (district_list_rs.next()){
                int district_id = district_list_rs.getInt(1);

                //получение названия района
                PreparedStatement district_name_statement = connection.prepareStatement(districtName);
                district_name_statement.setInt(1, district_id);
                ResultSet district_name_rs = district_name_statement.executeQuery();
                district_name_rs.next();
                String district_name = district_name_rs.getString(1);

                list.append("______");
                list.append(district_name);
                list.append("______\n\n");

                PreparedStatement consumer_from_district = connection.prepareStatement(consumersList);
                consumer_from_district.setInt(1, district_id);
                ResultSet consumers_list_rs = consumer_from_district.executeQuery();
                while (consumers_list_rs.next()){

                    StringBuilder comments = new StringBuilder("Комментарии: ");
                    int consumer_id = consumers_list_rs.getInt(1);

                    PreparedStatement total_sum_statement = connection.prepareStatement(totalSumCounter);
                    total_sum_statement.setInt(1, consumer_id);
                    ResultSet total_sum_rs = total_sum_statement.executeQuery();
                    total_sum_rs.next();

                    list.append(consumers_list_rs.getString("name"));
                    list.append(" ");
                    list.append(consumers_list_rs.getString("street"));
                    list.append(" ");
                    list.append(consumers_list_rs.getString("room"));
                    list.append(" ");
                    list.append(consumers_list_rs.getString("phone"));
                    list.append(" ");
                    list.append(total_sum_rs.getDouble(1));
                    list.append(" руб\n");

                    //получение заказов этого заказчика
                    PreparedStatement orders_statement = connection.prepareStatement(orderList);
                    orders_statement.setInt(1, consumer_id);
                    ResultSet orders_rs = orders_statement.executeQuery();

                    while (orders_rs.next()){
                        int product_id = orders_rs.getInt("product_id");

                        //Получение названия продукта
                        PreparedStatement product_name_statement = connection.prepareStatement(productName);
                        product_name_statement.setInt(1, product_id);
                        ResultSet product_name_rs = product_name_statement.executeQuery();
                        product_name_rs.next();
                        String product_name = product_name_rs.getString(1);

                        list.append(product_name);
                        list.append(" ");
                        list.append(orders_rs.getDouble(2));
                        list.append(" ");
                        list.append(orders_rs.getDouble(3));
                        list.append(" руб\n");

                        comments.append(orders_rs.getString("comment"));
                    }
                    list.append(comments);
                    list.append("\n\n");
                }
                list.append("\n");
            }
            return list.toString();
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    /**
     * Меняет у заказа amount
     * @param id_amount Массив вида (order_id, new_amount...)
     */

    public String changeOrderAmount(String[] id_amount){
        String query = "update \"order\" set amount = ? where id = ?;";

        try(Connection connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(query)){

            for (int i = 0; i < id_amount.length; i += 2){
                preparedStatement.setFloat(1, Float.parseFloat(id_amount[i + 1]));
                preparedStatement.setInt(2, Integer.parseInt(id_amount[i]));

                preparedStatement.executeUpdate();
            }
            return getOrdersList("delivery");
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    /**
     * Удаление заказа по id  из delivery и undone
     */
    public String deleteOrder(String[] ids){
        String query = "delete from \"order\" where id = ?";

        try(Connection connection = getConnection();
        PreparedStatement delete_order = connection.prepareStatement(query)){

            for (String id:ids){
                delete_order.setInt(1, Integer.parseInt(id));
                delete_order.executeUpdate();
            }

            return getOrdersList("undone");
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    public String addComment(int id, String comment){
        String query = "update \"order\" set comment = ? where id = ?";

        try(Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(2, id);
            statement.setString(1, comment);
            statement.executeUpdate();

            return "К заказу " + id + " добавлен комментарий:\n" + comment;
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }

    }

    public String clearDelivery(){
        String query = "call clear_delivery();";

        try(Connection connection = getConnection();
        Statement statement = connection.createStatement()){
            statement.executeQuery(query);
            return getOrdersList("delivery");
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }

    public String lastOrders(String consumer_name){
        String query = "select * from last_10_orders(?);";
        StringBuilder list = new StringBuilder("Последние заказы " + consumer_name + ":\n\n");

        try(Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, consumer_name);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                list.append(rs.getString(1));
                list.append(" ");
                list.append(rs.getDouble(2));
                list.append(" ");
                list.append(rs.getDate(3).toString());
                list.append("\n");
            }
            return list.toString();
        }catch (Exception e){
            return "Ошибка:\n" + e.getMessage();
        }
    }
}
