package crypto.bot.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import crypto.bot.data.Credentials;
import crypto.bot.modes.Live;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("api")
public class UserRestController {
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
}
