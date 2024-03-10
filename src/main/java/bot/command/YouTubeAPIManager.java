package bot.command;

import bot.service.MyTelegramBot;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class YouTubeAPIManager {

    private MyTelegramBot telegramBot;

    public YouTubeAPIManager(@Lazy MyTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void search(long chatId, String messageText) throws IOException {
        String apiKey = telegramBot.getYouTubeApi();
        telegramBot.sendMessage(chatId, "Выполняется поиск по запросу: " + messageText);
        String searchQuery = messageText; // Поисковой запрос
        String encodedSearchQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8.toString());
        String apiUrl = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=5&q=" + encodedSearchQuery + "&key=" + apiKey;

        System.out.println(apiUrl);
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrl);

        HttpResponse response = httpClient.execute(httpGet);
        String json = EntityUtils.toString(response.getEntity());

        JSONObject jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");

        String mostPopularVideoId = "";
        int maxPopularity = Integer.MIN_VALUE;
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            JSONObject id = item.getJSONObject("id");
            if (id.has("videoId")) {
                String videoId = id.getString("videoId");
                String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

                int videoPopularity = getVideoPopularity(videoId, apiKey); // Функция для получения популярности видео

                if (videoPopularity > maxPopularity) {
                    mostPopularVideoId = videoId;
                    maxPopularity = videoPopularity;

                    telegramBot.sendMessage(chatId, "Всё что удалось найти по твоему запросу: " + videoUrl + maxPopularity);

                }
            }
        }

    }
    private static int getVideoPopularity(String videoId, String apiKey) throws IOException, JSONException {
        String apiUrlPopular = "https://www.googleapis.com/youtube/v3/videos?part=statistics&id=" + videoId + "&key=" + apiKey;

        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(apiUrlPopular);

        HttpResponse response = httpClient.execute(httpGet);
        String json = EntityUtils.toString(response.getEntity());

        JSONObject jsonObject = new JSONObject(json);
        JSONArray items = jsonObject.getJSONArray("items");

        if (items.length() > 0) {
            JSONObject videoInfo = items.getJSONObject(0);
            JSONObject statistics = videoInfo.getJSONObject("statistics");

            if (statistics.has("viewCount")) {
                String viewCount = statistics.getString("viewCount");
                return Integer.parseInt(viewCount);
            }
        }

        return 0;  // В случае ошибки или отсутствия информации возвращаем 0
    }
}