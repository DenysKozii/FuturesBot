package crypto.bot.trading;

import com.binance.client.SubscriptionClient;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.market.Candlestick;
import crypto.bot.data.PriceBean;
import crypto.bot.indicators.Indicator;
import crypto.bot.indicators.RSI;
import crypto.bot.system.ConfigSetup;
import crypto.bot.system.Formatter;

import java.util.*;
import java.util.stream.Collectors;

public class Currency {
    public int CONFLUENCE_LONG_OPEN = 1;
    public int CONFLUENCE_SHORT_OPEN = 2;
    public int CONFLUENCE_SHORT_UNLOCK = 3;
    public int CONFLUENCE_LONG_UNLOCK = 4;
    public double SELL_ROE;
    public double PROFIT_ROE;
    public double GOAL_ROE = 0.0001;

    private final String pair;
    private double money = 1000;
    private double entryPrice;
    private double sellPrice;
    private double takePrice;
    private double goalPrice;
    private long candleTime;
    private final List<Indicator> indicators = new ArrayList<>();

    private Date currentDate;
    private double currentPrice;
    private long currentTime;
    private int counter;
    private boolean inLong;
    private boolean inShort;
    private boolean inLongWaiting;
    private boolean inShortWaiting;
    private int longOpenRSI;
    private int shortOpenRSI;
    private Map<Integer, Integer> rsiStatistics = new HashMap<>();


    public Currency(String coin, double money, int longOpenRSI, int shortOpenRSI, double SELL_ROE, double PROFIT_ROE, double sellPrice, List<Candlestick> history) {
        this.pair = coin + ConfigSetup.getFiat();
        this.longOpenRSI = longOpenRSI;
        this.shortOpenRSI = shortOpenRSI;
        this.SELL_ROE = SELL_ROE;
        this.PROFIT_ROE = PROFIT_ROE;
        this.money = money;
        this.sellPrice = sellPrice;
        //Every currency needs to contain and update our crypto.bot.indicators
//        history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1654030800000L, null, 1500);
        // 1000
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1651352400000L, null, 1500);
        // 1016
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1656622800000L, null, 1500);
        // 999
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1648760400000L, null, 1500);
        // 1000
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1652060400000L, null, 1500);
        // 1000
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1632060400000L, null, 1500);
        // 965
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1644060400000L, null, 1500);
        // 1000
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1640060400000L, null, 1500);
        // 1000


        // backtest 15
//        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1646085600000L, null, 960);
//        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1646863200000L, null, 960));
//        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1647727200000L, null, 960));
//        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1648587600000L, null, 960));
//        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1646776800000L, null, 960));
//        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1647640800000L, null, 960));
//        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, 1648501200000L, null, 960));


        // backtest 1
        List<Double> closingPrices = history.stream().map(candle -> candle.getClose().doubleValue()).collect(Collectors.toList());
        indicators.add(new RSI(closingPrices.subList(0, 100), 11));
        for (int i = 100; i < history.size(); i++) {
            double newPrice = history.get(i).getClose().doubleValue();
            accept(new PriceBean(history.get(i).getCloseTime(), newPrice, true));
//            int rsi = (int) indicators.get(0).getTemp(currentPrice);
//            rsiStatistics.put(rsi, rsiStatistics.getOrDefault(rsi, 0) + 1);
            currentDate = new Date(history.get(i).getCloseTime());
//            System.out.println(currentDate + " : " + getProfit() + " | " + rsi);
        }
        //We set the initial values to check against in onMessage based on the latest candle in history
//        currentTime = System.currentTimeMillis();
//        candleTime = history.get(history.size() - 1).getCloseTime();
//        currentPrice = history.get(history.size() - 1).getClose().doubleValue();

        //We add a websocket listener that automatically updates our values and triggers our strategy or trade logic as needed
