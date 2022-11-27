package crypto.bot.utils;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    private final static String URL = "https://cryptodenysserverjs-production.up.railway.app/data";

    @Scheduled(fixedRate = 1000 * 60 * 4)
    public void timer() throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(URL);
            HttpResponse response = client.execute(request);
            var bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = bufReader.readLine()) != null) {
                System.out.println(line);
                log.info(line);
            }
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
