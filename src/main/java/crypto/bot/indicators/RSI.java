package crypto.bot.indicators;

import crypto.bot.trading.Currency;

import java.util.List;

public class RSI implements Indicator {

    private double avgUp;
    private double avgDwn;
    private double prevClose;
    private final int period;

    public RSI(List<Double> closingPrice, int period) {
        avgUp = 0;
        avgDwn = 0;
        this.period = period;
        init(closingPrice);
    }

    @Override
    public void init(List<Double> closingPrices) {
        prevClose = closingPrices.get(0);
        for (int i = 1; i < period + 1; i++) {
            double change = closingPrices.get(i) - prevClose;
            if (change > 0) {
                avgUp += change;
            } else {
                avgDwn += Math.abs(change);
            }
        }

        //Initial SMA values
        avgUp = avgUp / (double) period;
        avgDwn = avgDwn / (double) period;

        //Dont use latest unclosed value
        for (int i = period + 1; i < closingPrices.size() - 1; i++) {
            update(closingPrices.get(i));
        }
    }

    @Override
    public double get() {
        return 100 - 100.0 / (1 + avgUp / avgDwn);
    }

    @Override
    public double getTemp(double newPrice) {
        double change = newPrice - prevClose;
        double tempUp;
        double tempDwn;
        if (change > 0) {
            tempUp = (avgUp * (period - 1) + change) / (double) period;
            tempDwn = (avgDwn * (period - 1)) / (double) period;
        } else {
            tempDwn = (avgDwn * (period - 1) + Math.abs(change)) / (double) period;
            tempUp = (avgUp * (period - 1)) / (double) period;
        }
        return 100 - 100.0 / (1 + tempUp / tempDwn);
    }

    @Override
    public void update(double newPrice) {
        double change = newPrice - prevClose;
        if (change > 0) {
            avgUp = (avgUp * (period - 1) + change) / (double) period;
            avgDwn = (avgDwn * (period - 1)) / (double) period;
        } else {
            avgUp = (avgUp * (period - 1)) / (double) period;
            avgDwn = (avgDwn * (period - 1) + Math.abs(change)) / (double) period;
        }
        prevClose = newPrice;
    }

    @Override
    public int check(double newPrice, Currency currency) {
        double temp = getTemp(newPrice);
        if (!currency.isInShort() && !currency.isInLong() && !currency.isInShortWaiting() && !currency.isInLongWaiting() && temp < currency.getLongOpenRSI()) {
            return currency.CONFLUENCE_LONG_OPEN;
        }
        if (!currency.isInShort() && !currency.isInLong() && !currency.isInShortWaiting() && !currency.isInLongWaiting() && temp > currency.getShortOpenRSI()) {
            return currency.CONFLUENCE_SHORT_OPEN;
        }
        if (currency.isInShortWaiting() && temp <= currency.getShortOpenRSI()) {
            return currency.CONFLUENCE_UNLOCK;
        }
        if (currency.isInLongWaiting() && temp >= currency.getLongOpenRSI()) {
            return currency.CONFLUENCE_UNLOCK;
        }
        return 0;
    }

}
