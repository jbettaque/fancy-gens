package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.db.PlacedUpgradableChest;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class UpgradableChestGui extends FancyGui{
    PlacedUpgradableChest upgradableChest;
    Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao;


    public UpgradableChestGui(String title, int size, Player player, PlacedUpgradableChest placedUpgradableChest ,    Dao<PlacedUpgradableChest, Integer> placedUpgradableChestDao) {
        super(title, size, player);
        this.upgradableChest = placedUpgradableChest;
        this.placedUpgradableChestDao = placedUpgradableChestDao;
        this.previous = this;
        this.isInputGui = true;
    }

    @Override
    public void handleItemInput(InventoryClickEvent e) {
        ItemStack currentItem = e.getCurrentItem();
        upgradableChest.addItem(currentItem);
        System.out.println(currentItem.toString());
        gui.clearSlot(e.getSlot());
        try {
            placedUpgradableChestDao.update(upgradableChest);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        populate();
    }

    @Override
    public void populate() {
        contents = new ArrayList<>();
        for (Map.Entry<ItemStack, BigInteger> item:upgradableChest.getStoredItems().entrySet()) {
            BigInteger amount = item.getValue();
            ItemStack itemStack = new ItemBuilder( item.getKey())
                    .addLore(TextHelper.parseFancyString("&gray&Amount: "+amount.toString()))
                    .addLore("")
                    .addLore(TextHelper.parseFancyString("&gray&**Left Click to get a stack**"))
                    .addLore("")
                    .addLore(TextHelper.parseFancyString("&gray&**Right Click to sell all**"));

            itemStack.setAmount(1);

            contents.add(itemStack);



            setCallback(itemStack, e ->{


//                if (e.getAction().equals(InventoryAction.)){
//
//                    ItemStack currentItem = e.getCurrentItem();
//                    upgradableChest.addItem(currentItem);
//                    System.out.println(currentItem.toString());
//                    gui.clearSlot(e.getSlot());
//                    try {
//                        placedUpgradableChestDao.update(upgradableChest);
//                    } catch (SQLException ex) {
//                        ex.printStackTrace();
//                    }
//                    populate();
//
//                }
                if (e.getClickedInventory().equals(player.getInventory())){

                }

                if (e.isShiftClick() && e.isLeftClick()){

                }
                if (e.isShiftClick() && e.isRightClick()){

                }
                if (e.isLeftClick()){
                    if (amount.compareTo(BigInteger.valueOf(item.getKey().getMaxStackSize()))>=0){
                        if (upgradableChest.removeItem(item.getKey(), BigInteger.valueOf(item.getKey().getMaxStackSize()))){
                            ItemUtils.give((Player) e.getWhoClicked(),item.getKey(),item.getKey().getMaxStackSize());
                        }
                    }else {
                        if (upgradableChest.removeItem(item.getKey(), amount)){
                            ItemUtils.give((Player) e.getWhoClicked(),item.getKey(),amount.intValue());
                        }
                    }

                    try {
                        placedUpgradableChestDao.update(upgradableChest);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                    populate();
                    return;
                }
                if (e.isRightClick()){
                    populate();

                    return;
                }

                populate();

            });


        }






        super.populate();
    }
}
