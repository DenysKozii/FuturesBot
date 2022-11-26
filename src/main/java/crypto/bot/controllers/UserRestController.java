package crypto.bot.controllers;

import crypto.bot.dto.CredentialsDto;
import crypto.bot.modes.Live;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedWriter;
import java.io.FileWriter;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1")
public class UserRestController {

    private final Live live;

    @GetMapping
    public String ping() {
        return "pong";
    }

    @PostMapping("credentials")
    public String credentials(@RequestBody CredentialsDto credentialsDto) {
        Live.API_KEY = credentialsDto.getKey();
        Live.API_SECRET = credentialsDto.getSecret();
        live.init();
        writeCredentials(credentialsDto);
        return "credentials injected";
    }

    @SneakyThrows
    private void writeCredentials(CredentialsDto credentialsDto) {
        BufferedWriter writer = new BufferedWriter(new FileWriter("credentials.txt"));
        writer.write(credentialsDto.getKey());
        writer.newLine();
        writer.write(credentialsDto.getSecret());
        writer.close();
    }
}
