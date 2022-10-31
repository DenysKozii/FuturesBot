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
    @Scheduled(cron = "1 3 * * * *", zone = "GMT+0")
    public void read() {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet("https://www.binance.com/fapi/v1/premiumIndex");
            CloseableHttpResponse closeableHttpResponse = client.execute(request);
            System.out.println("--read--");
            System.out.println(closeableHttpResponse.toString());
            System.out.println(closeableHttpResponse.toString());
            System.out.println(closeableHttpResponse.toString());
            System.out.println(closeableHttpResponse.toString());
        }
    }

    @SneakyThrows
    @Scheduled(cron = "1 0 */8 * * *", zone = "GMT+0")
    public void close() {
        System.out.println("close");
    }

    @SneakyThrows
    @Scheduled(cron = "57 59 23 * * *", zone = "GMT+0")
    public void open0() {
        System.out.println("open0");
    }

    @SneakyThrows
    @Scheduled(cron = "57 59 7 * * *", zone = "GMT+0")
    public void open8() {
        System.out.println("open8");
    }

    @SneakyThrows
    @Scheduled(cron = "57 59 15 * * *", zone = "GMT+0")
    public void open16() {
        System.out.println("open16");
    }
}
