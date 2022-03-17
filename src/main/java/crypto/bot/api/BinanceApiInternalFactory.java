package crypto.bot.api;//package crypto.bot.api;
//
//import com.binance.client.SubscriptionOptions;
//import com.binance.client.impl.WebSocketStreamClientImpl;
//
//import java.net.URI;
//
//public final class BinanceApiInternalFactory {
//    private static final BinanceApiInternalFactory instance = new BinanceApiInternalFactory();
//
//    public static BinanceApiInternalFactory getInstance() {
//        return instance;
//    }
//
//    public SyncRequestClient createSyncRequestClient(String apiKey, String secretKey, RequestOptions options) {
//        RequestOptions requestOptions = new RequestOptions(options);
//        RestApiRequestImpl requestImpl = new RestApiRequestImpl(apiKey, secretKey, requestOptions);
//        return new SyncRequestImpl(requestImpl);
//    }
//
//    public SubscriptionClient createSubscriptionClient(String apiKey, String secretKey, SubscriptionOptions options) {
//        SubscriptionOptions subscriptionOptions = new SubscriptionOptions(options);
//        com.binance.client.RequestOptions requestOptions = new com.binance.client.RequestOptions();
//
//        try {
//            String host = (new URI(options.getUri())).getHost();
//            requestOptions.setUrl("https://" + host);
//        } catch (Exception var7) {
//        }
//
//        SubscriptionClient webSocketStreamClient = new WebSocketStreamClientImpl(apiKey, secretKey, subscriptionOptions);
//        return webSocketStreamClient;
//    }
//}
//
