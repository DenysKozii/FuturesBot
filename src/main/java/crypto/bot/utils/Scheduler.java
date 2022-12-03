package crypto.bot.utils;

import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;
import crypto.bot.modes.Live;
import crypto.bot.trading.CurrentAPI;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    private final static String URL = "https://cryptodenysserverjs-production.up.railway.app/data";
    private final static String HEROKU = "https://funding.herokuapp.com/api/v1";
    private static String SYMBOL;
    private static SyncRequestClient clientFutures;
    private static boolean inLong = false;
    private static boolean closed = false;

    @Scheduled(fixedRate = 1000 * 60 * 4)
    public void timer() throws IOException {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(HEROKU);
            client.execute(request);
        }
    }

    private static double nextQuantity() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        double quantity = accountInformation.getAvailableBalance().doubleValue();
        return Math.max(quantity * 0.9, 0);
    }

    public void closePosition() {
        if (closed){
            log.info("Buffer close for {} is ignored", SYMBOL);
            return;
        }
        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(SYMBOL)).findFirst();

        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() == 0) {
            log.info("Position {} is closed", SYMBOL);
            closed = true;
            return;
        }

        String positionAmount = position.get().getPositionAmt().toString();
        List<Order> openOrders = clientFutures.getOpenOrders(SYMBOL);
        if (inLong){
            if (openOrders.isEmpty()) {
                Order order = clientFutures.postOrder(
                        SYMBOL, OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
            }
        } else {
            if (openOrders.isEmpty()) {
                positionAmount = String.valueOf(-1 * Double.parseDouble(positionAmount));
                Order order = clientFutures.postOrder(
                        SYMBOL, OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
            }
        }

    }

    public void openPosition() throws IOException {
        clientFutures = CurrentAPI.getClient();
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(URL);
            HttpResponse response = client.execute(request);
            var bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            SYMBOL = "BTCUSDT";
            String rate = "0.001";
            String price = "0.001";
            while ((line = bufReader.readLine()) != null) {
                SYMBOL = line.split(";")[0];
                rate = line.split(";")[1];
                price = line.split(";")[2];
                log.info(SYMBOL);
                log.info(rate);
            }
            if (Math.abs(Double.parseDouble(rate)) < 0.001){
                log.info("rate is lower than limit {}", 0.001);
                return;
            }
            try {
                clientFutures.changeMarginType(SYMBOL, MarginType.ISOLATED);
            } catch (Exception ignored) {
            }
            clientFutures.changeInitialLeverage(SYMBOL, 2);
            double amount = nextQuantity();
            amount *= 2;
            amount /= Double.parseDouble(price);
            String positionAmount = String.valueOf((int) amount);
            log.info("positionAmount = {}", positionAmount);
            if (Double.parseDouble(rate) < 0) {
                inLong = true;
                Order order = clientFutures.postOrder(
                        SYMBOL, OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
            } else {
                inLong = false;
                Order order = clientFutures.postOrder(
                        SYMBOL, OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
            }
            closed = false;
        }
    }

    @SneakyThrows
    @EventListener(ApplicationReadyEvent.class)
    public void getFunding(){
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(URL);
            HttpResponse response = client.execute(request);
            var bufReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            SYMBOL = "BTCUSDT";
            String rate = "0.001";
            String price = "0.001";
            while ((line = bufReader.readLine()) != null) {
                SYMBOL = line.split(";")[0];
                rate = line.split(";")[1];
                price = line.split(";")[2];
                log.info("symbol = {}", SYMBOL);
                log.info("rate = {}", rate);
                log.info("price = {}", price);
            }
        }
    }

    @Scheduled(cron = "0 55 23 * * *", zone = "GMT+0")
    public void isAlive1() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        log.info("Available balance check = {}", accountInformation.getAvailableBalance());
    }

    @SneakyThrows
    @Scheduled(cron = "58 59 23 * * *", zone = "GMT+0")
    public void open1() {
        log.info("open started");
        openPosition();
        log.info("open finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 0 * * *", zone = "GMT+0")
    public void close1() {
        log.info("close started");
        closePosition();
        log.info("close finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "5 0 0 * * *", zone = "GMT+0")
    public void close11() {
        log.info("buffer close 1 started");
        closePosition();
        log.info("buffer close 1 finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "7 0 0 * * *", zone = "GMT+0")
    public void close12() {
        log.info("buffer close 2 started");
        closePosition();
        log.info("buffer close 2 finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "9 0 0 * * *", zone = "GMT+0")
    public void close13() {
        log.info("buffer close 3 started");
        closePosition();
        log.info("buffer close 3 finished successfully");
    }

    @Scheduled(cron = "0 55 7 * * *", zone = "GMT+0")
    public void isAlive8() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        log.info("Available balance check = {}", accountInformation.getAvailableBalance());
    }

    @SneakyThrows
    @Scheduled(cron = "58 59 7 * * *", zone = "GMT+0")
    public void open8() {
        log.info("open started");
        openPosition();
        log.info("open finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 8 * * *", zone = "GMT+0")
    public void close8() {
        log.info("close started");
        closePosition();
        log.info("close finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "5 0 8 * * *", zone = "GMT+0")
    public void close81() {
        log.info("buffer close 1 started");
        closePosition();
        log.info("buffer close 1 finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "7 0 8 * * *", zone = "GMT+0")
    public void close82() {
        log.info("buffer close 2 started");
        closePosition();
        log.info("buffer close 2 finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "9 0 8 * * *", zone = "GMT+0")
    public void close83() {
        log.info("buffer close 3 started");
        closePosition();
        log.info("buffer close 3 finished successfully");
    }

    @Scheduled(cron = "0 55 15 * * *", zone = "GMT+0")
    public void isAlive16() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        log.info("Available balance check = {}", accountInformation.getAvailableBalance());
    }

    @SneakyThrows
    @Scheduled(cron = "58 59 15 * * *", zone = "GMT+0")
    public void open16() {
        log.info("open started");
        openPosition();
        log.info("open finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "0 0 16 * * *", zone = "GMT+0")
    public void close16() {
        log.info("close started");
        closePosition();
        log.info("close finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "5 0 8 * * *", zone = "GMT+0")
    public void close161() {
        log.info("buffer close 1 started");
        closePosition();
        log.info("buffer close 1 finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "7 0 8 * * *", zone = "GMT+0")
    public void close162() {
        log.info("buffer close 2 started");
        closePosition();
        log.info("buffer close 2 finished successfully");
    }

    @SneakyThrows
    @Scheduled(cron = "9 0 8 * * *", zone = "GMT+0")
    public void close163() {
        log.info("buffer close 3 started");
        closePosition();
        log.info("buffer close 3 finished successfully");
    }
}
