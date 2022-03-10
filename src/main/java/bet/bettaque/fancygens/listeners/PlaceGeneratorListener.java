package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.TileState;
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
import redempt.redlib.commandmanager.Messages;

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


        if (tagContainer.has(key, PersistentDataType.INTEGER)){
            try {
                GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
                if (generatorPlayer.getUsedGens() < generatorPlayer.getMaxGens()){
                    generatorPlayer.incrementUsedGens();
                    generatorPlayerDao.update(generatorPlayer);
//                    player.sendMessage(Messages.msg("genPlaced"));

                    PlacedGenerator placedGenerator = new PlacedGenerator(block.getLocation(), tagContainer.get(key, PersistentDataType.INTEGER), player.getUniqueId());
                    placedGeneratorDao.create(placedGenerator);

                    PersistentDataContainer blockContainer = new CustomBlockData(block, plugin);
                    blockContainer.set(key, PersistentDataType.INTEGER, placedGenerator.getId());

                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 2);
                    player.spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 20, 0.5, 0.5, 0.5, 0.4);


                } else {
                    player.sendMessage(Messages.msg("tooManyGens") + " " + Messages.msg("usedSlots") + generatorPlayer.getUsedGens() + " / " + generatorPlayer.getMaxGens());
                    event.setCancelled(true);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
