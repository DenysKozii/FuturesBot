package crypto.bot.dto;

import lombok.Data;

@Data
public class TradeDto {

    private String symbol;

    private Double profit;

    private Integer longRSI;

    private Integer shortRSI;

    private Double stop;

}
