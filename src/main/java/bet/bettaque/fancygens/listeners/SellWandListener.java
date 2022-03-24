package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.ItemHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.TileState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.itemutils.ItemUtils;

import java.util.List;
import java.util.Map;

public class SellWandListener implements Listener {
    Plugin plugin;
    ShopCommands shopCommands;
    LandsIntegration landsIntegration;

    public SellWandListener(Plugin plugin, ShopCommands shopCommands, LandsIntegration landsIntegration) {
        this.plugin = plugin;
        this.shopCommands = shopCommands;
        this.landsIntegration = landsIntegration;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        ItemStack sellwand = player.getInventory().getItemInMainHand();
        if (sellwand.getType() == Material.DIAMOND_SHOVEL) {
            ItemMeta itemMeta = sellwand.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "sellwand");
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();
            if (container.has(key, PersistentDataType.DOUBLE)) {
                Area area = landsIntegration.getArea(block.getLocation());
                if (area != null) {
                    if (area.hasFlag(player.getUniqueId(), Flags.BLOCK_BREAK)) {
                        if (action == Action.RIGHT_CLICK_BLOCK && block.getState() instanceof Container && player.isSneaking() && event.getHand() == EquipmentSlot.HAND) {
                            Container state = (Container) block.getState();
                            shopCommands.sellAllBackend(player, state.getInventory(), container.get(key, PersistentDataType.DOUBLE), true);
                            ItemHelper.damageAndCheckRemove(sellwand, 1, player);

                        }
                        event.setCancelled(true);
                    }

                }

            }
        }
    }
}
