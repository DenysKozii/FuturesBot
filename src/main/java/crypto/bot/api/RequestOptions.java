package crypto.bot.api;//package crypto.bot.api;
//
//import java.net.URL;
//
//public class RequestOptions {
//    private String url = "https://fapi.binance.com";
//
//    public RequestOptions() {
//    }
//
//    public RequestOptions(RequestOptions option) {
//        this.url = option.url;
//    }
//
//    public void setUrl(String url) {
//        try {
//            URL u = new URL(url);
//            this.url = u.toString();
//        } catch (Exception var3) {
//            throw new RuntimeException("The URI is incorrect: " + var3.getMessage());
//        }
//
//        this.url = url;
//    }
//
//    public String getUrl() {
//        return this.url;
//    }
//}
