package crypto.bot.system;

import crypto.bot.modes.Live;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.Currency;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
                    case "Currencies to track":
                        currencies = List.of(arr[1].toUpperCase().split(", "));
                        break;
                    case "Leverage":
                        BuySell.LEVERAGE = Integer.parseInt(arr[1]);
                        break;
                    case "FIAT":
                        fiat = arr[1].toUpperCase();
                        break;
                    case "test":
                        BuySell.TEST = Boolean.valueOf(arr[1]);
                        break;
                    case "Main currency":
                        Live.CURRENCY = arr[1].toUpperCase();
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
