package crypto.bot.trading;

import com.binance.client.SyncRequestClient;
import com.binance.client.exception.BinanceApiException;
import com.binance.client.model.enums.MarginType;
import com.binance.client.model.enums.NewOrderRespType;
import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.enums.OrderType;
import com.binance.client.model.enums.PositionSide;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;

import java.util.List;
import java.util.Optional;

public class BuySell {

    private static LocalAccount localAccount;
    public static  double       MONEY_PER_TRADE;
    public static  double       MONEY_PERCENTAGE_LIMIT;
    public static  double       MONEY_LIMIT;
    public static  Integer      LEVERAGE;
    public static double price;

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
        double amount = nextAmount(currency);
        if (amount != 0) {
            placeOrder(currency, nextAmount(currency), true, inLong);
        }
    }

    public static void close(Currency currency, boolean inLong) {
        placeOrder(currency, 0, false, inLong);
    }

    private static double nextAmount(Currency currency) {
//        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        return currency.MONEY;
    }

    public static void placeOpenOrder(SyncRequestClient clientFutures, Currency currency, double amount, boolean inLong) {
//        amount *= LEVERAGE;
        currency.MONEY -= currency.MONEY * 0.0007;
        price = currency.getPrice();
//        amount /= currency.getPrice();
//        String positionAmount = String.valueOf((int) amount);
//        if ((int) amount == 0) {
//            currency.setInLong(false);
//            currency.setInShort(false);
//            return;
//        }
//        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();

//        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() != 0) {
//            return;
//        }
//        if (inLong) {
//            Order order = clientFutures.postOrder(
//                    currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
//                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//            currency.log(order.getStatus() + " open long = " + positionAmount);
//        } else {
//            Order order = clientFutures.postOrder(
//                    currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
//                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//            currency.log(order.getStatus() + " open short = " + positionAmount);
//        }
    }

    public static void placeCloseOrder(SyncRequestClient clientFutures, Currency currency, boolean inLong) {
//        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
//
//        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() == 0) {
//            return;
//        }
//
//        String positionAmount = position.get().getPositionAmt().toString();
//        List<Order> openOrders = clientFutures.getOpenOrders(currency.getPair());
        if (inLong) {
            currency.MONEY = currency.MONEY * currency.getPrice() / price;
            currency.MONEY -= currency.MONEY * 0.0009;
//            if (openOrders.isEmpty()) {
//                Order order = clientFutures.postOrder(
//                        currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
//                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//                currency.log(order.getStatus() + " close long = " + positionAmount);
//            }
        } else {
//            if (openOrders.isEmpty()) {
            currency.MONEY = currency.MONEY * price / currency.getPrice();
            currency.MONEY -= currency.MONEY * 0.0009;
//                positionAmount = String.valueOf(-1 * Double.parseDouble(positionAmount));
//                Order order = clientFutures.postOrder(
//                        currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
//                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
//                currency.log(order.getStatus() + "close short = " + positionAmount);
//            }
        }
    }

    public static void placeOrder(Currency currency, double amount, boolean open, boolean inLong) {
        currency.log("\n---Placing a " + (open ? "open" : "close") + (inLong ? " long" : " short") + " market order for " + currency.getPair());
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
