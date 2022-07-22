package crypto.bot.trading;

import com.binance.client.SyncRequestClient;
import com.binance.client.exception.BinanceApiException;
import com.binance.client.model.trade.AccountInformation;

public class BuySell {

    private static LocalAccount localAccount;
    public static  double       MONEY_PER_TRADE;
    public static  double       MONEY_PERCENTAGE_LIMIT;
    public static  double       MONEY_LIMIT;
    public static  Integer      LEVERAGE;

    public static void setAccount(LocalAccount localAccount) {
        BuySell.localAccount = localAccount;
    }

    public static LocalAccount getAccount() {
        return localAccount;
    }

    private BuySell() {
        throw new IllegalStateException("Utility class");
    }

    public static void open(Currency currency,  boolean inLong) {
        double amount = nextAmount();
        if (amount != 0) {
            placeOrder(currency, nextAmount(), true, inLong);
        }
    }

    public static void close(Currency currency, boolean inLong) {
        placeOrder(currency, 0, false, inLong);
    }

    private static double nextAmount() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        return accountInformation.getAvailableBalance().doubleValue() - MONEY_PER_TRADE > 0 ? MONEY_PER_TRADE : 0;
    }

    public static void placeOpenOrder(SyncRequestClient clientFutures, Currency currency, double amount, boolean inLong) {
//        amount /= currency.getPrice();
//        amount *= LEVERAGE;
//        String positionAmount = String.valueOf((int) amount);
//        if ((int) amount == 0) {
//            currency.setInLong(false);
//            currency.setInShort(false);
//            return;
//        }
//        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
//
//        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() != 0) {
//            return;
//        }
//        if (inLong) {
//            Order order = clientFutures.postOrder(
//                    currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
//                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//            currency.log(order.getStatus() + " open long = " + positionAmount);
        currency.setMoney(currency.getMoney() * 0.9985);
//        } else {
//            Order order = clientFutures.postOrder(
//                    currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
//                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//            currency.log(order.getStatus() + " open short = " + positionAmount);
//        }
    }

    public static void placeCloseOrder(SyncRequestClient clientFutures, Currency currency, boolean inLong) {
//        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();

//        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() == 0) {
//            return;
//        }

//        String positionAmount = position.get().getPositionAmt().toString();
//        List<Order> openOrders = clientFutures.getOpenOrders(currency.getPair());
        if (inLong) {
//            if (openOrders.isEmpty()) {
//                Order order = clientFutures.postOrder(
//                        currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
//                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//                currency.log(order.getStatus() + " close long = " + positionAmount);
//            }
            double profit = currency.getPrice() / currency.getEntryPrice();
            currency.setMoney(currency.getMoney() * profit * 0.9985);
        } else {
//            if (openOrders.isEmpty()) {
//                positionAmount = String.valueOf(-1 * Double.parseDouble(positionAmount));
//                Order order = clientFutures.postOrder(
//                        currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
//                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//                currency.log(order.getStatus() + "close short = " + positionAmount);
//            }
            double profit = currency.getEntryPrice() / currency.getPrice();
            currency.setMoney(currency.getMoney() * profit * 0.9985);
        }
    }

    public static void placeOrder(Currency currency, double amount, boolean open, boolean inLong) {
        currency.log("\n---Placing a " + (open ? "open" : "close") + " market order for " + currency.getPair());
        try {
//            SyncRequestClient clientFutures = CurrentAPI.getClient();
//            try {
//                clientFutures.changeMarginType(currency.getPair(), MarginType.ISOLATED);
//            } catch (Exception ignored) {
//            }
//            clientFutures.changeInitialLeverage(currency.getPair(), LEVERAGE);

            if (open) {
                System.out.println("open");
                placeOpenOrder(null, currency, amount, inLong);
            } else {
                System.out.println("close");
                placeCloseOrder(null, currency, inLong);
            }
        } catch (BinanceApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
