package crypto.bot.modes;

import crypto.bot.system.ConfigSetup;
import crypto.bot.trading.BuySell;
import crypto.bot.trading.LocalAccount;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

@Slf4j
@Service
@RequiredArgsConstructor
public final class Live {
    public static String API_KEY;
    public static String API_SECRET;
    public static String CURRENCY;


    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        try {
            while (API_KEY == null && API_SECRET == null) {
                log.info("reading credentials");
                readCredentials();
                Thread.sleep(10 * 1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ConfigSetup.readConfig();
        LocalAccount localAccount = new LocalAccount(API_KEY, API_SECRET);
        BuySell.setAccount(localAccount);
    }

    @SneakyThrows
    private static void readCredentials(){
        File file = new File("credentials.txt");
        if (!file.exists()) {
            System.out.println("No config file detected!");
            return;
        }
        try (FileReader reader = new FileReader(file);
             BufferedReader br = new BufferedReader(reader)) {
            String line;
            boolean read = true;
            while ((line = br.readLine()) != null) {
                if (read){
                    API_KEY = line;
                    read = false;
                    continue;
                }
                API_SECRET = line;
                return;
            }
        }
    }

}
