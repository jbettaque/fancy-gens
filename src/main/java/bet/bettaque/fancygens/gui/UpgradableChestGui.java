package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.db.FancyItemStack;
import bet.bettaque.fancygens.db.PlacedUpgradableChest;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Map;

public class UpgradableChestGui extends FancyGui{
    PlacedUpgradableChest upgradableChest;
    Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao;


    public UpgradableChestGui(String title, int size, Player player, PlacedUpgradableChest placedUpgradableChest ,    Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao) {
        super(title, size, player);
        this.upgradableChest = placedUpgradableChest;
        this.placedUpgradableChestDao = placedUpgradableChestDao;
    }

    @Override
    public void populate() {
        for (Map.Entry<FancyItemStack, BigInteger> item:upgradableChest.getStoredItems().entrySet()) {
            BigInteger amount = item.getValue();
            ItemStack itemStack = new ItemBuilder( item.getKey())
                    .addLore(TextHelper.parseFancyString("&grey&Amount: "+amount.toString()))
                    .addLore("")
                    .addLore(TextHelper.parseFancyString("&grey&Left Click to a stack"))
                    .addLore("")
                    .addLore(TextHelper.parseFancyString("&grey&Right Click to sell all"));

            itemStack.setAmount(1);

            contents.add(itemStack);
            setCallback(itemStack, e ->{
                if (e.isShiftClick() && e.isLeftClick()){

                }
                if (e.isShiftClick() && e.isRightClick()){

                }
                if (e.isLeftClick()){
                    upgradableChest.removeItem(item.getKey(), BigInteger.valueOf(64));
                    ItemUtils.give((Player) e.getWhoClicked(),itemStack,64);
                    try {
                        placedUpgradableChestDao.update(upgradableChest);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                if (e.isRightClick()){

                }
            });


        }






        super.populate();
    }
}
