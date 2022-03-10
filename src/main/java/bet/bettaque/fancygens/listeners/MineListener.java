package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.ItemHelper;
import bet.bettaque.fancygens.helpers.PersistanceHelper;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;
import java.util.Random;

public class MineListener implements Listener {
    Plugin plugin;
    Economy econ;
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public MineListener(Plugin plugin, Economy econ, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.plugin = plugin;
        this.econ = econ;
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @EventHandler
    public void onStartMining(PlayerInteractEvent event){
        if (event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
            Block block = event.getClickedBlock();
            Player player = event.getPlayer();
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            if (container.has(PersistanceHelper.oreKey, PersistentDataType.INTEGER)) {
                if (container.get(PersistanceHelper.oreKey, PersistentDataType.INTEGER) == 1){

                    if (block.getDrops(player.getInventory().getItemInMainHand()).isEmpty()){
                       return;
                    }
                    try {
                        GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());

                        Random random = new Random();

                        if (random.nextInt(1000) == 1){
                            int specialPrice = random.nextInt(5) +1;
                            generatorPlayer.addGems(specialPrice);
                            generatorPlayerDao.update(generatorPlayer);
                            BaseComponent[] extraMessage = new MineDown("&#4CAF50-#CDDC39&Special Price! " + TextHelper.formatGems(specialPrice, player)).toComponent();
                            player.spigot().sendMessage(extraMessage);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                        }

                        if (random.nextInt(2000) == 1){
                            int specialPrice = random.nextInt(2) +1;
                            generatorPlayer.addMaxGens(specialPrice);
                            generatorPlayerDao.update(generatorPlayer);
                            BaseComponent[] extraMessage = new MineDown("&#009688-#FFEE58&Special Price! " + TextHelper.formatSlots(specialPrice, player)).toComponent();
                            player.spigot().sendMessage(extraMessage);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                        }

                        if (random.nextInt(4000) == 1){
                            double specialPrice = (random.nextInt(5) +1) / 320d;
                            generatorPlayer.incrementMultiplier(specialPrice);
                            generatorPlayerDao.update(generatorPlayer);
                            BaseComponent[] extraMessage = new MineDown("&#EC407A-#E91E63&Special Price! " + TextHelper.formatMultiplier(specialPrice, true, player)).toComponent();
                            player.spigot().sendMessage(extraMessage);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 1);
                        }

                        double reward = GensConfig.miningItems.get(block.getType());
                        double pointsMulti = (generatorPlayer.getScore() / 100000) + 1;
                        reward = reward * pointsMulti;
                        reward = reward * generatorPlayer.getMultiplier();
                        double rewardOverTime = PersistanceHelper.addMineGain(player, reward);
                        String message = ChatColor.YELLOW + " + " + TextHelper.formatCurrency(rewardOverTime, player);
                        player.spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent(message));
                        generatorPlayer.incrementScore(reward);
                        generatorPlayerDao.update(generatorPlayer);
                        econ.depositPlayer(player, reward);
                        container.remove(PersistanceHelper.oreKey);
                        block.setType(Material.AIR);
                        player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_STEP, 0.7f, 1);
                        player.spawnParticle(Particle.EXPLOSION_NORMAL, block.getLocation(), 7, 0, 0, 0, 0.25);

                        if (random.nextInt(7) == 1){
                            ItemHelper.damageAndCheckRemove(event.getItem(),1, player);
                        }
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }


                }
            }

        }
    }

    @EventHandler
    public void onWierdlyBrokeBlock(BlockBreakEvent event){
        Block block = event.getBlock();
        PersistentDataContainer container = new CustomBlockData(block, plugin);
        if (container.has(PersistanceHelper.oreKey, PersistentDataType.INTEGER)) {
            if (container.get(PersistanceHelper.oreKey, PersistentDataType.INTEGER) == 1) {
                event.setCancelled(true);
            }
        }
    }
}
