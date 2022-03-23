package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.commands.AdminCommands;
import bet.bettaque.fancygens.db.PlacedAutosellChest;
import bet.bettaque.fancygens.db.PlacedUpgradableChest;
import bet.bettaque.fancygens.gui.UpgradableChestGui;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.Messages;

import java.sql.SQLException;

public class UpgradableChestListener implements Listener {
    Plugin plugin;
    Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao;
    AdminCommands adminCommands;

    public UpgradableChestListener(Plugin plugin, Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao, AdminCommands adminCommands) {
        this.plugin = plugin;
        this.placedUpgradableChestDao = placedUpgradableChestDao;
        this.adminCommands = adminCommands;
    }

    @EventHandler
    public void onUpgradableChestPlace(BlockPlaceEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlockPlaced();
        ItemStack item = e.getItemInHand();
        ItemMeta itemMeta = item.getItemMeta();
        PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        NamespacedKey key = new NamespacedKey(plugin, "upgradableChest");
        if (container.has(key, PersistentDataType.INTEGER)){
            int level = container.get(key, PersistentDataType.INTEGER);
            PlacedUpgradableChest placedUpgradableChest = new PlacedUpgradableChest(block.getLocation(), player.getUniqueId(), level);
            try {
                placedUpgradableChestDao.create(placedUpgradableChest);
                PersistentDataContainer blockContainer = new CustomBlockData(e.getBlock(), plugin);
                blockContainer.set(key, PersistentDataType.INTEGER, placedUpgradableChest.getId());
                player.sendMessage(TextHelper.parseFancyString("&green&Placed a &#2196F3-#FFEB3B&Upgradable Chest"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    @EventHandler
    public void onUpgradableChestBrake(BlockBreakEvent e){
        Player player = e.getPlayer();
        Block block = e.getBlock();
        PersistentDataContainer container = new CustomBlockData(block, plugin);
        NamespacedKey key = new NamespacedKey(plugin, "upgradableChest");
        if (container.has(key, PersistentDataType.INTEGER)){
            try {
                PlacedUpgradableChest placedUpgradableChest = placedUpgradableChestDao.queryForId(container.get(key, PersistentDataType.INTEGER));
                adminCommands.giveUpgradableChestBackend(player,placedUpgradableChest.getLevel());
                e.setCancelled(true);
                block.setType(Material.AIR);
                container.remove(key);
                placedUpgradableChestDao.delete(placedUpgradableChest);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }

    @EventHandler
    public void onUpgradableChestOpen(InventoryOpenEvent e){
//        if (e.getClickedBlock().getState() instanceof Chest)
        if (e.getInventory().getHolder() instanceof Chest){
            Chest chest = (Chest) e.getInventory().getHolder();
            Block block = e.getPlayer().getWorld().getBlockAt(chest.getLocation());
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            NamespacedKey key = new NamespacedKey(plugin, "upgradableChest");
            if (container.has(key, PersistentDataType.INTEGER)){
                e.setCancelled(true);
                try {
                    PlacedUpgradableChest placedUpgradableChest = placedUpgradableChestDao.queryForId(container.get(key, PersistentDataType.INTEGER));
                    UpgradableChestGui upgradableChestGui = new UpgradableChestGui(TextHelper.parseFancyString("&#2196F3-#FFEB3B&Upgradable Chest"),27, (Player) e.getPlayer(),placedUpgradableChest,placedUpgradableChestDao);
                    upgradableChestGui.populate();


                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

            }


        }
    }


}
