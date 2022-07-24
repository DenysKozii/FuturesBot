package crypto.bot.repository;

import crypto.bot.entity.Trade;
import crypto.bot.enums.Strategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    Optional<Trade> findBySymbolAndLongRSIAndShortRSIAndStopAndStrategy(String symbol, Integer longRSI, Integer shortRSI, Double stop, Strategy strategy);

}
