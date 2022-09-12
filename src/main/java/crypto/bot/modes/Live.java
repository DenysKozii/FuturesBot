package crypto.bot.modes;

import com.binance.client.model.enums.CandlestickInterval;
import com.binance.client.model.market.Candlestick;
import crypto.bot.entity.Credentials;
import crypto.bot.entity.Trade;
import crypto.bot.enums.Strategy;
import crypto.bot.repository.CredentialsRepository;
import crypto.bot.repository.TradeRepository;
import crypto.bot.system.ConfigSetup;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.Currency;
import crypto.bot.trading.CurrentAPI;
import crypto.bot.trading.LocalAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class Live {
    public static String apiKey;
    public static String apiSecret;
    private final TradeRepository tradeRepository;
    private final CredentialsRepository credentialsRepository;
    public static String CURRENCY;


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        System.out.println(tradeRepository.count());
        try {
            Optional<Credentials> credentials = credentialsRepository.findById(1L);
            if (credentials.isPresent()) {
                apiKey = credentials.get().getKey();
                apiSecret = credentials.get().getSecret();
            }
            while (apiKey == null && apiSecret == null) {
                System.out.println("waiting credentials");
                Thread.sleep(10 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConfigSetup.readConfig();
        LocalAccount localAccount = new LocalAccount(apiKey, apiSecret);
        BuySell.setAccount(localAccount);
        String current = "";
        try {
            List<Currency> currencies = new ArrayList<>();
            if (BuySell.TEST) {
                for (String symbol : ConfigSetup.getCurrencies()) {
                    String pair = symbol + ConfigSetup.getFiat();
                    int candlesAmount = 1440;
                    long startMillis = 1609452000000L;
//                    List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1640060400000L, null, 1500);

//                    List<Candlestick> history = CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE, 1644060400000L, null, 1500);
                    List<Candlestick> history = new ArrayList<>();
                    for (int i = 500; i < 620; i++) {
                        history.addAll(CurrentAPI.getClient().getCandlestick(pair, CandlestickInterval.ONE_MINUTE,  startMillis+ i * 86400000L, null, candlesAmount));
                        System.out.println("Added " + i);
                    }
                    for (int deltaRSI = 20; deltaRSI <= 20; deltaRSI += 2) {
                        for (double deltaStop = 0.008; deltaStop <= 0.008; deltaStop += 0.002) {
                            for (double deltaProfit = 0.1; deltaProfit <= 0.1; deltaProfit += 0.002) {
                                Optional<Trade> tradeOptionalROE = Optional.empty();
                                upsert(currencies, symbol, deltaRSI, deltaStop, deltaProfit, tradeOptionalROE, history);
                            }
                        }
                    }
                }
            } else {
                Optional<Trade> tradeOptionalROE = tradeRepository.findBySymbolAndLongRSIAndShortRSIAndStop(CURRENCY, 30, 70, 0.012);
                upsert(currencies, CURRENCY, 0, 0.012, 0.01, tradeOptionalROE, null);
            }
            currencies.forEach(System.out::println);
        } catch (Exception e) {
            System.out.println("---Could not add " + current + ConfigSetup.getFiat());
            System.out.println(e.getMessage());
        }
    }

    private void upsert(List<Currency> currencies, String symbol, int deltaRSI, double deltaStop, double deltaProfit, Optional<Trade> tradeOptional, List<Candlestick> history) {
        Currency currency;
        if (tradeOptional.isEmpty()) {
            currency = new Currency(symbol, 1000.0, 30 - deltaRSI, 70 + deltaRSI, deltaStop, deltaProfit, 0.0, history);
        } else {
            Trade trade = tradeOptional.get();
            currency = new Currency(symbol, trade.getProfit(), 30 - deltaRSI, 70 + deltaRSI, deltaStop, deltaProfit, trade.getSellPrice(), history);
            currency.setInLong(trade.getInLong());
            currency.setInShort(trade.getInShort());
        }
        currencies.add(currency);
    }

    public List<Trade> getTrades() {
        return tradeRepository.findAll().stream()
                .sorted(Comparator.comparing(Trade::getProfit).reversed())
                .collect(Collectors.toList());
    }

    public void clean() {
        tradeRepository.deleteAll();
    }

}
