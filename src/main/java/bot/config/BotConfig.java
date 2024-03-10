package bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {

    //TelegramBot
    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String token;

    //YouTube
    @Value("${youtube.api}")
    String youtubeKey;

    //ChatGPT
    @Value("${chatGPT.api}")
    String chatGPTKey;

}