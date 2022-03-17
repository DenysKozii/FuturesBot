package crypto.bot.api;//package crypto.bot.api;
//
//
//import java.util.List;
//
//public interface SyncRequestClient {
//    static SyncRequestClient create() {
//        return create("", "", new RequestOptions());
//    }
//
//    static SyncRequestClient create(String apiKey, String secretKey) {
//        return BinanceApiInternalFactory.getInstance().createSyncRequestClient(apiKey, secretKey, new com.binance.client.RequestOptions());
//    }
//
//    static SyncRequestClient create(String apiKey, String secretKey, RequestOptions options) {
//        return BinanceApiInternalFactory.getInstance().createSyncRequestClient(apiKey, secretKey, options);
//    }
//
//    List<Candlestick> getCandlestick(String var1, CandlestickInterval var2, Long var3, Long var4, Integer var5);
//
//
//    AccountInformation getAccountInformation();
//
//}
