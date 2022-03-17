package crypto.bot.data;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Credentials {
    private String key;
    private String secret;
}
