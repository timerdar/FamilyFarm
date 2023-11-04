package telegram;

import org.checkerframework.checker.units.qual.A;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class FarmBot extends AbilityBot implements Constants{
    private final ResponseHandler responseHandler;

    public FarmBot(){
        super(Constants.BOT_TOKEN, "Farm_tasks_bot");
        responseHandler = new ResponseHandler(silent);
    }
    public Ability startBot(){
        return Ability.builder()
                .name("start")
                .info("Запуск бота")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> responseHandler.replyToStart(ctx.chatId()))
                .post(ctx -> responseHandler.replyToStart2(ctx.chatId(), ctx.update()))
                .build();
    }

    public Ability addProduct(){
        return Ability.builder()
                .name("addproduct")
                .info("Добавлить продукт в базу")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> responseHandler.replyToAddProduct(ctx.chatId()))
                .build();
    }




    public Ability stop(){
        return Ability.builder()
                .name("stop")
                .info("Выход из бота")
                .locality(Locality.USER)
                .privacy(Privacy.PUBLIC)
                .action(ctx -> responseHandler.stopChat(ctx.chatId()))
                .build();
    }

    @Override
    public long creatorId() {
        return Constants.CREATOR_ID;
    }
}
