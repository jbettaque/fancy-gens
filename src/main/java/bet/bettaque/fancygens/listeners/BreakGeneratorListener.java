package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.flags.types.LandFlag;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;

public class BreakGeneratorListener implements Listener {
    Plugin plugin;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    LandsIntegration landsIntegration;

    public BreakGeneratorListener(Plugin plugin, Dao<PlacedGenerator, Integer> placedGeneratorDao, Dao<GeneratorPlayer, String> generatorPlayerDao, LandsIntegration landsIntegration) {
        this.plugin = plugin;
        this.placedGeneratorDao = placedGeneratorDao;
        this.generatorPlayerDao = generatorPlayerDao;
        this.landsIntegration = landsIntegration;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        Location location = event.getBlock().getLocation();
        Player player = event.getPlayer();
        Block block = event.getBlock();



        PersistentDataContainer container = new CustomBlockData(block, plugin);
        NamespacedKey key = new NamespacedKey(plugin, "generator");
        NamespacedKey boostKey = new NamespacedKey(plugin, "generator_boost");
        if (container.has(key, PersistentDataType.INTEGER)){
            Integer genId = container.get(key, PersistentDataType.INTEGER);
            try {
                Area area = landsIntegration.getArea(location);
                if (area != null){
                    if(area.hasFlag(player.getUniqueId(), Flags.BLOCK_BREAK)){
                        PlacedGenerator brokenGen =  this.placedGeneratorDao.queryForId(genId);
                        if (brokenGen != null){
                            placedGeneratorDao.delete(brokenGen);
                            GeneratorPlayer genPlayer = generatorPlayerDao.queryForId(brokenGen.getOwner().toString());
                            genPlayer.decrementUsedGens();
                            generatorPlayerDao.update(genPlayer);

                            event.setCancelled(true);

                            block.setType(Material.AIR);

                            ItemStack gen = new ItemBuilder(brokenGen.getGenerator().block)
                                    .setName(brokenGen.getGenerator().name)
                                    .addLore(TextHelper.parseFancyString("&gray&Tier: &yellow&" + brokenGen.getGenerator().id))
                                    .addLore(TextHelper.parseFancyString("&red&Boost: " + brokenGen.getBoost()))
                                    .addPersistentTag(key, PersistentDataType.INTEGER, brokenGen.getGenerator().id)
                                    .addPersistentTag(boostKey, PersistentDataType.INTEGER, brokenGen.getBoost());
                            ItemUtils.addEnchant(gen, Enchantment.DURABILITY, 1);
                            ItemUtils.addItemFlags(gen, ItemFlag.HIDE_ENCHANTS);
                            ItemUtils.give(player, gen);

                            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 3);
                            player.spawnParticle(Particle.EXPLOSION_NORMAL, block.getLocation(), 7, 0, 0, 0, 0.25);
                            CMIHologram hologram = CMI.getInstance().getHologramManager().getByName(String.valueOf(brokenGen.getId()));
                            if (hologram != null) hologram.remove();
                        }
                        container.remove(key);
                    }
                }


//                player.sendMessage(Messages.msg("brokeGen"));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }
    }
}
