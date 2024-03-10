package bot.command;

import bot.service.MyTelegramBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.io.IOException;

@Configuration
public class BotCommands {

    final MyTelegramBot telegramBot;
    final ButtonCreator buttonCreator;

    public BotCommands(@Lazy MyTelegramBot telegramBot, ButtonCreator buttonCreator) {
        this.telegramBot = telegramBot;
        this.buttonCreator = buttonCreator;
    }

    public void stopCommand(long chatId, String name) {

        telegramBot.isWaitingForUserInputYouTube = false;
        telegramBot.isWaitingForUserInputChatGPT = false;

        String text = name + " , не забывайте, что важно сохранять хорошие отношения даже при прощании. " +
                "Пожелаю вам удачи в вашем дальнейшем пути и надеюсь, что AnswersBot будет помнить вас с " +
                "добрыми воспоминаниями. До скорой встречи, возможно, " +
                "новые прогнозы будут более вдохновляющими! \uD83C\uDF0C\uD83D\uDD2E";
        telegramBot.sendMessage(chatId, text);

    }

    public void startCommand(long chatId, String[] stringsArray) throws IOException {

        var text = "Выбери нужный сервис для поиска или ответов на вопросы ниже.";

        buttonCreator.newButton(chatId, text, stringsArray);

    }
}



