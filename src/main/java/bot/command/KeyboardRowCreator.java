package bot.command;

import bot.service.MyTelegramBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class KeyboardRowCreator {

    private final MyTelegramBot telegramBot;

    public KeyboardRowCreator(@Lazy MyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void newKeyboardRow(long chatId, String text, String[] KeyboardRowArray) {

        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        int count = 0;
        KeyboardRow currentRow = null;

        for (String buttonText : KeyboardRowArray) {
            if (count % 3 == 0) {
                currentRow = new KeyboardRow();
                keyboardRows.add(currentRow);
            }

            if (currentRow != null) {
                currentRow.add(buttonText);
            }

            count++;
        }

        markup.setKeyboard(keyboardRows);
        message.setReplyMarkup(markup);

        telegramBot.executeMessage(message);
    }
}
