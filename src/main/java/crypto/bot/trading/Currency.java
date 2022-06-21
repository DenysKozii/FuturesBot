package crypto.bot.trading;

import com.binance.client.SubscriptionClient;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.market.Candlestick;

import crypto.bot.data.PriceBean;
import crypto.bot.indicators.Indicator;
import crypto.bot.indicators.RSI;
import crypto.bot.system.ConfigSetup;
import crypto.bot.system.Formatter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Currency {
    public static int    CONFLUENCE_LONG_OPEN;
    public static int    CONFLUENCE_LONG_CLOSE;
    public static int    CONFLUENCE_LONG_WAITING;
    public static int    CONFLUENCE_SHORT_OPEN;
    public static int    CONFLUENCE_SHORT_CLOSE;
    public static int    CONFLUENCE_SHORT_WAITING;
    public static String LOG_PATH = "log.txt";
    public static double TRAILING_SL;
    public static double TAKE_PROFIT;
    public static double SELL_ROE;
    public static double GOAL_ROE;

    private final String          pair;
    private       double          entryPrice;
    private       double          sellPrice;
    private       double          goalPrice;
    private       long            candleTime;
    private final List<Indicator> indicators = new ArrayList<>();

    private double  currentPrice;
    private long    currentTime;
    private int     counter;
    private boolean inLong;
    private boolean inShort;
    private boolean waitingShort;
    private boolean waitingLong;

    static {
        File myFoo = new File(LOG_PATH);
        try {
            new FileWriter(myFoo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Currency(String coin) {
        this.pair = coin + ConfigSetup.getFiat();
        //Every currency needs to contain and update our crypto.bot.indicators
        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, null, null, 1000);
        List<Double> closingPrices = history.stream().map(candle -> candle.getClose().doubleValue()).collect(Collectors.toList());
        indicators.add(new RSI(closingPrices, 10));

        //We set the initial values to check against in onMessage based on the latest candle in history
        currentTime = System.currentTimeMillis();
        candleTime = history.get(history.size() - 1).getCloseTime();
        currentPrice = history.get(history.size() - 1).getClose().doubleValue();

        //We add a websocket listener that automatically updates our values and triggers our strategy or trade logic as needed
        SubscriptionClient.create().subscribeSymbolTickerEvent(pair.toLowerCase(), ((response) -> {
            //Every message and the resulting indicator and strategy calculations is handled concurrently
            double newPrice = response.getLastPrice().doubleValue();
            long newTime = response.getEventTime();
            currentPrice = newPrice;

            if (newTime > candleTime) {
                accept(new PriceBean(candleTime, newPrice, true));
                candleTime += 1000L * 60L * 15L;
                log(this.toString());
            }
            accept(new PriceBean(newTime, newPrice));
        }), null);
        log("---SETUP DONE FOR " + this);
    }

    private void accept(PriceBean bean) {
        currentPrice = bean.getPrice();
        currentTime = bean.getTimestamp();
        if (bean.isClosing()) {
            indicators.forEach(indicator -> indicator.update(bean.getPrice()));
        }
        int confluence = check();
        if (confluence == CONFLUENCE_LONG_WAITING){
            waitingLong = true;
        }
        if (confluence == CONFLUENCE_SHORT_WAITING){
            waitingShort = true;
        }
        if (inLong || inShort) {
            update();
        } else if (confluence == CONFLUENCE_LONG_OPEN) {
            inLong = true;
            waitingLong = false;
            counter = 0;
            updatePrices();
            log("LONG for: " + confluence + " | " + this);
            BuySell.open(Currency.this, false);
        } else if (confluence == CONFLUENCE_SHORT_OPEN) {
            inShort = true;
            waitingShort = false;
            counter = 0;
            updatePrices();
            log("SHORT for: " + confluence + " | " + this);
            BuySell.open(Currency.this, true);
        }
    }

    private void updatePrices() {
        entryPrice = currentPrice;
        counter++;
        double sellRoe = SELL_ROE + counter * 0;
        double goalRoe = GOAL_ROE + counter * 0;
        sellPrice = inShort ? entryPrice * (1 + sellRoe) : entryPrice * (1 - sellRoe);
        goalPrice = inShort ? entryPrice * (1 - goalRoe) : entryPrice * (1 + goalRoe);
    }

    private void update(){
        if (inLong) {
            if (currentPrice >= goalPrice){
                updatePrices();
                log(this + " change prices: entryPrice = " + entryPrice + ", sellPrice = " + sellPrice + ", goalPrice = " + goalPrice);
            } else if (currentPrice <= sellPrice){
                inLong = false;
                waitingLong = false;
                log(this + " close");
                BuySell.close(this, false);
            }
        } else if (inShort) {
            if (currentPrice <= goalPrice){
                updatePrices();
                log(this + " change prices: entryPrice = " + entryPrice + ", sellPrice = " + sellPrice + ", goalPrice = " + goalPrice);
            } else if (currentPrice >= sellPrice){
                inShort = false;
                waitingShort = false;
                log(this + " close");
                BuySell.close(this, true);
            }
        }
    }

    public void log(String log) {
        System.out.println(log);
//        try {
//            String content = read();
//            File myFoo = new File(LOG_PATH);
//            FileWriter fooStream = new FileWriter(myFoo);
//            fooStream.write(content);
//            fooStream.write("\n" + log);
//            fooStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static String read() throws IOException {
        Path path = Paths.get(LOG_PATH);
        Stream<String> lines = Files.lines(path);
        String data = lines.collect(Collectors.joining("\n"));
        String[] strings = data.split("\nBALANCE:");
        lines.close();
        return strings[0];
    }

    public boolean isWaitingShort() {
        return waitingShort;
    }

    public void setWaitingShort(boolean waitingShort) {
        this.waitingShort = waitingShort;
    }

    public boolean isWaitingLong() {
        return waitingLong;
    }

    public void setWaitingLong(boolean waitingLong) {
        this.waitingLong = waitingLong;
    }

    public int check() {
        return indicators.stream().mapToInt(indicator -> indicator.check(currentPrice, this)).sum();
    }

    public boolean isInShort() {
        return inShort;
    }

    public void setInShort(boolean inShort) {
        this.inShort = inShort;
    }

    public boolean isInLong() {
        return inLong;
    }

    public void setInLong(boolean inLong) {
        this.inLong = inLong;
    }

    public String getPair() {
        return pair;
    }

    public double getPrice() {
        return currentPrice;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(pair + " price: " + currentPrice);
        if (currentTime == candleTime)
            indicators.forEach(indicator -> s.append(", ").append(indicator.getClass().getSimpleName()).append(": ").append(Formatter.formatDecimal(indicator.get())));
        else
            indicators.forEach(indicator -> s.append(", ").append(indicator.getClass().getSimpleName()).append(": ").append(Formatter.formatDecimal(indicator.getTemp(currentPrice))));
        s.append(", waiting long: ").append(waitingLong)
         .append(", waiting short: ").append(waitingShort)
         .append(", in long: ").append(inLong)
         .append(", in short: ").append(inShort).append(")");
        return s.toString();
    }

    @Override
    public int hashCode() {
        return pair.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != Currency.class) return false;
        return pair.equals(((Currency) obj).pair);
    }

    public double getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(double entryPrice) {
        this.entryPrice = entryPrice;
    }

    public double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
}
