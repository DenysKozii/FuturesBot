package crypto.bot.controllers;

import crypto.bot.data.Credentials;
import crypto.bot.entity.Trade;
import crypto.bot.modes.Live;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class UserRestController {

    private final Live live;

    @GetMapping
    public String ping() {
        return "pong";
    }

    @PostMapping("credentials")
    public String credentials(@RequestBody Credentials credentials) {
        Live.apiKey = credentials.getKey();
        Live.apiSecret = credentials.getSecret();
        return "credentials injected";
    }

    @GetMapping("profit")
    public List<Trade> getProfit() {
        return live.getTrades();
    }

    @DeleteMapping("clean")
    public String clean() {
        live.clean();
        return "database is clean";
    }
}
