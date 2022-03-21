package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.math.BigInteger;
import java.sql.SQLException;

public class FirstJoinListener implements Listener {
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public FirstJoinListener(Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        BigInteger thousand = BigInteger.valueOf(1000L);
//        for (int i = 50; i > 0; i--)
//        {
//            thousand = thousand.multiply(BigInteger.valueOf(i));
//            Bukkit.broadcastMessage(TextHelper.formatCurrency(thousand.longValue(), event.getPlayer()));
//        }
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
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),"oraxen pack send" + player.getName());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                this.generatorPlayerDao.executeRaw("ALTER TABLE `players` ADD COLUMN coins VARCHAR");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
