package bet.bettaque.fancygens.services;

import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.blockdata.BlockDataManager;
import redempt.redlib.blockdata.DataBlock;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;
import java.util.Random;

public class GeneratorHandler {
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    Plugin plugin;

    public GeneratorHandler(Dao<PlacedGenerator, Integer> placedGeneratorDao, Plugin plugin) {
        this.placedGeneratorDao = placedGeneratorDao;
        this.plugin = plugin;
    }

    public void generateResources(){
        try {
            for (PlacedGenerator placedGenerator : placedGeneratorDao.queryForAll()) {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(placedGenerator.getOwner());
                if (!offlinePlayer.isOnline()) continue;
                Location location = placedGenerator.getLocation();

                World world = location.getWorld();

                Location newLocation = new Location(world,location.getX()+.5d,location.getY()+1.2d,location.getZ()+.5d);


                if (world.getBlockAt(newLocation).getType() != Material.AIR || world.getBlockAt(newLocation).getType() != Material.WATER){
                    for (int x=-1; x<=1 ;x++){
                        for(int z=-1; z<=1 ;z++){
                            if (world.getBlockAt(location.add(x,0,z)).getType() != Material.AIR || world.getBlockAt(location.add(x,0,z)).getType() != Material.WATER) continue;
                                else {
                                   newLocation =  newLocation.add(x+.5d,.5d,z+.5d);
                            }


                        }
                    }
                }

                Player player = Bukkit.getPlayer(placedGenerator.getOwner());

                ItemStack item = new ItemBuilder(placedGenerator.getMaterial());
                NamespacedKey key = new NamespacedKey(plugin, "sellable");
                double price = placedGenerator.getProductPrice();
                ItemUtils.addPersistentTag(item, key, PersistentDataType.DOUBLE, price);

                ItemUtils.addLore(item,"Price: " + TextHelper.formatCurrency(price, player));
                if (location.getChunk().getEntities().length < 1000){
                    world.dropItem(newLocation, item);
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,TextHelper.parseFancyComponents("&red&There are too many Items already in your Plot. Generators not generating!"));
                }
            }
        }catch (SQLException throwables) {
            throwables.printStackTrace();
            try {
                this.placedGeneratorDao.executeRaw("ALTER TABLE `placed_generators` ADD COLUMN boost INTEGER");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
