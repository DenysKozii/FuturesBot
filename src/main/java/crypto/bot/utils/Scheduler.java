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
    @Scheduled(cron = "58 44 */1 * * *", zone = "GMT-1")
    public void open() {
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
        System.out.println("Open");
    }

    @SneakyThrows
    @Scheduled(cron = "1 45 */1 * * *", zone = "GMT+0")
    public void close() {
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        System.out.println("Date and time in Madrid: " + df.format(date));
        df.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        System.out.println("Date and time in Madrid: " + df.format(date));
        df.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        System.out.println("Date and time in Madrid: " + df.format(date));

        df.setTimeZone(TimeZone.getTimeZone("GMT-1"));
        System.out.println("Date and time in Madrid: " + df.format(date));
        df.setTimeZone(TimeZone.getTimeZone("GMT-2"));
        System.out.println("Date and time in Madrid: " + df.format(date));
        df.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        System.out.println("Date and time in Madrid: " + df.format(date));

        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
        System.out.println("close");
    }
}
