package bet.bettaque.fancygens.gui;

import bet.bettaque.fancygens.commands.ShopCommands;
import bet.bettaque.fancygens.helpers.TextHelper;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class GemShopGui extends FancyGui {
    ShopCommands shopCommands;

    public GemShopGui(Player player, MainMenuGui previous, ShopCommands shopCommands) {
        super(TextHelper.parseFancyString("&#4CAF50-#26A69A&**Gems Store**"), 45, player, previous);
        this.shopCommands = shopCommands;
    }

    @Override
    public void populate() {
        ItemStack sellWand = OraxenItems.getItemById("sell_wand").build();
        ItemStack upgradeWand = OraxenItems.getItemById("upgrade_wand").build();
        ItemStack obsidianPickaxe = OraxenItems.getItemById("obsidian_pickaxe").build();
        ItemStack anubisHead = OraxenItems.getItemById("anubis_head").build();
        ItemStack autosellChest = new ItemBuilder(Material.CHEST).setName(Messages.msg("autosellChestName"));


        this.contents.add(upgradeWand);
        this.setCallback(upgradeWand, e -> {
            shopCommands.buyUpgradeWand(player, 800);
        });
        this.setLore(upgradeWand, Arrays.asList(TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatGems(800, player))));


        this.contents.add(obsidianPickaxe);
        this.setCallback(obsidianPickaxe, e -> {
            shopCommands.buyMiningPickaxe(player, 25);
        });
        this.setLore(obsidianPickaxe, Arrays.asList(TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatGems(25, player))));


        this.contents.add(autosellChest);
        this.setCallback(autosellChest, e -> {
            shopCommands.buyAutosellChest(player, 1, 2000);
        });
        this.setLore(autosellChest, Arrays.asList(TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatGems(2000, player)),
                TextHelper.parseFancyString("&gray&Multiplier: " + TextHelper.formatMultiplier(1, true, player))));

        this.contents.add(anubisHead);
        this.setCallback(anubisHead, e -> {
            shopCommands.buyAnubisHead(player, 100);
        });
        this.setLore(anubisHead, Arrays.asList(TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatGems(100, player))));


        for (int i = 0; i < 7; i++) {
            double multiplier = i / 2d + 1;
            ItemStack sellWandTier = new ItemBuilder(sellWand).addLore(TextHelper.parseFancyString("&gray&Multiplier: " + TextHelper.formatMultiplier(multiplier, true, player)));
            this.contents.add(sellWandTier);
            double price = 1000 * i * multiplier;
            this.setCallback(sellWandTier, e -> {
                shopCommands.buySellWand(player, multiplier, price);
            });
            this.setLore(sellWandTier, Arrays.asList(
                    TextHelper.parseFancyString("&gray&Price: " + TextHelper.formatGems(price, player))
            ));
        }



        super.populate();
    }
}
