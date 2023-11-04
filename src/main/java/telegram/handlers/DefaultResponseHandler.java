package telegram.handlers;

import dbs.ProductDB;
import dto.Product;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import telegram.Constants;

public class DefaultResponseHandler {
        private final SilentSender sender;

        public DefaultResponseHandler(SilentSender sender) {
            this.sender = sender;
        }

        public void replyToStart(long chatId) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("Привет!");
            sender.execute(message);
        }

        public void stopChat(long chatId) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Пока!");
            sender.execute(sendMessage);
        }

        public void help(long chatId){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(Constants.HELP_MESSAGE);
            sender.execute(sendMessage);
        }

        public void test(long chatId){
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(Constants.TEST);
            sender.execute(sendMessage);
        }
}
