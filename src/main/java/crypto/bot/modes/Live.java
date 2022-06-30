package crypto.bot.modes;

import com.binance.client.model.trade.Position;

import crypto.bot.system.ConfigSetup;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.Currency;
import crypto.bot.trading.LocalAccount;

import java.util.ArrayList;
import java.util.List;


public final class Live {
    public static String apiKey;
    public static String apiSecret;

    public static void init() {

//        while (apiKey == null && apiSecret == null) {
//            try {
//                System.out.println("waiting credentials");
//                Thread.sleep(10 * 1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        LocalAccount localAccount = new LocalAccount(apiKey, apiSecret);
//        BuySell.setAccount(localAccount);
//
        String current = "";
        try {
            List<String> addedCurrencies = new ArrayList<>();
//            for (Position position : localAccount.getRealAccount().getPositions()) {
//                current = position.getSymbol().replace(ConfigSetup.getFiat(), "");
//                if (ConfigSetup.getCurrencies().contains(current)) {
//                    new Currency(current);
//                    addedCurrencies.add(current);
//                }
//            }
            for (String currency : ConfigSetup.getCurrencies()) {
                if (!addedCurrencies.contains(currency)) {
                    new Currency(currency);
                }
            }
        } catch (Exception e) {
            System.out.println("---Could not add " + current + ConfigSetup.getFiat());
            System.out.println(e.getMessage());
        }
    }

}
