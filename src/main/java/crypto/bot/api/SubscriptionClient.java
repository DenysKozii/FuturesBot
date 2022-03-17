package crypto.bot.api;//package crypto.bot.api;
//
//import com.binance.client.SubscriptionErrorHandler;
//import com.binance.client.SubscriptionListener;
//import com.binance.client.SubscriptionOptions;
//import com.binance.client.model.event.SymbolTickerEvent;
//
//public interface SubscriptionClient {
//    static SubscriptionClient create() {
//        return create("", "", new SubscriptionOptions());
//    }
//
//    static SubscriptionClient create(String apiKey, String secretKey, SubscriptionOptions subscriptionOptions) {
//        return BinanceApiInternalFactory.getInstance().createSubscriptionClient(apiKey, secretKey, subscriptionOptions);
//    }
//
//    void subscribeSymbolTickerEvent(String var1, SubscriptionListener<SymbolTickerEvent> var2, SubscriptionErrorHandler var3);
//
//}
