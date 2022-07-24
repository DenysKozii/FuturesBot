package crypto.bot.modes;

import crypto.bot.entity.Trade;
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


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        System.out.println(tradeRepository.count());
        try {
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
                for (int deltaRSI = -20; deltaRSI <= 30; deltaRSI += 5) {
                    for (double deltaStop = 0.004; deltaStop <= 0.014; deltaStop += 0.003) {
                        Optional<Trade> tradeOptional = tradeRepository.findBySymbolAndLongRSIAndShortRSIAndStop(symbol, 30 + deltaRSI, 70 - deltaRSI, deltaStop);
                        Currency currency;
                        if (tradeOptional.isEmpty()) {
                            currency = new Currency(symbol, 1000.0, 30 + deltaRSI, 70 - deltaRSI, deltaStop);
                            Trade trade = new Trade();
                            trade.setSymbol(symbol);
                            trade.setProfit(currency.getMoney());
                            trade.setLongRSI(currency.getLongOpenRSI());
                            trade.setShortRSI(currency.getShortOpenRSI());
                            trade.setStop(currency.getSELL_ROE());
                            tradeRepository.save(trade);
                        } else {
                            currency = new Currency(symbol, tradeOptional.get().getProfit(), 30 + deltaRSI, 70 - deltaRSI, deltaStop);
                        }
                        currencies.add(currency);
                    }
                }
            }
            try {
                while (true) {
                    System.out.println("Update profit in database");
                    for (Currency currency : currencies) {
                        Optional<Trade> tradeOptional = tradeRepository.findBySymbolAndLongRSIAndShortRSIAndStop(currency.getPair().split(ConfigSetup.getFiat())[0], currency.getLongOpenRSI(), currency.getShortOpenRSI(), currency.getSELL_ROE());
                        if (tradeOptional.isPresent()) {
                            Trade trade = tradeOptional.get();
                            trade.setProfit(currency.getProfit());
                            tradeRepository.save(trade);
                        }
                    }
                    Thread.sleep(10 * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("---Could not add " + current + ConfigSetup.getFiat());
            System.out.println(e.getMessage());
        }
    }

    public List<Trade> getTrades() {
        return tradeRepository.findAll().stream()
                .sorted(Comparator.comparing(Trade::getProfit).reversed())
                .collect(Collectors.toList());
    }

    public void clean() {
        tradeRepository.deleteAll();
        ;
    }

}
