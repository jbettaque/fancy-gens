package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class BreakGeneratorEvent implements Listener {
    Plugin plugin;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public BreakGeneratorEvent(Plugin plugin, Dao<PlacedGenerator, Integer> placedGeneratorDao, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.plugin = plugin;
        this.placedGeneratorDao = placedGeneratorDao;
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PersistentDataContainer container = new CustomBlockData(block, plugin);
        NamespacedKey key = new NamespacedKey(plugin, "generator");
        if (container.has(key, PersistentDataType.INTEGER)){
            Integer genId = container.get(key, PersistentDataType.INTEGER);
            try {
                PlacedGenerator brokenGen =  this.placedGeneratorDao.queryForId(genId);
                placedGeneratorDao.delete(brokenGen);
                GeneratorPlayer genPlayer = generatorPlayerDao.queryForId(brokenGen.getOwner().toString());
                genPlayer.decrementUsedGens();
                generatorPlayerDao.update(genPlayer);

                player.sendMessage("Broke Gen! Used slots: " + genPlayer.getUsedGens());
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
