package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
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
//        player.sendMessage("10000: " + TextHelper.formatCurrency(10000, player));
//        player.sendMessage("10000000: " + TextHelper.formatCurrency(10000000, player));
//        player.sendMessage("10000000000: " + TextHelper.formatCurrency(10000000000d, player));
//        player.sendMessage("10000000000000: " + TextHelper.formatCurrency(10000000000000d, player));
//        player.sendMessage("10000000000000000: " + TextHelper.formatCurrency(10000000000000000d, player));
//        player.sendMessage("10000000000000000000: " + TextHelper.formatCurrency(10000000000000000000d, player));
//        player.sendMessage("10000000000000000000000: " + TextHelper.formatCurrency(10000000000000000000000d, player));
//        player.sendMessage("10000000000000000000000000: " + TextHelper.formatCurrency(10000000000000000000000000d, player));
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (generatorPlayer == null){
                generatorPlayer = new GeneratorPlayer(player.getUniqueId().toString(), 20, 0);
                this.generatorPlayerDao.create(generatorPlayer);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
