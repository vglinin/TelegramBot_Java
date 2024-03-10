package bot.command;

import bot.service.MyTelegramBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ButtonCreator {

    private final MyTelegramBot telegramBot;

    public ButtonCreator(@Lazy MyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void newButton(long chatId, String text, String[] buttonArray) throws IOException {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();

        for (String buttonText : buttonArray) {
            var button = new InlineKeyboardButton();
            button.setText(buttonText);
            button.setCallbackData(buttonText + "_BUTTON");
            rowInLine.add(button);
        }

        rowsInLine.add(rowInLine);
        markup.setKeyboard(rowsInLine);
        message.setReplyMarkup(markup);

        telegramBot.executeMessage(message);
    }
}
