package crypto.bot.controllers;

import crypto.bot.dto.CredentialsDto;
import crypto.bot.entity.Credentials;
import crypto.bot.entity.Trade;
import crypto.bot.modes.Live;
import crypto.bot.repository.CredentialsRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class UserRestController {

    private final Live live;
    private final CredentialsRepository credentialsRepository;

    @GetMapping
    public String ping() {
        return "pong";
    }

    @PostMapping("credentials")
    public String credentials(@RequestBody CredentialsDto credentialsDto) {
        Live.apiKey = credentialsDto.getKey();
        Live.apiSecret = credentialsDto.getSecret();
        Credentials credentials = new Credentials();
        credentials.setKey(credentialsDto.getKey());
        credentials.setSecret(credentialsDto.getSecret());
        credentialsRepository.save(credentials);
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
