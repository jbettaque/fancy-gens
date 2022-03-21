package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.FancyResource;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.ItemHelper;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.helpers.PersistanceHelper;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.query.In;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
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

import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;

public class UpgradeWandListener implements Listener {
    Plugin plugin;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    UpgradeGeneratorListener upgradeGeneratorListener;
    FancyEconomy economy;

    public UpgradeWandListener(Plugin plugin, Dao<PlacedGenerator, Integer> placedGeneratorDao, UpgradeGeneratorListener upgradeGeneratorListener, FancyEconomy economy) {
        this.plugin = plugin;
        this.placedGeneratorDao = placedGeneratorDao;
        this.upgradeGeneratorListener = upgradeGeneratorListener;
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        ItemStack sellwand = player.getInventory().getItemInMainHand();
        if(sellwand.getType() == Material.DIAMOND_SHOVEL && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)){
            ItemMeta itemMeta = sellwand.getItemMeta();
            NamespacedKey key = new NamespacedKey(plugin, "upgradewand");
            PersistentDataContainer container = itemMeta.getPersistentDataContainer();

            if (container.has(key, PersistentDataType.INTEGER)){
                if (PersistanceHelper.isPlayerUpgrading(player)) return;
                if (event.getHand() == EquipmentSlot.HAND){
                    QueryBuilder<PlacedGenerator, Integer> queryBuilder = placedGeneratorDao.queryBuilder();
                    try {
                        List<PlacedGenerator> ownedGens = placedGeneratorDao.query(queryBuilder.where().eq("owner", player.getUniqueId()).prepare());
                        ownedGens.sort(Comparator.comparing(PlacedGenerator::getGeneratorId));

                        int count = 0;
                        double total = 0;
                        int fixedId = 0;
                        for (PlacedGenerator pg: ownedGens) {
                            double cost = pg.getUpgradeCost();
                            if (total + cost <= economy.getBalance(player, FancyResource.COINS)){


                                if(player.isSneaking()){
                                    if (fixedId == 0){
                                        fixedId = pg.getGeneratorId();
                                    } else {
                                        if(fixedId != pg.getGeneratorId()){
                                            continue;
                                        }
                                    }
                                }
                                if (PersistanceHelper.updateGenerator(pg)){
                                    count++;
                                    total += cost;
                                }
                            }
                        }
                        if (count > 0) {
                            player.sendMessage(ChatColor.GREEN + "Upgraded " + ChatColor.YELLOW + count + ChatColor.GREEN + " generators to for a price of " + TextHelper.formatCurrency(total, player));
                        } else {
                            player.spigot().sendMessage(TextHelper.parseFancyComponents("&red&You don't have enough money to upgrade any generators!"));
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
