package com.sushkov.projects.quoine.service;

import com.sushkov.projects.quoine.sockets.WebsocketClientEndpoint;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;

public class CurPairRequestService {

    private static final String BTC_USD_CURPAIR_CHANNEL = "product_cash_btcusd_1";
    private static final String BTC_JPY_CURPAIR_CHANNEL = "product_cash_btcjpy_5";
    private static final String SERVICE_URL = "wss://tap.liquid.com/app/LiquidTapClient";

    private BigDecimal btcusdAsk = BigDecimal.ZERO.setScale(5);
    private BigDecimal btcusdBid = BigDecimal.ZERO.setScale(5);

    private BigDecimal btcjpyAsk = BigDecimal.ZERO.setScale(5);
    private BigDecimal btcjpyBid = BigDecimal.ZERO.setScale(5);

    /**
     * Start request service. Send subscriber request and add message handler for response processing.
     *
     */
    public void startCurPairRequestService(){
        try ( WebsocketClientEndpoint clientEndPoint = new WebsocketClientEndpoint(new URI(SERVICE_URL))) {

            clientEndPoint.sendMessage("{\"event\":\"pusher:subscribe\",\"data\":{\"channel\":\"" + BTC_USD_CURPAIR_CHANNEL + "\"}}");
            clientEndPoint.sendMessage("{\"event\":\"pusher:subscribe\",\"data\":{\"channel\":\"" + BTC_JPY_CURPAIR_CHANNEL + "\"}}");

            while (clientEndPoint.getUserSession().isOpen()) {
                clientEndPoint.addMessageHandler(message -> {
                    JSONObject jObject = new JSONObject(message);
                    JSONObject dataObject = new JSONObject(jObject.get("data").toString());
                    if (!dataObject.isEmpty()) {
                        printCurPairRate(dataObject);
                    }
                });
            }
        } catch (URISyntaxException ex) {
            System.err.println("URISyntaxException exception: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Print currency pair update info.
     *
     * @param jsonData The json data from server response
     */
    private void printCurPairRate(JSONObject jsonData){
        if (jsonData.getString("currency_pair_code").equals("BTCUSD")) {

            if(!jsonData.getBigDecimal("market_ask").equals(btcusdAsk) || !jsonData.getBigDecimal("market_bid").equals(btcusdBid)){
                btcusdAsk = jsonData.getBigDecimal("market_ask");
                btcusdBid = jsonData.getBigDecimal("market_bid");

                System.out.println("Currency pair: [BTC/USD]    " +
                        "Market ask: [" + btcusdAsk + "]    " +
                        "Market bid: [" + btcusdBid + "]    " +
                        "Change time [" + new Date().toString() + "] ");
            }
        } else if(jsonData.getString("currency_pair_code").equals("BTCJPY")) {

            if (!jsonData.getBigDecimal("market_ask").equals(btcjpyAsk) || !jsonData.getBigDecimal("market_bid").equals(btcjpyBid)) {
                btcjpyAsk = jsonData.getBigDecimal("market_ask");
                btcjpyBid = jsonData.getBigDecimal("market_bid");

                System.out.println("Currency pair: [BTC/JPY]    " +
                        "Market ask: [" + btcjpyAsk + "]    " +
                        "Market bid: [" + btcjpyBid + "]    " +
                        "Change time [" + new Date().toString() + "] ");
            }
        }
    }
}
