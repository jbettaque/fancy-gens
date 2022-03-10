package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.commands.AdminCommands;
import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.db.PlacedAutosellChest;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class BreakAutosellChestListener implements Listener {
    Plugin plugin;
    AdminCommands adminCommands;
    Dao<PlacedAutosellChest, Integer> placedAutosellChestDao;

    public BreakAutosellChestListener(Plugin plugin, AdminCommands adminCommands, Dao<PlacedAutosellChest, Integer> placedAutosellChestDao) {
        this.plugin = plugin;
        this.adminCommands = adminCommands;
        this.placedAutosellChestDao = placedAutosellChestDao;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        PersistentDataContainer container = new CustomBlockData(block, plugin);
        NamespacedKey key = new NamespacedKey(plugin, "autosellchest");
        if (container.has(key, PersistentDataType.INTEGER)){
            try {
                PlacedAutosellChest placedAutosellChest = placedAutosellChestDao.queryForId(container.get(key, PersistentDataType.INTEGER));
                adminCommands.giveAutosellChestBackend(player, (int) placedAutosellChest.getMultiplier());
                event.setCancelled(true);
                block.setType(Material.AIR);
                container.remove(key);
                placedAutosellChestDao.delete(placedAutosellChest);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }


    }
}
