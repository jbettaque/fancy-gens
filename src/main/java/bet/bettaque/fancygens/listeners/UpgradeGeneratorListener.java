package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
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
import redempt.redlib.commandmanager.Messages;

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
                    updateGenerator(placedGenerator);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }

            }
        }
    }

    public double updateGenerator(PlacedGenerator placedGenerator){
        Block block = placedGenerator.getLocation().getBlock();
        Player player = Bukkit.getPlayer(placedGenerator.getOwner());

        PersistentDataContainer container = new CustomBlockData(block, plugin);
        NamespacedKey key = new NamespacedKey(plugin, "generator");

        if (container.has(key, PersistentDataType.INTEGER)){
            int genId = container.get(key, PersistentDataType.INTEGER);

            try {
                GenConfig genConfig = placedGenerator.upgradeGenerator();
                if (econ.getBalance(player) - GensConfig.gens.get(placedGenerator.getGenerator().id).getCost() >= 0){
                    if (genConfig == null) {
                        player.sendMessage(Messages.msg("maxUpgrade"));
                        return 0;
                    }
                    placedGeneratorDao.update(placedGenerator);
                    block.setType(genConfig.block);
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                    player.spawnParticle(Particle.VILLAGER_HAPPY, block.getLocation(), 20, 0.5, 0.5, 0.5, 0.4);
                    econ.withdrawPlayer(player, GensConfig.gens.get(placedGenerator.getGenerator().id).getCost());
                    return GensConfig.gens.get(placedGenerator.getGenerator().id).getCost();
                } else {
                    player.sendMessage(Messages.msg("notEnoughMoney") + " " + TextHelper.formatCurrency(econ.getBalance(player), player) + " / " + TextHelper.formatCurrency(GensConfig.gens.get(placedGenerator.getGenerator().id).getCost(), player));
                    return 0;
                }

            } catch (SQLException throwables) {
                throwables.printStackTrace();
                return  0;
            }

        }
        return 0;
    }
}
