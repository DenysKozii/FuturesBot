package crypto.bot.modes;

import crypto.bot.entity.Credentials;
import crypto.bot.entity.Trade;
import crypto.bot.enums.Strategy;
import crypto.bot.repository.CredentialsRepository;
import crypto.bot.repository.TradeRepository;
import crypto.bot.system.ConfigSetup;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.Currency;
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
            for (String symbol : ConfigSetup.getCurrencies()) {
                for (int deltaRSI = 0; deltaRSI <= 0; deltaRSI += 5) {
                    for (double deltaStop = 0.011; deltaStop <= 0.011; deltaStop += 0.005) {
                        Optional<Trade> tradeOptionalROE = tradeRepository.findBySymbolAndLongRSIAndShortRSIAndStopAndStrategy(symbol, 30 + deltaRSI, 70 - deltaRSI, deltaStop, Strategy.ROE);
                        upsert(currencies, symbol, deltaRSI, deltaStop, tradeOptionalROE, Strategy.ROE);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("---Could not add " + current + ConfigSetup.getFiat());
            System.out.println(e.getMessage());
        }
    }

    private void upsert(List<Currency> currencies, String symbol, int deltaRSI, double deltaStop, Optional<Trade> tradeOptional, Strategy strategy) {
        Currency currency;
        if (tradeOptional.isEmpty()) {
            currency = new Currency(symbol, 1000.0, 30 + deltaRSI, 70 - deltaRSI, deltaStop, strategy);
            Trade trade = new Trade();
            trade.setSymbol(symbol);
            trade.setProfit(currency.getMoney());
            trade.setLongRSI(currency.getLongOpenRSI());
            trade.setShortRSI(currency.getShortOpenRSI());
            trade.setStop(currency.getSELL_ROE());
            trade.setStrategy(strategy);
            trade.setInLong(currency.isInLong());
            trade.setInShort(currency.isInShort());
//            tradeRepository.save(trade);
        } else {
            currency = new Currency(symbol, tradeOptional.get().getProfit(), 30 + deltaRSI, 70 - deltaRSI, deltaStop, strategy);
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
