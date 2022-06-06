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

    public static void setAccount(LocalAccount localAccount) {
        BuySell.localAccount = localAccount;
    }

    public static LocalAccount getAccount() {
        return localAccount;
    }

    private BuySell() {
        throw new IllegalStateException("Utility class");
    }

    public static void open(Currency currency) {
        double amount = nextAmount();
        if (amount != 0) {
            placeOrder(currency, nextAmount(), true);
        }
    }

    public static void close(Currency currency) {
//        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
//        if (position.isPresent() && position.get().getPositionAmt().doubleValue() != 0) {
            placeOrder(currency, 0, false);
//        }
    }

    private static double nextAmount() {
        AccountInformation accountInformation = CurrentAPI.getClient().getAccountInformation();
        return accountInformation.getAvailableBalance().doubleValue() - MONEY_LIMIT - MONEY_PER_TRADE > 0 ? MONEY_PER_TRADE : 0;
    }

    public static void placeOrder(Currency currency, double amount, boolean open) {
        currency.log("\n---Placing a " + (open ? "open" : "close") + " market order for " + currency.getPair());
        try {
            SyncRequestClient clientFutures = CurrentAPI.getClient();
            try {
                clientFutures.changeMarginType(currency.getPair(), MarginType.ISOLATED);
            } catch (Exception ignored) {
            }
            clientFutures.changeInitialLeverage(currency.getPair(), LEVERAGE);
            String positionAmount = String.valueOf(amount);

            if (open && currency.isInLong()) {
                amount /= currency.getPrice();
                amount *= LEVERAGE;
                positionAmount = String.valueOf((int) amount);
                currency.log("positionAmount = " + positionAmount);
                if ((int) amount != 0) {
                    Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
                    if (position.isPresent() && position.get().getPositionAmt().doubleValue() == 0) {
                        Order order = clientFutures.postOrder(
                                currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
                                positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
                        currency.log(order.getStatus() + " open long = " + positionAmount);
                    }
                } else {
                    currency.setInLong(false);
                }
            }
            if (!open && currency.isInLong()) {
                currency.log("positionAmount = " + positionAmount);
                Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
                while (position.isPresent() && position.get().getPositionAmt().doubleValue() > 0) {
                    positionAmount = position.get().getPositionAmt().toString();
                    List<Order> openOrders = clientFutures.getOpenOrders(currency.getPair());
                    if (openOrders.isEmpty()) {
                        Order order = clientFutures.postOrder(
                                currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
                                positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
                        currency.log(order.getStatus() + " close long = " + positionAmount);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
                }
            }
            if (open && currency.isInShort()) {
                    amount /= currency.getPrice();
                    amount *= LEVERAGE;
                    positionAmount = String.valueOf((int) amount);
                    currency.log("positionAmount = " + positionAmount);
                    if ((int) amount != 0) {
                        Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
                        if (position.isPresent() && position.get().getPositionAmt().doubleValue() == 0) {
                            Order order = clientFutures.postOrder(
                                    currency.getPair(), OrderSide.SELL, PositionSide.BOTH, OrderType.MARKET, null,
                                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
                            currency.log(order.getStatus() + " open short = " + positionAmount);
                        }
                    } else {
                        currency.setInShort(false);
                    }
                }
                if (!open && currency.isInShort()) {
                    Optional<Position> position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
                    while (position.isPresent() && position.get().getPositionAmt().doubleValue() < 0) {
                        positionAmount = position.get().getPositionAmt().toString();
                        positionAmount = String.valueOf(-1 * Double.parseDouble(positionAmount));
                        List<Order> openOrders = clientFutures.getOpenOrders(currency.getPair());
                        if (openOrders.isEmpty()) {
                            Order order = clientFutures.postOrder(
                                    currency.getPair(), OrderSide.BUY, PositionSide.BOTH, OrderType.MARKET, null,
                                    positionAmount, null, null, null, null, null, null, null, null, null, NewOrderRespType.RESULT);
                            currency.log(order.getStatus() + "close short = " + positionAmount);
                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        position = CurrentAPI.getClient().getAccountInformation().getPositions().stream().filter(o -> o.getSymbol().equals(currency.getPair())).findFirst();
                    }
                }
            } catch (BinanceApiException e) {
                System.out.println(e.getMessage());
            }
    }
//
//    private boolean placeLongCloseOrder() {
//
//    }
//
//    private boolean placeShortCloseOrder() {
//
//    }
}
