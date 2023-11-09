package telegram.handlers;

import dbs.OrderDB;
import dto.Order;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;

public class OrderResponseHandler {
    private final SilentSender sender;
    private final OrderDB db = new OrderDB();
    public OrderResponseHandler(SilentSender sender) {
        this.sender = sender;
    }

    public void addOrder(long chatId, Update update){
        SendMessage answer = new SendMessage();
        StringBuilder text = new StringBuilder();
        String[] message = update.getMessage().getText().split(" ");

        for (int i = 0; i < (message.length - 2)/2; i++){
            Order order = new Order(message[1], message[2 + i*2], Float.parseFloat(message[3 + i*2]));
            text.append(db.addOrder(order));
            text.append("\n");
        }
        answer.setChatId(chatId);
        answer.setText(text.toString());
        sender.execute(answer);
    }

    public void undoneOrdersList(long chatId){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(db.getOrdersList("undone"));
        sender.execute(answer);
    }

    public void deliveryOrdersList(long chatId){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(db.getOrdersList("delivery"));
        sender.execute(answer);
    }

    public void moveToDelivery(long chatId, Update update){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        String[] nums = update.getMessage().getText().split(" ");
        String[] ids = Arrays.copyOfRange(nums, 1, nums.length);
        answer.setText(db.moveToDelivery(ids));
        sender.execute(answer);
    }

    public void undoneOrdersByProducts(long chatId){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(db.getOrdersListByProducts());
        sender.execute(answer);
    }

    public void deliveryList(long chatId){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(db.getListToDelivery());
        sender.execute(answer);
    }

    public void changeAmountDelivery(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");
        answer.setText(db.changeOrderAmount(Arrays.copyOfRange(message, 1, message.length)));
        answer.setChatId(chatId);
        sender.execute(answer);
    }

    public void deleteOrder(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");
        answer.setChatId(chatId);
        answer.setText(db.deleteOrder(Arrays.copyOfRange(message, 1, message.length)));
        answer.setParseMode(ParseMode.MARKDOWN);
        sender.execute(answer);
    }

    public void addComment(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");
        int id = Integer.parseInt(message[1]);
        StringBuilder comment = new StringBuilder();
        for (int i = 2; i < message.length; i++){
            comment.append(message[i]);
            comment.append(" ");
        }
        answer.setChatId(chatId);
        answer.setText(db.addComment(id, comment.toString()));
        sender.execute(answer);

    }

    public void clearDelivery(long chatId){
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText(db.clearDelivery());
        sender.execute(answer);
    }

    public void lastOrders(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");
        answer.setText(db.lastOrders(message[1]));
        answer.setChatId(chatId);
        sender.execute(answer);
    }
}
