package crypto.bot.trading;

import com.binance.client.SyncRequestClient;
import com.binance.client.exception.BinanceApiException;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.AccountInformation;
import com.binance.client.model.trade.Order;
import com.binance.client.model.trade.Position;

import java.util.List;
import java.util.Optional;

public class BuySell {

    private static LocalAccount localAccount;
    public static double TRADE_DELTA = 10;
    public static double MONEY_PERCENTAGE_LIMIT;
    public static double MONEY_LIMIT;
    public static Integer LEVERAGE;

    public static void setAccount(LocalAccount localAccount) {
        BuySell.localAccount = localAccount;
    }

    public static LocalAccount getAccount() {
        return localAccount;
    }

    private BuySell() {
        throw new IllegalStateException("Utility class");
    }

    public static void open(Currency currency, boolean inLong) {
        double quantity = nextQuantity();
        if (quantity != 0) {
            placeOrder(currency, nextQuantity(), true, inLong);
        }
    }

    public static void close(Currency currency, boolean inLong) {
        placeOrder(currency, 0, false, inLong);
    }

    private static double nextQuantity() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        double quantity = accountInformation.getAvailableBalance().doubleValue() - TRADE_DELTA;
        return Math.max(quantity, 0);
    }

    public static void placeOpenOrder(SyncRequestClient clientFutures, Currency currency, double amount, boolean inLong) {
        amount /= currency.getPrice();
        amount *= LEVERAGE;
        String positionAmount = String.valueOf((int) amount);
        if ((int) amount == 0) {
            currency.setInLong(false);
            currency.setInShort(false);
            return;
        }
        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();

        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() != 0) {
            return;
        }
        if (inLong) {
            Order order = clientFutures.postOrder(
                    currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
            currency.log(order.getStatus() + " open long = " + positionAmount);
        } else {
            Order order = clientFutures.postOrder(
                    currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
            currency.log(order.getStatus() + " open short = " + positionAmount);
        }
    }

    public static void placeCloseOrder(SyncRequestClient clientFutures, Currency currency, boolean inLong) {
        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();

        if (position.isEmpty() || position.get().getPositionAmt().doubleValue() == 0) {
            return;
        }

        String positionAmount = position.get().getPositionAmt().toString();
        List<Order> openOrders = clientFutures.getOpenOrders(currency.getPair());
        if (inLong) {
            if (openOrders.isEmpty()) {
                Order order = clientFutures.postOrder(
                        currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
                currency.log(order.getStatus() + " close long = " + positionAmount);
            }
//            double profit = currency.getPrice() / currency.getEntryPrice();
//            currency.setMoney(currency.getMoney() * profit * 0.9985);
        } else {
            if (openOrders.isEmpty()) {
                positionAmount = String.valueOf(-1 * Double.parseDouble(positionAmount));
                Order order = clientFutures.postOrder(
                        currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
                        positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
                currency.log(order.getStatus() + "close short = " + positionAmount);
            }
//            double profit = currency.getEntryPrice() / currency.getPrice();
//            currency.setMoney(currency.getMoney() * profit * 0.9985);
        }
    }

    public static void placeOrder(Currency currency, double amount, boolean open, boolean inLong) {
        currency.log("\n---Placing a " + (open ? "open" : "close") + " market order for " + currency.getPair());
        try {
            SyncRequestClient clientFutures = CurrentAPI.getClient();
            try {
                clientFutures.changeMarginType(currency.getPair(), MarginType.ISOLATED);
            } catch (Exception ignored) {
            }
            clientFutures.changeInitialLeverage(currency.getPair(), LEVERAGE);

            if (open) {
                System.out.println("open");
                placeOpenOrder(clientFutures, currency, amount, inLong);
            } else {
                System.out.println("close");
                placeCloseOrder(clientFutures, currency, inLong);
            }
        } catch (BinanceApiException e) {
            System.out.println(e.getMessage());
        }
    }
}
