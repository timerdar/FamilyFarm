package telegram.handlers;

import dbs.ConsumerDB;
import dbs.ProductDB;
import dto.Consumer;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ConsumerResponseHandler {
    private final SilentSender sender;
    private final ConsumerDB db = new ConsumerDB();

    public ConsumerResponseHandler(SilentSender sender) {
        this.sender = sender;
    }

    public void addConsumer(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");
        Consumer consumer = new Consumer(message[1], message[2], message[3], message[4], message[5]);

        answer.setChatId(chatId);
        answer.setText(db.addConsumer(consumer));
        sender.execute(answer);
    }

    public void changeConsumer(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");
        Consumer consumer = new Consumer(message[1], message[2], message[3], message[4], message[5]);

        answer.setChatId(chatId);
        answer.setText(db.changeConsumer(consumer));
        sender.execute(answer);
    }

    public void consumerListByDistrict(long chatId, Update update){
        SendMessage answer = new SendMessage();
        String[] message = update.getMessage().getText().split(" ");

        answer.setChatId(chatId);
        answer.setText(db.consumerListByDistrict(message[1]));
        sender.execute(answer);
    }

    public void getDistrictsList(long chatId){
        SendMessage answer = new SendMessage();
        answer.setText(db.getDistrictsList());
        answer.setChatId(chatId);
        sender.execute(answer);
    }
}
