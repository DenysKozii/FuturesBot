package crypto.bot.api;//package crypto.bot.api;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.binance.client.exception.BinanceApiException;
//import com.binance.client.impl.RestApiRequest;
//import com.binance.client.impl.utils.JsonWrapperArray;
//import com.binance.client.impl.utils.UrlParamsBuilder;
//import com.binance.client.model.ResponseResult;
//import com.binance.client.model.enums.AutoCloseType;
//import com.binance.client.model.enums.MarginType;
//import com.binance.client.model.enums.NewOrderRespType;
//import com.binance.client.model.enums.OrderSide;
//import com.binance.client.model.enums.OrderType;
//import com.binance.client.model.enums.PositionSide;
//import com.binance.client.model.enums.TimeInForce;
//import com.binance.client.model.enums.WorkingType;
//import com.binance.client.model.market.ExchangeFilter;
//import com.binance.client.model.market.ExchangeInfoEntry;
//import com.binance.client.model.market.ExchangeInformation;
//import com.binance.client.model.market.LiquidationOrder;
//import com.binance.client.model.market.RateLimit;
//import com.binance.client.model.trade.Order;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import okhttp3.Request;
//import okhttp3.Request.Builder;
//
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//class RestApiRequestImpl {
//    private static final Logger log = LoggerFactory.getLogger(RestApiRequestImpl.class);
//    private String apiKey;
//    private String secretKey;
//    private String serverUrl;
//
//    RestApiRequestImpl(String apiKey, String secretKey, RequestOptions options) {
//        this.apiKey = apiKey;
//        this.secretKey = secretKey;
//        this.serverUrl = options.getUrl();
//    }
//
//    private Request createRequestByGet(String address, UrlParamsBuilder builder) {
//        log.debug("Request URL " + this.serverUrl);
//        return this.createRequestByGet(this.serverUrl, address, builder);
//    }
//
//    private Request createRequestByGet(String url, String address, UrlParamsBuilder builder) {
//        return this.createRequest(url, address, builder);
//    }
//
//    private Request createRequest(String url, String address, UrlParamsBuilder builder) {
//        String requestUrl = url + address;
//        log.debug("Request URL " + requestUrl);
//        if (builder != null) {
//            return builder.hasPostParam() ? (new Builder()).url(requestUrl).post(builder.buildPostBody()).addHeader("Content-Type", "application/json").addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build() : (new Builder()).url(requestUrl + builder.buildUrl()).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//        } else {
//            return (new Builder()).url(requestUrl).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//        }
//    }
//
//    private Request createRequestWithSignature(String url, String address, UrlParamsBuilder builder) {
//        if (builder == null) {
//            throw new BinanceApiException("RuntimeError", "[Invoking] Builder is null when create request with Signature");
//        } else {
//            String requestUrl = url + address;
//            (new ApiSignature()).createSignature(this.apiKey, this.secretKey, builder);
//            if (builder.hasPostParam()) {
//                requestUrl = requestUrl + builder.buildUrl();
//                return (new Builder()).url(requestUrl).post(builder.buildPostBody()).addHeader("Content-Type", "application/json").addHeader("X-MBX-APIKEY", this.apiKey).addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//            } else if (builder.checkMethod("PUT")) {
//                requestUrl = requestUrl + builder.buildUrl();
//                return (new Builder()).url(requestUrl).put(builder.buildPostBody()).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", this.apiKey).addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//            } else if (builder.checkMethod("DELETE")) {
//                requestUrl = requestUrl + builder.buildUrl();
//                return (new Builder()).url(requestUrl).delete().addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("client_SDK_Version", "binance_futures-1.0.1-java").addHeader("X-MBX-APIKEY", this.apiKey).build();
//            } else {
//                requestUrl = requestUrl + builder.buildUrl();
//                return (new Builder()).url(requestUrl).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("client_SDK_Version", "binance_futures-1.0.1-java").addHeader("X-MBX-APIKEY", this.apiKey).build();
//            }
//        }
//    }
//
//    private Request createRequestByPostWithSignature(String address, UrlParamsBuilder builder) {
//        return this.createRequestWithSignature(this.serverUrl, address, builder.setMethod("POST"));
//    }
//
//    private Request createRequestByGetWithSignature(String address, UrlParamsBuilder builder) {
//        return this.createRequestWithSignature(this.serverUrl, address, builder);
//    }
//
//    private Request createRequestByPutWithSignature(String address, UrlParamsBuilder builder) {
//        return this.createRequestWithSignature(this.serverUrl, address, builder.setMethod("PUT"));
//    }
//
//    private Request createRequestByDeleteWithSignature(String address, UrlParamsBuilder builder) {
//        return this.createRequestWithSignature(this.serverUrl, address, builder.setMethod("DELETE"));
//    }
//
//    private Request createRequestWithApikey(String url, String address, UrlParamsBuilder builder) {
//        if (builder == null) {
//            throw new BinanceApiException("RuntimeError", "[Invoking] Builder is null when create request with Signature");
//        } else {
//            String requestUrl = url + address;
//            requestUrl = requestUrl + builder.buildUrl();
//            if (builder.hasPostParam()) {
//                return (new Builder()).url(requestUrl).post(builder.buildPostBody()).addHeader("Content-Type", "application/json").addHeader("X-MBX-APIKEY", this.apiKey).addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//            } else if (builder.checkMethod("DELETE")) {
//                return (new Builder()).url(requestUrl).delete().addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", this.apiKey).addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//            } else {
//                return builder.checkMethod("PUT") ? (new Builder()).url(requestUrl).put(builder.buildPostBody()).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", this.apiKey).addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build() : (new Builder()).url(requestUrl).addHeader("Content-Type", "application/x-www-form-urlencoded").addHeader("X-MBX-APIKEY", this.apiKey).addHeader("client_SDK_Version", "binance_futures-1.0.1-java").build();
//            }
//        }
//    }
//
//    private Request createRequestByGetWithApikey(String address, UrlParamsBuilder builder) {
//        return this.createRequestWithApikey(this.serverUrl, address, builder);
//    }
//
//    RestApiRequest<ExchangeInformation> getExchangeInformation() {
//        RestApiRequest<ExchangeInformation> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build();
//        request.request = this.createRequestByGet("/fapi/v1/exchangeInfo", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            ExchangeInformation result = new ExchangeInformation();
//            result.setTimezone(jsonWrapper.getString("timezone"));
//            result.setServerTime(jsonWrapper.getLong("serverTime"));
//            List<RateLimit> elementList = new LinkedList();
//            JsonWrapperArray dataArray = jsonWrapper.getJsonArray("rateLimits");
//            dataArray.forEach((item) -> {
//                RateLimit element = new RateLimit();
//                element.setRateLimitType(item.getString("rateLimitType"));
//                element.setInterval(item.getString("interval"));
//                element.setIntervalNum(item.getLong("intervalNum"));
//                element.setLimit(item.getLong("limit"));
//                elementList.add(element);
//            });
//            result.setRateLimits(elementList);
//            List<ExchangeFilter> filterList = new LinkedList();
//            JsonWrapperArray filterArray = jsonWrapper.getJsonArray("exchangeFilters");
//            filterArray.forEach((item) -> {
//                ExchangeFilter filter = new ExchangeFilter();
//                filter.setFilterType(item.getString("filterType"));
//                filter.setMaxNumOrders(item.getLong("maxNumOrders"));
//                filter.setMaxNumAlgoOrders(item.getLong("maxNumAlgoOrders"));
//                filterList.add(filter);
//            });
//            result.setExchangeFilters(filterList);
//            List<ExchangeInfoEntry> symbolList = new LinkedList();
//            JsonWrapperArray symbolArray = jsonWrapper.getJsonArray("symbols");
//            symbolArray.forEach((item) -> {
//                ExchangeInfoEntry symbol = new ExchangeInfoEntry();
//                symbol.setSymbol(item.getString("symbol"));
//                symbol.setStatus(item.getString("status"));
//                symbol.setMaintMarginPercent(item.getBigDecimal("maintMarginPercent"));
//                symbol.setRequiredMarginPercent(item.getBigDecimal("requiredMarginPercent"));
//                symbol.setBaseAsset(item.getString("baseAsset"));
//                symbol.setQuoteAsset(item.getString("quoteAsset"));
//                symbol.setPricePrecision(item.getLong("pricePrecision"));
//                symbol.setQuantityPrecision(item.getLong("quantityPrecision"));
//                symbol.setBaseAssetPrecision(item.getLong("baseAssetPrecision"));
//                symbol.setQuotePrecision(item.getLong("quotePrecision"));
//                symbol.setOrderTypes(item.getJsonArray("orderTypes").convert2StringList());
//                symbol.setTimeInForce(item.getJsonArray("orderTypes").convert2StringList());
//                List<List<Map<String, String>>> valList = new LinkedList();
//                JsonWrapperArray valArray = item.getJsonArray("filters");
//                valArray.forEach((val) -> {
//                    valList.add(val.convert2DictList());
//                });
//                symbol.setFilters(valList);
//                symbolList.add(symbol);
//            });
//            result.setSymbols(symbolList);
//            return result;
//        };
//        return request;
//    }
//
//
//    RestApiRequest<List<LiquidationOrder>> getLiquidationOrders(String symbol, AutoCloseType autoCloseType, Long startTime, Long endTime, Integer limit) {
//        RestApiRequest<List<LiquidationOrder>> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build().putToUrl("symbol", symbol).putToUrl("startTime", startTime).putToUrl("endTime", endTime).putToUrl("limit", limit);
//        request.request = this.createRequestByGetWithApikey("/fapi/v1/allForceOrders", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            List<LiquidationOrder> result = new LinkedList();
//            JsonWrapperArray dataArray = jsonWrapper.getJsonArray("data");
//            dataArray.forEach((item) -> {
//                LiquidationOrder element = new LiquidationOrder();
//                element.setOrderId(item.getLong("orderId"));
//                element.setSymbol(item.getString("symbol"));
//                element.setStatus(item.getString("status"));
//                element.setClientOrderId(item.getString("clientOrderId"));
//                element.setPrice(item.getBigDecimal("price"));
//                element.setAveragePrice(item.getBigDecimal("avgPrice"));
//                element.setOrigQty(item.getBigDecimal("origQty"));
//                element.setExecutedQty(item.getBigDecimal("executedQty"));
//                element.setCumQuote(item.getBigDecimal("cumQuote"));
//                element.setTimeInForce(item.getString("timeInForce"));
//                element.setType(item.getString("type"));
//                element.setReduceOnly(item.getBoolean("reduceOnly"));
//                element.setClosePosition(item.getBoolean("closePosition"));
//                element.setSide(item.getString("side"));
//                element.setPositionSide(item.getString("positionSide"));
//                element.setStopPrice(item.getBigDecimal("stopPrice"));
//                element.setWorkingType(item.getString("workingType"));
//                element.setOrigType(item.getString("origType"));
//                element.setTime(item.getLong("time"));
//                element.setUpdateTime(item.getLong("updateTime"));
//                result.add(element);
//            });
//            return result;
//        };
//        return request;
//    }
//
//    RestApiRequest<List<Object>> postBatchOrders(String batchOrders) {
//        RestApiRequest<List<Object>> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build().putToUrl("batchOrders", batchOrders);
//        request.request = this.createRequestByPostWithSignature("/fapi/v1/batchOrders", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            JSONObject jsonObject = jsonWrapper.getJson();
//            List<Object> listResult = new ArrayList();
//            JSONArray jsonArray = (JSONArray)jsonObject.get("data");
//            jsonArray.forEach((obj) -> {
//                if (((JSONObject)obj).containsKey("code")) {
//                    ResponseResult responseResult = new ResponseResult();
//                    responseResult.setCode(((JSONObject)obj).getInteger("code"));
//                    responseResult.setMsg(((JSONObject)obj).getString("msg"));
//                    listResult.add(responseResult);
//                } else {
//                    Order o = new Order();
//                    JSONObject jsonObj = (JSONObject)obj;
//                    o.setClientOrderId(jsonWrapper.getString("clientOrderId"));
//                    o.setCumQty(jsonWrapper.getBigDecimal("cumQty"));
//                    o.setCumQuote(jsonWrapper.getBigDecimal("cumQuote"));
//                    o.setExecutedQty(jsonWrapper.getBigDecimal("executedQty"));
//                    o.setOrderId(jsonWrapper.getLong("orderId"));
//                    o.setAvgPrice(jsonWrapper.getBigDecimal("avgPrice"));
//                    o.setOrigQty(jsonWrapper.getBigDecimal("origQty"));
//                    o.setPrice(jsonWrapper.getBigDecimal("price"));
//                    o.setReduceOnly(jsonWrapper.getBoolean("reduceOnly"));
//                    o.setSide(jsonWrapper.getString("side"));
//                    o.setPositionSide(jsonWrapper.getString("positionSide"));
//                    o.setStatus(jsonWrapper.getString("status"));
//                    o.setStopPrice(jsonWrapper.getBigDecimal("stopPrice"));
//                    o.setClosePosition(jsonWrapper.getBoolean("closePosition"));
//                    o.setSymbol(jsonWrapper.getString("symbol"));
//                    o.setTimeInForce(jsonWrapper.getString("timeInForce"));
//                    o.setType(jsonWrapper.getString("type"));
//                    o.setOrigType(jsonWrapper.getString("origType"));
//                    if (jsonWrapper.containKey("activatePrice")) {
//                        o.setActivatePrice(jsonWrapper.getBigDecimal("activatePrice"));
//                    }
//
//                    if (jsonWrapper.containKey("priceRate")) {
//                        o.setActivatePrice(jsonWrapper.getBigDecimal("priceRate"));
//                    }
//
//                    o.setUpdateTime(jsonWrapper.getLong("updateTime"));
//                    o.setWorkingType(jsonWrapper.getString("workingType"));
//                    o.setPriceProtect(jsonWrapper.getBoolean("priceProtect"));
//                    listResult.add(o);
//                }
//
//            });
//            return listResult;
//        };
//        return request;
//    }
//
//    RestApiRequest<Order> postOrder(String symbol, OrderSide side, PositionSide positionSide, OrderType orderType, TimeInForce timeInForce, String quantity, String reduceOnly, String price, String newClientOrderId, String stopPrice, String closePosition, String activationPrice, String callBackRate, WorkingType workingType, String priceProtect, NewOrderRespType newOrderRespType) {
//        RestApiRequest<Order> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build().putToUrl("symbol", symbol).putToUrl("side", side).putToUrl("positionSide", positionSide).putToUrl("type", orderType).putToUrl("timeInForce", timeInForce).putToUrl("quantity", quantity).putToUrl("price", price).putToUrl("reduceOnly", reduceOnly).putToUrl("newClientOrderId", newClientOrderId).putToUrl("stopPrice", stopPrice).putToUrl("closePosition", closePosition).putToUrl("activationPrice", activationPrice).putToUrl("callBackRate", callBackRate).putToUrl("workingType", workingType).putToUrl("priceProtect", priceProtect).putToUrl("newOrderRespType", newOrderRespType);
//        request.request = this.createRequestByPostWithSignature("/fapi/v1/order", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            Order result = new Order();
//            result.setClientOrderId(jsonWrapper.getString("clientOrderId"));
//            result.setCumQuote(jsonWrapper.getBigDecimal("cumQuote"));
//            result.setExecutedQty(jsonWrapper.getBigDecimal("executedQty"));
//            result.setOrderId(jsonWrapper.getLong("orderId"));
//            result.setAvgPrice(jsonWrapper.getBigDecimal("avgPrice"));
//            result.setOrigQty(jsonWrapper.getBigDecimal("origQty"));
//            result.setPrice(jsonWrapper.getBigDecimal("price"));
//            result.setReduceOnly(jsonWrapper.getBoolean("reduceOnly"));
//            result.setSide(jsonWrapper.getString("side"));
//            result.setPositionSide(jsonWrapper.getString("positionSide"));
//            result.setStatus(jsonWrapper.getString("status"));
//            result.setStopPrice(jsonWrapper.getBigDecimal("stopPrice"));
//            result.setClosePosition(jsonWrapper.getBoolean("closePosition"));
//            result.setSymbol(jsonWrapper.getString("symbol"));
//            result.setTimeInForce(jsonWrapper.getString("timeInForce"));
//            result.setType(jsonWrapper.getString("type"));
//            result.setOrigType(jsonWrapper.getString("origType"));
//            if (jsonWrapper.containKey("activatePrice")) {
//                result.setActivatePrice(jsonWrapper.getBigDecimal("activatePrice"));
//            }
//
//            if (jsonWrapper.containKey("priceRate")) {
//                result.setActivatePrice(jsonWrapper.getBigDecimal("priceRate"));
//            }
//
//            result.setUpdateTime(jsonWrapper.getLong("updateTime"));
//            result.setWorkingType(jsonWrapper.getString("workingType"));
//            result.setPriceProtect(jsonWrapper.getBoolean("priceProtect"));
//            return result;
//        };
//        return request;
//    }
//
//    RestApiRequest<ResponseResult> changePositionSide(String dual) {
//        RestApiRequest<ResponseResult> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build().putToUrl("dualSidePosition", dual);
//        request.request = this.createRequestByPostWithSignature("/fapi/v1/positionSide/dual", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            ResponseResult result = new ResponseResult();
//            result.setCode(jsonWrapper.getInteger("code"));
//            result.setMsg(jsonWrapper.getString("msg"));
//            return result;
//        };
//        return request;
//    }
//
//    RestApiRequest<ResponseResult> changeMarginType(String symbolName, MarginType marginType) {
//        RestApiRequest<ResponseResult> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build().putToUrl("symbol", symbolName).putToUrl("marginType", marginType);
//        request.request = this.createRequestByPostWithSignature("/fapi/v1/marginType", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            ResponseResult result = new ResponseResult();
//            result.setCode(jsonWrapper.getInteger("code"));
//            result.setMsg(jsonWrapper.getString("msg"));
//            return result;
//        };
//        return request;
//    }
//
//    RestApiRequest<JSONObject> addPositionMargin(String symbol, Integer type, String amount, PositionSide positionSide) {
//        RestApiRequest<JSONObject> request = new RestApiRequest();
//        UrlParamsBuilder builder = UrlParamsBuilder.build().putToUrl("symbol", symbol).putToUrl("amount", amount).putToUrl("positionSide", positionSide.name()).putToUrl("type", type);
//        request.request = this.createRequestByPostWithSignature("/fapi/v1/positionMargin", builder);
//        request.jsonParser = (jsonWrapper) -> {
//            JSONObject result = new JSONObject();
//            result.put("code", jsonWrapper.getInteger("code"));
//            result.put("msg", jsonWrapper.getString("msg"));
//            result.put("amount", jsonWrapper.getDouble("amount"));
//            result.put("type", jsonWrapper.getInteger("type"));
//            return result;
//        };
//        return request;
//    }
//
//}
//
