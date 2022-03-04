package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;

import java.sql.SQLException;

public class PlaceGeneratorListener implements Listener {
    Plugin plugin;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;

    public PlaceGeneratorListener(Plugin plugin, Dao<GeneratorPlayer, String> generatorPlayerDao, Dao<PlacedGenerator, Integer> placedGeneratorDao) {
        this.plugin = plugin;
        this.generatorPlayerDao = generatorPlayerDao;
        this.placedGeneratorDao = placedGeneratorDao;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemInHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        PersistentDataContainer tagContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "generator");
        Block block = event.getBlock();

        if (tagContainer.has(key)){
            try {
                GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
                if (generatorPlayer.getUsedGens() < generatorPlayer.getMaxGens()){
                    generatorPlayer.incrementUsedGens();
                    generatorPlayerDao.update(generatorPlayer);
                    player.sendMessage("You have placed a generator! Used slots: " + generatorPlayer.getUsedGens());


                    PlacedGenerator placedGenerator = new PlacedGenerator(block.getLocation(), tagContainer.get(key, PersistentDataType.STRING));
                    placedGeneratorDao.create(placedGenerator);


                } else {
                    player.sendMessage("You too many gens! Used slots: " + generatorPlayer.getUsedGens() + " " + generatorPlayer.getMaxGens());
                    event.setCancelled(true);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
