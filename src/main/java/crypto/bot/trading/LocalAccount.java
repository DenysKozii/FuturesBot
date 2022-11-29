package crypto.bot.trading;

import com.binance.client.model.trade.AccountInformation;

import crypto.bot.system.Formatter;

public class LocalAccount {
    private AccountInformation realAccount;

    public LocalAccount(String apiKey, String secretApiKey) {
        CurrentAPI.login(apiKey, secretApiKey);
        try {
            realAccount = CurrentAPI.getClient().getAccountInformation();
        } catch (RuntimeException runtimeException){
            System.out.println(runtimeException.getMessage());
            System.out.println("waiting change ip");
            try {
                Thread.sleep(1 * 30 * 1000);
                realAccount = CurrentAPI.getClient().getAccountInformation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!realAccount.getCanTrade()) {
            System.out.println("Can't trade!");
        }
        double fiatValue = realAccount.getAvailableBalance().doubleValue();
        BuySell.MONEY_LIMIT = fiatValue * BuySell.MONEY_PERCENTAGE_LIMIT;
        System.out.println("---Starting FIAT: " + Formatter.formatDecimal(fiatValue));
    }

    public AccountInformation getRealAccount() {
        return realAccount;
    }

}
