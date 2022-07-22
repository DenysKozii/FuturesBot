package crypto.bot.modes;

import crypto.bot.system.ConfigSetup;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.Currency;
import crypto.bot.trading.LocalAccount;


public final class Live {
    public static String apiKey;
    public static String apiSecret;

    public static void init() {

        while (apiKey == null && apiSecret == null) {
            try {
                System.out.println("waiting credentials");
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        LocalAccount localAccount = new LocalAccount(apiKey, apiSecret);
        BuySell.setAccount(localAccount);

        String current = "";
        try {
            for (String currency : ConfigSetup.getCurrencies()) {
                for (int deltaRSI = -20; deltaRSI <= 10; deltaRSI += 10) {
                    for (double deltaStop = 0.004; deltaStop <= 0.013; deltaStop += 0.003) {
                        new Currency(currency, 30 + deltaRSI, 70 - deltaRSI, deltaStop);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("---Could not add " + current + ConfigSetup.getFiat());
            System.out.println(e.getMessage());
        }
    }

}
