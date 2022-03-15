package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.commands.UiCommands;
import bet.bettaque.fancygens.helpers.TextHelper;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class MainMenuGui extends FancyGui {
    UiCommands uiCommands;
    ShopCommands shopCommands;
    public MainMenuGui(Player player, UiCommands uiCommands, ShopCommands shopCommands) {
        super(TextHelper.parseFancyString("&#E91E63-#2196F3&**FancyGens** Main Menu"), 9, player);
        this.uiCommands = uiCommands;
        this.shopCommands = shopCommands;
        this.center = true;
    }

    @Override
    public void populate() {
        ItemStack prestigeIcon = OraxenItems.getItemById("prestige_icon").build();
        ItemStack genshopIcon = OraxenItems.getItemById("genshop_icon").build();
        ItemStack slotShopIcon = OraxenItems.getItemById("slots_icon").build();
        ItemStack gemShopIcon = OraxenItems.getItemById("gems_icon").build();
        ItemStack sellAllIcon = OraxenItems.getItemById("coins_icon").build();
        ItemStack minesIcon = OraxenItems.getItemById("mines_icon").build();
        ItemStack homeIcon = OraxenItems.getItemById("home").build();
        ItemStack hopperIcon = new ItemBuilder(Material.HOPPER).setName(TextHelper.parseFancyString("&#3F51B5-#1976D2&**Get Upgradable Hopper**")).setLore(TextHelper.parseFancyString("&#6f737d&» &#D5D6D8&Left click the hopper!"));
        ItemStack shopIcon = OraxenItems.getItemById("shop_icon").build();


        contents.add(minesIcon);
        callbackMap.put(minesIcon, e -> uiCommands.mines(player));

        contents.add(genshopIcon);
        callbackMap.put(genshopIcon, e -> uiCommands.genShop(player));

        contents.add(gemShopIcon);
        callbackMap.put(gemShopIcon, e -> uiCommands.gemShop(player));

        contents.add(prestigeIcon);
        callbackMap.put(prestigeIcon, e -> uiCommands.prestige(player));

        contents.add(homeIcon);
        callbackMap.put(homeIcon, e -> uiCommands.home(player));

        contents.add(hopperIcon);
        callbackMap.put(hopperIcon, e -> player.performCommand("hopper get confirm"));

        contents.add(shopIcon);
        callbackMap.put(shopIcon, e -> player.performCommand("shop"));

        contents.add(slotShopIcon);
        callbackMap.put(slotShopIcon, e -> uiCommands.slotShop(player));


        contents.add(sellAllIcon);
        callbackMap.put(sellAllIcon, e -> shopCommands.sellAll(player));



        super.populate();
    }
}
