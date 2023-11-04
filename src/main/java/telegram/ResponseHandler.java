package telegram;

import dbs.ProductDB;
import dto.Product;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import states.AddProductState;

import java.util.Map;

public class ResponseHandler {
    private final SilentSender sender;
    //private final Map<Long, AddProductState> chatStates;
    // TODO: 29.10.2023 DBContext
    public ResponseHandler(SilentSender sender) {


        this.sender = sender;
    }

    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Привет!! Как тебя зовут?");
        sender.execute(message);
    }

    public void replyToStart2(long chatId, Update update) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Рад знакомству, " + update.getMessage().getText() +"!");
        sender.execute(message);

    }

    public void replyToAddProduct(long chatId){
        ProductDB db = new ProductDB();
        sender.execute(new SendMessage(String.valueOf(chatId), "Введите наименование продукта"));
        sender.execute(db.addProduct(new Product("ПЯ", 5.5), chatId));
    }

    public void stopChat(long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Пока пока!");
        sender.execute(sendMessage);
    }
}


