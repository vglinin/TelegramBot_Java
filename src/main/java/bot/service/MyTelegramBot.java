package bot.service;

import bot.command.*;
import bot.config.BotConfig;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;


@Component
public class MyTelegramBot extends TelegramLongPollingBot {

    public boolean isWaitingForUserInputYouTube = false;
    public boolean isWaitingForUserInputChatGPT = false;

    private final String YouTube = "Yotube поисковик";
    private final String ChatGPT = "Знающий";

    private final String START = "/start";
    private final String STOP = "/stop";

    final BotConfig config;
    final YouTubeAPIManager youTubeExample;
    final ChatGPTApiClient chatGPTExample;
    final ButtonCreator buttonCreator;
    final BotCommands commands;
    final KeyboardRowCreator keyboardRowCreator;

    public MyTelegramBot(BotConfig config, YouTubeAPIManager youTubeExample, ChatGPTApiClient chatGPTExample, ButtonCreator buttonCreator, BotCommands commands, KeyboardRowCreator keyboardRowCreator) {
        this.config = config;
        this.youTubeExample = youTubeExample;
        this.chatGPTExample = chatGPTExample;
        this.buttonCreator = buttonCreator;
        this.commands = commands;
        this.keyboardRowCreator = keyboardRowCreator;

        List<BotCommand> commandListMenu = new ArrayList<>();
        commandListMenu.add(new BotCommand(START, "Запустить бота"));
        commandListMenu.add(new BotCommand(STOP, "Остановить бота"));
        commandListMenu.add(new BotCommand("/help", "Помощь бота"));


        try {
            this.execute(new SetMyCommands(commandListMenu, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getYouTubeApi() {
        String youTubeKey = config.getYoutubeKey();
        return youTubeKey;
    }

    public String getChatGPTApi() {
        String chatGPTKey = config.getChatGPTKey();
        return chatGPTKey;
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            String name = update.getMessage().getChat().getFirstName();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case START:
                    String[] stringsArrayRow = new String[]{STOP};
                    keyboardRowCreator.newKeyboardRow(chatId, "Привет, " + name, stringsArrayRow);
                    String[] stringsArray = new String[]{YouTube, ChatGPT};
                    commands.startCommand(chatId, stringsArray);
                    break;
                case STOP:
                    commands.stopCommand(chatId, name);
                    break;
                default:
                    if (isWaitingForUserInputYouTube) {
                        youTubeExample.search(chatId, messageText);
                    } else if (isWaitingForUserInputChatGPT) {
                        sendMessage(chatId, "В ожидании ответа от Волшебника... \uD83E\uDDD9✨\uD83D\uDD2E " +
                                "пусть его магические слова принесут вам свет и мудрость, открывая новые горизонты в вашем путешествии " +
                                "к пониманию себя и мира вокруг. \uD83C\uDF1F✨");
                        chatGPTExample.search(chatId, messageText);
                    } else {
                        sendMessage(chatId, "Ничего не было выбрано.");
                    }
            }

        }
        else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch(callbackData) {
                case YouTube + "_BUTTON":
                    String text = "Теперь можете ввести любую музыку или запрос для ютуба";
                    sendMessage(chatId, text);
                    isWaitingForUserInputYouTube = true;
                    isWaitingForUserInputChatGPT = false;
                    break;
                case ChatGPT + "_BUTTON":
                    text = "\"Волшебник\" - таинственная и могущественная личность, " +
                            "обладающая удивительными способностями и знаниями в области магии и оккультизма. " +
                            "Его сила и мастерство часто считаются выше человеческого понимания, " +
                            "словно он способен управлять самими силами природы. " +
                            "Общение с волшебником может открывать двери в мир загадок и фантазии, " +
                            "принося в жизнь чудеса и невероятные события. Обратившись к волшебнику, " +
                            "вы можете ожидать удивительных и мистических возможностей, " +
                            "способных изменить ваше представление о реальности и привнести в вашу жизнь волшебство и волшебные моменты. \uD83E\uDDD9✨\uD83C\uDF1F";
                    sendMessage(chatId, text);
                    isWaitingForUserInputYouTube = false;
                    isWaitingForUserInputChatGPT = true;
                    break;
                default:
                    sendMessage(chatId, "Ничего не найдено!");
                    break;
            }
        }
    }

    public void sendMessage(long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void executeMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}