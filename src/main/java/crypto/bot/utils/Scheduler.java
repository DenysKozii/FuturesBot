package crypto.bot.utils;

import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

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

    @SneakyThrows
    @Scheduled(cron = "58 53 */9 * * *", zone = "GMT+0")
    public void open() {
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
    }


    @SneakyThrows
    @Scheduled(cron = "1 0 0 * * *", zone = "GMT+0")
    public void close0() {
        System.out.println("close0");
        System.out.println("close0");
        System.out.println("close0");
        System.out.println("close0");
        System.out.println("close0");
    }

    @SneakyThrows
    @Scheduled(cron = "1 54 9 * * *", zone = "GMT+0")
    public void close8() {
        System.out.println("close8");
        System.out.println("close8");
        System.out.println("close8");
        System.out.println("close8");
        System.out.println("close8");
    }

    @SneakyThrows
    @Scheduled(cron = "1 0 16 * * *", zone = "GMT+0")
    public void close16() {
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
    }
}
