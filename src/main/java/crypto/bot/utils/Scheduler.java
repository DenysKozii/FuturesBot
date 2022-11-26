package crypto.bot.utils;

import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
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
    @Scheduled(cron = "1 47 17 * * *", zone = "GMT+0")
    public void read() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("https://www.binance.com/fapi/v1/premiumIndex");
            CloseableHttpResponse closeableHttpResponse = client.execute(request);
            System.out.println("--read--");
            System.out.println(closeableHttpResponse.toString());
            System.out.println(closeableHttpResponse.getEntity().toString());
            System.out.println(closeableHttpResponse.getEntity().getContent());
        }
    }

    @SneakyThrows
    @Scheduled(cron = "57 59 0 * * *", zone = "GMT+0")
    public void open1() {
        System.out.println("open1");
    }

    @SneakyThrows
    @Scheduled(cron = "1 0 1 * * *", zone = "GMT+0")
    public void close1() {
        System.out.println("close1");
    }

    @SneakyThrows
    @Scheduled(cron = "57 59 8 * * *", zone = "GMT+0")
    public void open8() {
        System.out.println("open8");
    }

    @SneakyThrows
    @Scheduled(cron = "1 0 9 * * *", zone = "GMT+0")
    public void close8() {
        System.out.println("close8");
    }

    @SneakyThrows
    @Scheduled(cron = "57 59 16 * * *", zone = "GMT+0")
    public void open16() {
        System.out.println("open16");
    }

    @SneakyThrows
    @Scheduled(cron = "1 0 17 * * *", zone = "GMT+0")
    public void close16() {
        System.out.println("close16");
    }

}
