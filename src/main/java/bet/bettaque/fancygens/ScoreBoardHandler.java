package bet.bettaque.fancygens;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.ScoreHelper;
import bet.bettaque.fancygens.listeners.ScoreBoardListener;
import com.j256.ormlite.dao.Dao;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class ScoreBoardHandler {
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    Economy econ;

    public ScoreBoardHandler(Dao<GeneratorPlayer, String> generatorPlayerDao, Economy econ) {
        this.generatorPlayerDao = generatorPlayerDao;
        this.econ = econ;
    }

    public void updateScoreBoard(Player player){
        if (ScoreHelper.hasScore(player)){
            ScoreHelper scoreHelper = ScoreHelper.getByPlayer(player);

            try {
                GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
                ScoreBoardListener.setScoreboard(player, scoreHelper, generatorPlayer, econ);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
