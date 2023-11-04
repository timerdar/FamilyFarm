package telegram;

import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import telegram.handlers.DefaultResponseHandler;
import telegram.handlers.ProductResponseHandler;

@Component
public class FarmBot extends AbilityBot implements Constants{

    private final ProductResponseHandler productResponseHandler;
    private final DefaultResponseHandler defaultResponseHandler;


    public FarmBot(){
        super(Constants.BOT_TOKEN, "Farm_tasks_bot");
        productResponseHandler = new ProductResponseHandler(silent);
        defaultResponseHandler = new DefaultResponseHandler(silent);
    }

    public Ability startBot(){
        return Ability.builder()
                .name("start")
                .info("Запуск бота")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> defaultResponseHandler.replyToStart(ctx.chatId()))
                .build();
    }

    public Ability addProduct(){
        return Ability.builder()
                .name("add_product")
                .info("Добавлить продукт в базу (Введите название и цену)")
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .input(2)
                .action(ctx -> productResponseHandler.addProduct(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability productList(){
        return Ability.builder()
                .name("product_list")
                .info("Вывести прайс-лист")
                .input(0)
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> productResponseHandler.listProduct(ctx.chatId()))
                .build();
    }

    public Ability changeProductPrice(){
        return Ability.builder()
                .name("change_product")
                .info("Поменять цену продукта (Введите название и новую цену)")
                .input(2)
                .locality(Locality.USER)
                .privacy(Privacy.ADMIN)
                .action(ctx -> productResponseHandler.changeProductPrice(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability stop(){
        return Ability.builder()
                .name("stop")
                .info("Выход из бота")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> defaultResponseHandler.stopChat(ctx.chatId()))
                .build();
    }

    @Override
    public long creatorId() {
        return Constants.CREATOR_ID;
    }
}
