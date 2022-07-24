package crypto.bot.entity;

import crypto.bot.enums.Strategy;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "trades")
public class Trade {

    @Id
    @Column(unique = true, nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;

    private Double profit;

    private Integer longRSI;

    private Integer shortRSI;

    private Double stop;

    private Strategy strategy;

}
