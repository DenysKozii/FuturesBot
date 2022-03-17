package crypto.bot.trading;

import com.binance.client.model.trade.AccountInformation;

import crypto.bot.system.Formatter;

public class LocalAccount {
    private final AccountInformation realAccount;

    public LocalAccount(String apiKey, String secretApiKey) {
        CurrentAPI.login(apiKey, secretApiKey);
        realAccount = CurrentAPI.getClient().getAccountInformation();
        if (!realAccount.getCanTrade()) {
            System.out.println("Can't trade!");
        }
        double fiatValue = realAccount.getAvailableBalance().doubleValue();
        BuySell.MONEY_LIMIT = fiatValue * BuySell.MONEY_PERCENTAGE_LIMIT;
        System.out.println("---Starting FIAT: " + Formatter.formatDecimal(fiatValue) + ", money limit = " + BuySell.MONEY_LIMIT);
    }

    public AccountInformation getRealAccount() {
        return realAccount;
    }

}
