package crypto.bot.system;

import crypto.bot.indicators.RSI;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.Currency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConfigSetup {

    private static final StringBuilder setup = new StringBuilder();
    private static List<String> currencies;
    private static String fiat;

    public ConfigSetup() {
        throw new IllegalStateException("Utility class");
    }

    public static List<String> getCurrencies() {
        return currencies;
    }


    public static String getFiat() {
        return fiat;
    }

    public static void readConfig() {
        System.out.println("---Reading config...");
        File file = new File("config.txt");
        if (!file.exists()) {
            System.out.println("No config file detected!");
            System.exit(1);
        }
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isBlank() && !line.isEmpty()) {
                    setup.append(line).append("\n");
                } else {
                    continue;
                }
                String[] arr = line.strip().split(":");
                if (arr.length != 2) continue;
                switch (arr[0]) {
                    case "RSI long waiting":
                        RSI.LONG_WAITING = Integer.parseInt(arr[1]);
                        break;
                    case "RSI long open":
                        RSI.LONG_OPEN = Integer.parseInt(arr[1]);
                        break;
                    case "RSI long close":
                        RSI.LONG_CLOSE = Integer.parseInt(arr[1]);
                        break;
                    case "RSI short waiting":
                        RSI.SHORT_WAITING = Integer.parseInt(arr[1]);
                        break;
                    case "RSI short open":
                        RSI.SHORT_OPEN = Integer.parseInt(arr[1]);
                        break;
                    case "RSI short close":
                        RSI.SHORT_CLOSE = Integer.parseInt(arr[1]);
                        break;
                    case "Currencies to track":
                        currencies = Collections.unmodifiableList(Arrays.asList(arr[1].toUpperCase().split(", ")));
                        break;
                    case "Money per trade":
                        BuySell.MONEY_PER_TRADE = Double.parseDouble(arr[1]);
                        break;
                    case "Money percentage limit":
                        BuySell.MONEY_PERCENTAGE_LIMIT = Double.parseDouble(arr[1]);
                        break;
                    case "Leverage":
                        BuySell.LEVERAGE = Integer.parseInt(arr[1]);
                        break;
                    case "Trailing SL":
                        Currency.TRAILING_SL = Double.parseDouble(arr[1]);
                        break;
                    case "Take profit":
                        Currency.TAKE_PROFIT = Double.parseDouble(arr[1]);
                        break;
                    case "Confluence long open":
                        Currency.CONFLUENCE_LONG_OPEN = Integer.parseInt(arr[1]);
                        break;
                    case "Confluence long close":
                        Currency.CONFLUENCE_LONG_CLOSE = Integer.parseInt(arr[1]);
                        break;
                    case "Confluence long half close":
                        Currency.CONFLUENCE_LONG_HALF_CLOSE = Integer.parseInt(arr[1]);
                        break;
                    case "Confluence short open":
                        Currency.CONFLUENCE_SHORT_OPEN = Integer.parseInt(arr[1]);
                        break;
                    case "Confluence short close":
                        Currency.CONFLUENCE_SHORT_CLOSE = Integer.parseInt(arr[1]);
                        break;
                    case "Confluence short half close":
                        Currency.CONFLUENCE_SHORT_HALF_CLOSE = Integer.parseInt(arr[1]);
                        break;
                    case "FIAT":
                        fiat = arr[1].toUpperCase();
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
