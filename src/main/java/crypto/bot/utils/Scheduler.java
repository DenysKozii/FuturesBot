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
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Component
@Slf4j
@RequiredArgsConstructor
@EnableScheduling
public class Scheduler {

    private final static String URL = "https://cryptodenysserverjs-production.up.railway.app/data";
    private static String SYMBOL;
    private static SyncRequestClient clientFutures;
    private static boolean inLong = false;

    private static double nextQuantity() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        double quantity = accountInformation.getAvailableBalance().doubleValue() - 5;
        return Math.max(quantity, 0);
    }

    public void closePosition() throws IOException {
        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(SYMBOL)).findFirst();

        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() == 0) {
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
        Live.init();
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
            try {
                clientFutures.changeMarginType(SYMBOL, MarginType.ISOLATED);
            } catch (Exception ignored) {
            }
            clientFutures.changeInitialLeverage(SYMBOL, 2);
            double amount = nextQuantity();
            amount /= Double.parseDouble(price);
            amount *= 2;
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
        openPosition();
    }

    @SneakyThrows
    @Scheduled(cron = "1 0 17 * * *", zone = "GMT+0")
    public void close16() {
        closePosition();
    }

}
