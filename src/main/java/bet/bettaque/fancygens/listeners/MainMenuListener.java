package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.commands.UiCommands;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.itemutils.ItemUtils;

import java.util.Objects;

public class MainMenuListener implements Listener {
    Plugin plugin;
    UiCommands uiCommands;

    public MainMenuListener(Plugin plugin, UiCommands uiCommands) {
        this.plugin = plugin;
        this.uiCommands = uiCommands;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        ItemStack mainMenuItem = OraxenItems.getItemById("menu_icon").build();
        NamespacedKey key = new NamespacedKey(plugin, "mainmenu");
        ItemUtils.addPersistentTag(mainMenuItem, key, PersistentDataType.INTEGER, 1);

        int inventoryCount = ItemUtils.count(player.getInventory(), mainMenuItem);
        if (inventoryCount <= 0){
            player.getInventory().setItem(8, mainMenuItem);
//            ItemUtils.give(player, mainMenuItem, 1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        NamespacedKey key = new NamespacedKey(plugin, "mainmenu");
        ItemStack usedItem = event.getItem();
        PersistentDataContainer container = null;
        if (usedItem != null) {
            container = usedItem.getItemMeta().getPersistentDataContainer();
        }
        if (container != null && container.has(key, PersistentDataType.INTEGER)) {
            uiCommands.openMenu(player);
        }
    }

    @EventHandler
    public void onInventoryMove(InventoryClickEvent event){
        NamespacedKey key = new NamespacedKey(plugin, "mainmenu");
        ItemStack usedItem = event.getCurrentItem();
        if(usedItem != null) {
            ItemMeta itemMeta = usedItem.getItemMeta();
            if (itemMeta != null){
                PersistentDataContainer container = itemMeta.getPersistentDataContainer();
                if (container.has(key, PersistentDataType.INTEGER)){
                    event.setCancelled(true);
                } else {
                    ItemStack menuRef = OraxenItems.getItemById("menu_icon").build();
                    ItemUtils.addPersistentTag(menuRef, key, PersistentDataType.INTEGER, 1);

                    int slot = event.getInventory().first(menuRef);
                    if (slot > -1  && slot != 8){
                        event.getInventory().remove(menuRef);
                        event.getInventory().setItem(8, menuRef);
                    }
                }
            }
        }



    }
}
