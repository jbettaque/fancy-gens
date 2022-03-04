package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class FirstJoinListener implements Listener {
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public FirstJoinListener(Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (generatorPlayer == null){
                player.sendMessage("New Player! Making new entry in Database!");
                generatorPlayer = new GeneratorPlayer(player.getUniqueId().toString(), 10000, 0);
                this.generatorPlayerDao.create(generatorPlayer);
            }
            player.sendMessage(String.valueOf(generatorPlayer.getMaxGens()));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
