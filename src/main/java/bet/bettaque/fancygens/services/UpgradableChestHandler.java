package bet.bettaque.fancygens.services;

import bet.bettaque.fancygens.db.PlacedAutosellChest;
import bet.bettaque.fancygens.db.PlacedUpgradableChest;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import redempt.redlib.itemutils.ItemUtils;

import java.math.BigInteger;
import java.sql.SQLException;

public class UpgradableChestHandler {
    Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao;

    public UpgradableChestHandler(Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao) {
        this.placedUpgradableChestDao = placedUpgradableChestDao;
    }

    public void handleUpgradableChests(){
        try {
            for (PlacedUpgradableChest placedUpgradableChest : placedUpgradableChestDao.queryForAll()) {
                Location chestLocation = placedUpgradableChest.getLocation();
                Block chestBlock = chestLocation.getWorld().getBlockAt(chestLocation);
                BlockState state = chestBlock.getState();
                if (state instanceof Chest) {
                    Chest chest = (Chest) chestBlock.getState();
                    Player owner = Bukkit.getPlayer(placedUpgradableChest.getOwner());
                    for (ItemStack itemStack : chest.getInventory().getContents()){
                        if (itemStack != null){
                            placedUpgradableChest.addItem(itemStack);
                            ItemUtils.countAndRemove(chest.getInventory(),itemStack);
                        }

                    }

                    placedUpgradableChestDao.update(placedUpgradableChest);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
