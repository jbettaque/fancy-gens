package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.PlacedGenerator;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class UpgradeGeneratorListener implements Listener {
    Plugin plugin;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    Economy econ;

    public UpgradeGeneratorListener(Plugin plugin, Dao<PlacedGenerator, Integer> placedGeneratorDao, Economy econ) {
        this.plugin = plugin;
        this.placedGeneratorDao = placedGeneratorDao;
        this.econ = econ;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK && player.isSneaking() && event.getHand() == EquipmentSlot.HAND){
            Block block = event.getClickedBlock();
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            NamespacedKey key = new NamespacedKey(plugin, "generator");

            if (container.has(key, PersistentDataType.INTEGER)){
                int genId = container.get(key, PersistentDataType.INTEGER);

                try {
                    PlacedGenerator placedGenerator = this.placedGeneratorDao.queryForId(genId);
                    GenConfig genConfig = placedGenerator.upgradeGenerator();
                    player.sendMessage(String.valueOf(placedGenerator.getGenerator().id));
                    if (econ.getBalance(player) - GensConfig.gens.get(placedGenerator.getGenerator().id).cost >= 0){
                        if (genConfig == null) {
                            player.sendMessage("Max upgrade!");
                            return;
                        }
                        placedGeneratorDao.update(placedGenerator);
                        block.setType(genConfig.block);
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                        econ.withdrawPlayer(player, GensConfig.gens.get(placedGenerator.getGenerator().id).cost);
                        player.sendMessage("upgrading generator! New balance: " + econ.getBalance(player));
                    } else {
                        player.sendMessage("not enough money!");
                    }

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }
    }
}
