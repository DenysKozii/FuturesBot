package crypto.bot.api;//package crypto.bot.api;
//
//import com.binance.client.model.ResponseResult;
//import com.binance.client.model.enums.MarginType;
//import com.binance.client.model.enums.NewOrderRespType;
//import com.binance.client.model.enums.OrderSide;
//import com.binance.client.model.enums.OrderType;
//import com.binance.client.model.enums.PositionSide;
//import com.binance.client.model.enums.TimeInForce;
//import com.binance.client.model.enums.WorkingType;
//import com.binance.client.model.market.ExchangeInformation;
//import com.binance.client.model.trade.AccountBalance;
//import com.binance.client.model.trade.Leverage;
//import com.binance.client.model.trade.Order;
//
//import java.util.List;
//
//public class SyncRequestImpl implements SyncRequestClient {
//    private final RestApiRequestImpl requestImpl;
//
//    SyncRequestImpl(RestApiRequestImpl requestImpl) {
//        this.requestImpl = requestImpl;
//    }
//
//    public ExchangeInformation getExchangeInformation() {
//        return (ExchangeInformation) RestApiInvoker.callSync(this.requestImpl.getExchangeInformation());
//    }
//
//
//    public Order postOrder(String symbol, OrderSide side, PositionSide positionSide, OrderType orderType, TimeInForce timeInForce, String quantity, String price, String reduceOnly, String newClientOrderId, String stopPrice, String closePosition, String activationPrice, String callbackRate, WorkingType workingType, String priceProtect, NewOrderRespType newOrderRespType) {
//        return (Order) RestApiInvoker.callSync(this.requestImpl.postOrder(symbol, side, positionSide, orderType, timeInForce, quantity, reduceOnly, price, newClientOrderId, stopPrice, closePosition, activationPrice, callbackRate, workingType, priceProtect, newOrderRespType));
//    }
//
//
//    public ResponseResult changePositionSide(String dual) {
//        return (ResponseResult) RestApiInvoker.callSync(this.requestImpl.changePositionSide(dual));
//    }
//
//    public ResponseResult changeMarginType(String symbolName, MarginType marginType) {
//        return (ResponseResult) RestApiInvoker.callSync(this.requestImpl.changeMarginType(symbolName, marginType));
//    }
//
//
//    public Order getOrder(String symbol, Long orderId, String origClientOrderId) {
//        return (Order) RestApiInvoker.callSync(this.requestImpl.getOrder(symbol, orderId, origClientOrderId));
//    }
//
//    public List<AccountBalance> getBalance() {
//        return (List) RestApiInvoker.callSync(this.requestImpl.getBalance());
//    }
//
//    public AccountInformation getAccountInformation() {
//        return (AccountInformation) RestApiInvoker.callSync(this.requestImpl.getAccountInformation());
//    }
//
//    public Leverage changeInitialLeverage(String symbol, Integer leverage) {
//        return (Leverage) RestApiInvoker.callSync(this.requestImpl.changeInitialLeverage(symbol, leverage));
//    }
//}
//
