package bet.bettaque.fancygens.services;

import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.db.PlacedAutosellChest;
import bet.bettaque.fancygens.db.PlacedGenerator;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import redempt.redlib.itemutils.ItemBuilder;

import java.sql.SQLException;

public class AutosellChestHandler {
    Dao<PlacedAutosellChest, Integer> placedAutosellChestDao;
    ShopCommands shopCommands;

    public AutosellChestHandler(Dao<PlacedAutosellChest, Integer> placedAutosellChestDao, ShopCommands shopCommands) {
        this.placedAutosellChestDao = placedAutosellChestDao;
        this.shopCommands = shopCommands;
    }

    public void handleAutosellChests(){
        try {
            for (PlacedAutosellChest placedAutosellChest : placedAutosellChestDao.queryForAll()) {
                Location chestLocation = placedAutosellChest.getLocation();
                Block chestBlock = chestLocation.getWorld().getBlockAt(chestLocation);
                BlockState state = chestBlock.getState();
                if (state instanceof Chest) {
                    Chest chest = (Chest) chestBlock.getState();
                    Player owner = Bukkit.getPlayer(placedAutosellChest.getOwner());
                    if (owner != null){
                        placedAutosellChest.addSell(shopCommands.sellAllBackend(owner, chest.getInventory(), placedAutosellChest.getMultiplier(), true));
                        placedAutosellChestDao.update(placedAutosellChest);
                    }
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
