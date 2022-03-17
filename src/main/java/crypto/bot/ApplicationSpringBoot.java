package crypto.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import crypto.bot.modes.Live;
import crypto.bot.system.ConfigSetup;

@SpringBootApplication
public class ApplicationSpringBoot {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationSpringBoot.class, args);
        ConfigSetup.readConfig();
        Live.init();
    }

}
