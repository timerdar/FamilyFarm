package telegram;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import telegram.handlers.ConsumerResponseHandler;
import telegram.handlers.DefaultResponseHandler;
import telegram.handlers.OrderResponseHandler;
import telegram.handlers.ProductResponseHandler;

@Component
public class FarmBot extends AbilityBot implements Constants{

    private final ProductResponseHandler productResponseHandler;
    private final ConsumerResponseHandler consumerResponseHandler;
    private final DefaultResponseHandler defaultResponseHandler;
    private final OrderResponseHandler orderResponseHandler;


    public FarmBot(){
        super(Constants.BOT_TOKEN, "Farm_tasks_bot");
        productResponseHandler = new ProductResponseHandler(silent);
        defaultResponseHandler = new DefaultResponseHandler(silent);
        consumerResponseHandler = new ConsumerResponseHandler(silent);
        orderResponseHandler = new OrderResponseHandler(silent);
    }

    public Ability startBot(){
        return Ability.builder()
                .name("start")
                .info("Запуск бота")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> defaultResponseHandler.replyToStart(ctx.chatId()))
                .post(ctx -> defaultResponseHandler.help(ctx.chatId()))
                .build();
    }

    //PRODUCT COMMANDS
    public Ability addProduct(){
        return Ability.builder()
                .name("add_p")
                .info("Добавить продукт в базу (Введите название и цену)")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> productResponseHandler.addProduct(ctx.chatId(), ctx.update()))
                .build();
    }
    
    public Ability productList(){
        return Ability.builder()
                .name("price_list")
                .info("Вывести прайс-лист")
                .input(0)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> productResponseHandler.listProduct(ctx.chatId()))
                .build();
    }

    public Ability changeProductPrice(){
        return Ability.builder()
                .name("change_p")
                .info("Поменять цену продукта (Введите название и новую цену)")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> productResponseHandler.changeProductPrice(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability deleteProduct(){
        return Ability.builder()
                .name("delete_p")
                .info("Удалить продукт (Введите название)")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> productResponseHandler.deleteProduct(ctx.chatId(), ctx.update()))
                .build();
    }

    //CONSUMER COMMANDS

    public Ability addConsumer(){
        return Ability.builder()
                .name("add_c")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .input(5)
                .info("Добавить нового заказчика (Введите имя, улицу, дом+кв, район, телефон)")
                .action(ctx -> consumerResponseHandler.addConsumer(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability changeConsumer(){
        return Ability.builder()
                .name("change_c")
                .info("Изменить данные заказчика(Введите имя, улицу, дом+кв, район, телефон)")
                .input(5)
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> consumerResponseHandler.changeConsumer(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability consumerListByDistrict(){
        return Ability.builder()
                .name("districts_c")
                .info("Вывести список заказчиков из определенного района(Введите район)")
                .input(1)
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> consumerResponseHandler.consumerListByDistrict(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability districtsList(){
        return Ability.builder()
                .name("districts")
                .info("Вывести список районов доставки")
                .input(0)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> consumerResponseHandler.getDistrictsList(ctx.chatId()))
                .build();
    }

    //ORDER COMMANDS

    public Ability addOrder(){
        return Ability.builder()
                .name("add_o")
                .info("Добавить заказ(ы)")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.USER)
                .action(ctx -> orderResponseHandler.addOrder(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability getUndoneOrders(){
        return Ability.builder()
                .name("undone_c")
                .info("Незакрытые заказы по пользователям")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> orderResponseHandler.undoneOrdersList(ctx.chatId()))
                .build();
    }

    public Ability getDeliveryOrders(){
        return Ability.builder()
                .name("delivery")
                .info("В доставке")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> orderResponseHandler.deliveryOrdersList(ctx.chatId()))
                .build();
    }

    public Ability moveToDelivery(){
        return Ability.builder()
                .name("to_delivery")
                .info("!Перенести заказы в доставку(Введите номера позиций)")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> orderResponseHandler.moveToDelivery(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability undoneOrdersByProducts(){
        return Ability.builder()
                .name("undone_p")
                .info("Незакрытые заказы по продуктам")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> orderResponseHandler.undoneOrdersByProducts(ctx.chatId()))
                .build();
    }

    public Ability deliveryList(){
        return Ability.builder()
                .name("roadmap")
                .info("Дорожная карта доставки")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> orderResponseHandler.deliveryList(ctx.chatId()))
                .build();
    }

    public Ability changeAmount(){
        return Ability.builder()
                .name("change_o")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> orderResponseHandler.changeAmountDelivery(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability deleteOrder(){
        return Ability.builder()
                .name("delete_o")
                .info("!Удалить заказ")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> orderResponseHandler.deleteOrder(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability addComment(){
        return Ability.builder()
                .name("comment")
                .info("!Добавить комментарий к заказу")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> orderResponseHandler.addComment(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability clearDelivery(){
        return Ability.builder()
                .name("clear_delivery")
                .info("Почистить доставку")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> orderResponseHandler.clearDelivery(ctx.chatId()))
                .build();
    }

    public Ability lastOrders(){
        return Ability.builder()
                .name("last_orders")
                .input(1)
                .info("Вывод последних заказов данного заказчика")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> orderResponseHandler.lastOrders(ctx.chatId(), ctx.update()))
                .build();
    }

    //DEFAULT COMMANDS
    public Ability stop(){
        return Ability.builder()
                .name("stop")
                .info("Выход из бота")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> defaultResponseHandler.stopChat(ctx.chatId()))
                .build();
    }

    public Ability help(){
        return Ability.builder()
                .name("help")
                .info("Вывести подробную инструкцию для работы с ботом")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> defaultResponseHandler.help(ctx.chatId()))
                .build();
    }

    public Ability test(){
        return Ability.builder()
                .name("example")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> defaultResponseHandler.example(ctx.chatId()))
                .build();
    }

    public Ability alloc(){
        return Ability.builder()
                .name("alloc")
                .info("Список команд для распределения заказов")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> defaultResponseHandler.alloc(ctx.chatId()))
                .build();
    }


    public Ability process(){
        return Ability.builder()
                .name("process")
                .info("Список команд для подготовки заказов")
                .privacy(Privacy.ADMIN)
                .locality(Locality.USER)
                .action(ctx -> defaultResponseHandler.process(ctx.chatId()))
                .build();
    }
    @Override
    public long creatorId() {
        return Constants.CREATOR_ID;
    }
}
