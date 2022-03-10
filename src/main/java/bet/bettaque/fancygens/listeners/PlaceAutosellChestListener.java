package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.PlacedAutosellChest;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.Messages;

import java.sql.SQLException;

public class PlaceAutosellChestListener implements Listener {
    Plugin plugin;
    Dao<PlacedAutosellChest, Integer> placedAutosellChestDao;

    public PlaceAutosellChestListener(Plugin plugin, Dao<PlacedAutosellChest, Integer> placedAutosellChestDao) {
        this.plugin = plugin;
        this.placedAutosellChestDao = placedAutosellChestDao;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        ItemStack item = event.getItemInHand();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "autosellchest");
        if (container.has(key, PersistentDataType.INTEGER)){
            int multiplier = container.get(key, PersistentDataType.INTEGER);
            PlacedAutosellChest placedAutosellChest = new PlacedAutosellChest(block.getLocation(), player.getUniqueId(), multiplier);
            try {
                placedAutosellChestDao.create(placedAutosellChest);
                PersistentDataContainer blockContainer = new CustomBlockData(event.getBlock(), plugin);
                blockContainer.set(key, PersistentDataType.INTEGER, placedAutosellChest.getId());
                player.sendMessage(Messages.msg("placedAutosellChest"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }


    }
}
