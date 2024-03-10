package bot.command;

import bot.service.MyTelegramBot;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

@Configuration
public class ChatGPTApiClient {

    private final MyTelegramBot telegramBot;

    public ChatGPTApiClient(@Lazy MyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void search(long chatId, String messageText) {
        String url = "https://api.openai.com/v1/chat/completions";
        String api = telegramBot.getChatGPTApi();
        String model = "gpt-3.5-turbo";

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + api);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + messageText + "\"}]}";
            con.setDoOutput(true) ;
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the respon:
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            String responseString = response.toString();
            String[] parts = responseString.split("\"content\": \"");
            if (parts.length > 1) {
                String content = parts[1]; // Здесь содержится часть строки после "content": "
                content = content.substring(0, content.indexOf("\"")); // Получение части строки до следующей кавычки "

                telegramBot.sendMessage(chatId, content);
            } else {
                // Обработка случая, если не найдено соответствие для разделения по шаблону
                telegramBot.sendMessage(chatId, "Ответ не может быть найден. Так как запрос был некоректный!");
            }

        } catch (IOException e) {
            e.printStackTrace();
            telegramBot.sendMessage(chatId, "Кажется, волшебник намеренно молчит, " +
                    "поглощенный мыслями и загадками, " +
                    "не спеша обращать внимание на внешний мир. :thinking_face: :crystal_ball: :mantelpiece_clock: Пока он погружен в свои размышления, " +
                    "ответ остается в тени тайны, окутанный мантией загадочности. " +
                    "Попробуй задать свой вопрос позже! \uD83C\uDF0C\uD83E\uDDD9\u200D♂\uFE0F\uD83D\uDD12\uD83D\uDD2E");
        }
    }
}
