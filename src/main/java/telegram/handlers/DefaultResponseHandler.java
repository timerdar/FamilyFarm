package telegram.handlers;

import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telegram.Constants;

public class DefaultResponseHandler {
    private final SilentSender sender;

    public DefaultResponseHandler(SilentSender sender) {
            this.sender = sender;
        }

    public void replyToStart(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(Constants.START_MESSAGE);
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

    public void example(long chatId){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(Constants.EXAMPLE);
        sender.execute(sendMessage);
    }

    public void process(long chatId){
        SendMessage answer = new SendMessage();
        answer.setText(Constants.PROCESS_MESSAGE);
        answer.setChatId(chatId);
        sender.execute(answer);
    }

    public void alloc(long chatId){
    SendMessage answer = new SendMessage();
    answer.setText(Constants.ALLOC_MESSAGE);
    answer.setChatId(chatId);
    sender.execute(answer);
    }
}
