package telegram.handlers;

import dbs.ProductDB;
import dto.Product;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

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
}
