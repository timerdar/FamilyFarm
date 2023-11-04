package telegram.handlers;

import dbs.ProductDB;
import dto.Product;
import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ProductResponseHandler {

    private final SilentSender sender;
    private final ProductDB db = new ProductDB();

    public ProductResponseHandler(SilentSender sender) {
        this.sender = sender;
    }

    public void addProduct(long chatId, Update update){
        String[] message = update.getMessage().getText().split(" ");
        Product product = new Product(message[1], Double.valueOf(message[2]));
        SendMessage answer = new SendMessage();
        answer.setText(db.addProduct(product));
        answer.setChatId(chatId);
        sender.execute(answer);
    }

    public void listProduct(long chatId){
        SendMessage answer = new SendMessage();
        answer.setText(db.productList());
        answer.setChatId(chatId);
        sender.execute(answer);
    }

    public void changeProductPrice(long chatId, Update update){
        String[] message = update.getMessage().getText().split(" ");
        Product product = new Product(message[1], Double.valueOf(message[2]));
        SendMessage answer = new SendMessage();
        answer.setText(db.changePrice(product));
        answer.setChatId(chatId);
        sender.execute(answer);
    }
}


