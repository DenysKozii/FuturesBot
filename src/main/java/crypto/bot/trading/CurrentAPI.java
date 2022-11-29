package crypto.bot.trading;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.impl.BinanceApiInternalFactory;

public final class CurrentAPI {
    private static SyncRequestClient client;

    public static void login(String apiKey, String secretKey) {
        client = BinanceApiInternalFactory
                .getInstance()
                .createSyncRequestClient(apiKey, secretKey, new RequestOptions());
    }

    public static SyncRequestClient getClient() {
        return client;
    }
}
