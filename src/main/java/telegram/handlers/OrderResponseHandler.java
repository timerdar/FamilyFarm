package telegram.handlers;

import dbs.OrderDB;
import dto.Order;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class OrderResponseHandler {
    private final SilentSender sender;
    private final OrderDB db = new OrderDB();
    public OrderResponseHandler(SilentSender sender) {
        this.sender = sender;
    }

    public void addOrder(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");

        Order order = new Order(message[1], message[2], Float.parseFloat(message[3]));

        answer.setChatId(chatId);
        answer.setText(db.addOrder(order));
        sender.execute(answer);
    }
}
