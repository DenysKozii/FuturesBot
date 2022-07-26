package crypto.bot.trading;

import com.binance.client.SubscriptionClient;
import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.market.Candlestick;
import crypto.bot.data.PriceBean;
import crypto.bot.enums.Strategy;
import crypto.bot.indicators.Indicator;
import crypto.bot.indicators.RSI;
import crypto.bot.system.ConfigSetup;
import crypto.bot.system.Formatter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Currency {
    public int CONFLUENCE_LONG_OPEN = 1;
    public int CONFLUENCE_SHORT_OPEN = 2;
    public double SELL_ROE;
    public double GOAL_ROE = 0.0001;

    private final String pair;
    private double money;
    private double entryPrice;
    private double sellPrice;
    private double goalPrice;
    private long candleTime;
    private final List<Indicator> indicators = new ArrayList<>();

    private double currentPrice;
    private long currentTime;
    private int counter;
    private boolean inLong;
    private boolean inShort;
    private int longOpenRSI;
    private int shortOpenRSI;
    private Strategy strategy;

    public Currency(String coin, double money, int longOpenRSI, int shortOpenRSI, double SELL_ROE, Strategy strategy) {
        this.pair = coin + ConfigSetup.getFiat();
        this.longOpenRSI = longOpenRSI;
        this.shortOpenRSI = shortOpenRSI;
        this.SELL_ROE = SELL_ROE;
        this.money = money;
        this.strategy = strategy;
        //Every currency needs to contain and update our crypto.bot.indicators
        List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.FIFTEEN_MINUTES, null, null, 1000);
        List<Double> closingPrices = history.stream().map(candle -> candle.getClose().doubleValue()).collect(Collectors.toList());
        indicators.add(new RSI(closingPrices, 14));

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
        if (inLong || inShort) {
            update();
        } else if (confluence == CONFLUENCE_LONG_OPEN) {
            inLong = true;
            counter = 0;
            entryPrice = currentPrice;
            updatePrices();
            log("LONG for: " + confluence + " | " + this);
            BuySell.open(Currency.this, true);
        } else if (confluence == CONFLUENCE_SHORT_OPEN) {
            inShort = true;
            counter = 0;
            entryPrice = currentPrice;
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
        if (Strategy.ROE.equals(strategy)) {
            if (inLong) {
                if (currentPrice >= goalPrice) {
                    updatePrices();
                } else if (currentPrice <= sellPrice) {
                    inLong = false;
                    log(this + " close");
                    BuySell.close(this, true);
                }
            } else if (inShort) {
                if (currentPrice <= goalPrice) {
                    updatePrices();
                } else if (currentPrice >= sellPrice) {
                    inShort = false;
                    log(this + " close");
                    BuySell.close(this, false);
                }
            }
        } else {
            if (inLong) {
                if (indicators.get(0).getTemp(currentPrice) > 40) {
                    inLong = false;
                    log(this + " close");
                    BuySell.close(this, true);
                }
            } else if (inShort) {
                if (indicators.get(0).getTemp(currentPrice) < 60) {
                    inShort = false;
                    log(this + " close");
                    BuySell.close(this, false);
                }
            }
        }
    }

    public double getProfit(){
        double profit = money;
        if (inLong) {
            profit = currentPrice / entryPrice;
        }
        if (inShort) {
            profit = entryPrice / currentPrice;
        }
        return money * profit * 0.9985;
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
        StringBuilder s = new StringBuilder(getProfit() + ", " + pair + " price: " + currentPrice);
        if (currentTime == candleTime)
            indicators.forEach(indicator -> s.append(", ").append(indicator.getClass().getSimpleName()).append(": ").append(Formatter.formatDecimal(indicator.get())));
        else
            indicators.forEach(indicator -> s.append(", ").append(indicator.getClass().getSimpleName()).append(": ").append(Formatter.formatDecimal(indicator.getTemp(currentPrice))));
        s.append(", in long: ").append(inLong)
                .append(", in short: ").append(inShort)
                .append(", RSI long: ").append(longOpenRSI)
                .append(", RSI short: ").append(shortOpenRSI)
                .append(", delta stop: ").append(SELL_ROE);
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
