package crypto.bot.utils;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    private final static String URL = "https://bot-futures-prod.herokuapp.com/api";

    @Scheduled(fixedRate = 1000 * 60 * 4)
    public void timer() throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(URL);
            client.execute(request);
        }
    }

    @Scheduled(cron = "58 59 */8 * * *", zone = "GMT-1")
    public void open() throws IOException {
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
    }

    @Scheduled(cron = "1 0 */8 * * *", zone = "GMT+0")
    public void close() throws IOException {
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
    }
}