//        SubscriptionClient.create().subscribeSymbolTickerEvent(pair.toLowerCase(), ((response) -> {
//            //Every message and the resulting indicator and strategy calculations is handled concurrently
//            double newPrice = response.getLastPrice().doubleValue();
//            long newTime = response.getEventTime();
//            currentPrice = newPrice;
//
//            if (newTime > candleTime) {
//                accept(new PriceBean(candleTime, newPrice, true));
//                candleTime += 1000L * 60L * 15L;
//                log(this.toString());
//            }
//            accept(new PriceBean(newTime, newPrice));
//        }), null);
        log("---SETUP DONE FOR " + this);
    }

    private void accept(PriceBean bean) {
        currentPrice = bean.getPrice();
        currentTime = bean.getTimestamp();
        if (bean.isClosing()) {
            indicators.forEach(indicator -> indicator.update(bean.getPrice()));
        }
        int confluence = check();
//        if (confluence == CONFLUENCE_SHORT_UNLOCK) {
//            inShortWaiting = true;
//            return;
//        }
//        if (confluence == CONFLUENCE_LONG_UNLOCK) {
//            inLongWaiting = true;
//            return;
//        }
//        if (inShortWaiting || inLongWaiting) {
//            return;
//        }
        if (inLong || inShort) {
            update();
        } else if (confluence == CONFLUENCE_LONG_OPEN) {
            inLong = true;
            inLongWaiting = false;
            counter = 0;
            entryPrice = currentPrice;
            takePrice = currentPrice * (1 + PROFIT_ROE);
            updatePrices();
            log("LONG for: " + confluence + " | " + this);
            BuySell.open(Currency.this, true);
        } else if (confluence == CONFLUENCE_SHORT_OPEN) {
            inShort = true;
            inShortWaiting = false;
            counter = 0;
            entryPrice = currentPrice;
            takePrice = currentPrice * (1 - PROFIT_ROE);
            updatePrices();
            log("SHORT for: " + confluence + " | " + this);
            BuySell.open(Currency.this, false);
        }
    }

    private void updatePrices() {
        counter++;
        double sellRoe = SELL_ROE + counter * 0;
        double goalRoe = GOAL_ROE + counter * 0;
        sellPrice = inShort ? currentPrice * (1 + sellRoe) : currentPrice * (1 - sellRoe);
        goalPrice = inShort ? currentPrice * (1 - goalRoe) : currentPrice * (1 + goalRoe);
    }

    private void update() {
        if (inLong) {
            if (currentPrice >= goalPrice) {
                updatePrices();
            }
            else if (currentPrice <= sellPrice) {
                inLong = false;
                BuySell.close(this, true);
                log(this.toString());
//                inLongWaiting = indicators.get(0).getTemp(currentPrice) < longOpenRSI;
            }
            if (currentPrice >= takePrice) {
                inLong = false;
                BuySell.close(this, true);
                log(this.toString());
//                inLongWaiting = indicators.get(0).getTemp(currentPrice) < longOpenRSI;
            }
        } else if (inShort) {
            if (currentPrice <= goalPrice) {
                updatePrices();
            }
            else if (currentPrice >= sellPrice) {
                inShort = false;
                BuySell.close(this, false);
                log(this.toString());
//                inShortWaiting = indicators.get(0).getTemp(currentPrice) > shortOpenRSI;
            }
            if (currentPrice <= takePrice) {
                inShort = false;
                BuySell.close(this, false);
                log(this.toString());
//                inShortWaiting = indicators.get(0).getTemp(currentPrice) > shortOpenRSI;
            }
        }
    }

    public double getProfit() {
        double profit = money;
        if (inLong) {
            profit = currentPrice / entryPrice;
            profit = money * profit * 0.9993;
        }
        if (inShort) {
            profit = entryPrice / currentPrice;
            profit = money * profit * 0.9993;
        }
        return profit;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date currentDate) {
        this.currentDate = currentDate;
    }

    public boolean isInLongWaiting() {
        return inLongWaiting;
    }

    public void setInLongWaiting(boolean inLongWaiting) {
        this.inLongWaiting = inLongWaiting;
    }

    public boolean isInShortWaiting() {
        return inShortWaiting;
    }

    public void setInShortWaiting(boolean inShortWaiting) {
        this.inShortWaiting = inShortWaiting;
    }

    public void log(String log) {
        System.out.println(log);
    }


    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getSELL_ROE() {
        return SELL_ROE;
    }

    public Currency setSELL_ROE(double SELL_ROE) {
        this.SELL_ROE = SELL_ROE;
        return this;
    }

    public double getGOAL_ROE() {
        return GOAL_ROE;
    }

    public Currency setGOAL_ROE(double GOAL_ROE) {
        this.GOAL_ROE = GOAL_ROE;
        return this;
    }

    public int getLongOpenRSI() {
        return longOpenRSI;
    }

    public Currency setLongOpenRSI(int longOpenRSI) {
        this.longOpenRSI = longOpenRSI;
        return this;
    }

    public int getShortOpenRSI() {
        return shortOpenRSI;
    }

    public Currency setShortOpenRSI(int shortOpenRSI) {
        this.shortOpenRSI = shortOpenRSI;
        return this;
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
        StringBuilder s = new StringBuilder(currentDate + ": " + getProfit() + ", " + pair + " price: " + currentPrice);
        if (currentTime == candleTime)
            indicators.forEach(indicator -> s.append(", ").append(indicator.getClass().getSimpleName()).append(": ").append(Formatter.formatDecimal(indicator.get())));
        else
            indicators.forEach(indicator -> s.append(", ").append(indicator.getClass().getSimpleName()).append(": ").append(Formatter.formatDecimal(indicator.getTemp(currentPrice))));
        s.append(", in long: ").append(inLong)
                .append(", in short: ").append(inShort)
                .append(", RSI long: ").append(longOpenRSI)
                .append(", RSI short: ").append(shortOpenRSI)
                .append(", delta stop: ").append(SELL_ROE)
                .append(", delta profit: ").append(PROFIT_ROE);
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
