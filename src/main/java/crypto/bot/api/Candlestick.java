package crypto.bot.api;//package crypto.bot.api;
//
//import com.binance.client.constant.BinanceApiConstants;
//import java.math.BigDecimal;
//import org.apache.commons.lang3.builder.ToStringBuilder;
//
//public class Candlestick {
//    private Long openTime;
//    private BigDecimal open;
//    private BigDecimal high;
//    private BigDecimal low;
//    private BigDecimal close;
//    private BigDecimal volume;
//    private Long closeTime;
//    private BigDecimal quoteAssetVolume;
//    private Integer numTrades;
//    private BigDecimal takerBuyBaseAssetVolume;
//    private BigDecimal takerBuyQuoteAssetVolume;
//
//    public Candlestick() {
//    }
//
//    public Long getOpenTime() {
//        return this.openTime;
//    }
//
//    public void setOpenTime(Long openTime) {
//        this.openTime = openTime;
//    }
//
//    public BigDecimal getOpen() {
//        return this.open;
//    }
//
//    public void setOpen(BigDecimal open) {
//        this.open = open;
//    }
//
//    public BigDecimal getHigh() {
//        return this.high;
//    }
//
//    public void setHigh(BigDecimal high) {
//        this.high = high;
//    }
//
//    public BigDecimal getLow() {
//        return this.low;
//    }
//
//    public void setLow(BigDecimal low) {
//        this.low = low;
//    }
//
//    public BigDecimal getClose() {
//        return this.close;
//    }
//
//    public void setClose(BigDecimal close) {
//        this.close = close;
//    }
//
//    public BigDecimal getVolume() {
//        return this.volume;
//    }
//
//    public void setVolume(BigDecimal volume) {
//        this.volume = volume;
//    }
//
//    public Long getCloseTime() {
//        return this.closeTime;
//    }
//
//    public void setCloseTime(Long closeTime) {
//        this.closeTime = closeTime;
//    }
//
//    public BigDecimal getQuoteAssetVolume() {
//        return this.quoteAssetVolume;
//    }
//
//    public void setQuoteAssetVolume(BigDecimal quoteAssetVolume) {
//        this.quoteAssetVolume = quoteAssetVolume;
//    }
//
//    public Integer getNumTrades() {
//        return this.numTrades;
//    }
//
//    public void setNumTrades(Integer numTrades) {
//        this.numTrades = numTrades;
//    }
//
//    public BigDecimal getTakerBuyBaseAssetVolume() {
//        return this.takerBuyBaseAssetVolume;
//    }
//
//    public void setTakerBuyBaseAssetVolume(BigDecimal takerBuyBaseAssetVolume) {
//        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
//    }
//
//    public BigDecimal getTakerBuyQuoteAssetVolume() {
//        return this.takerBuyQuoteAssetVolume;
//    }
//
//    public void setTakerBuyQuoteAssetVolume(BigDecimal takerBuyQuoteAssetVolume) {
//        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
//    }
//
//    public String toString() {
//        return (new ToStringBuilder(this, BinanceApiConstants.TO_STRING_BUILDER_STYLE)).append("openTime", this.openTime).append("open", this.open).append("high", this.high).append("low", this.low).append("close", this.close).append("volume", this.volume).append("closeTime", this.closeTime).append("quoteAssetVolume", this.quoteAssetVolume).append("numTrades", this.numTrades).append("takerBuyBaseAssetVolume", this.takerBuyBaseAssetVolume).append("takerBuyQuoteAssetVolume", this.takerBuyQuoteAssetVolume).toString();
//    }
//}
