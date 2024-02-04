package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.services.FancyResource;
import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import io.th0rgal.oraxen.items.OraxenItems;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.TextFormat;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;
import java.util.ArrayList;

public class ShopCommands {
    Plugin plugin;
    GeneratorCommands generatorCommands;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    AdminCommands adminCommands;
    FancyEconomy economy;

    public ShopCommands(Plugin plugin, GeneratorCommands generatorCommands, Dao<GeneratorPlayer, String> generatorPlayerDao, AdminCommands adminCommands, FancyEconomy economy) {
        this.plugin = plugin;
        this.generatorCommands = generatorCommands;
        this.generatorPlayerDao = generatorPlayerDao;
        this.adminCommands = adminCommands;
        this.economy = economy;
    }

    @CommandHook("sellall")
    public void sellAll(Player player){
        sellAllBackend(player, player.getInventory(), 1, false);
    }

    public double sellAllBackend(Player player, Inventory inventory, double multiplier, boolean auto){
        GeneratorPlayer generatorPlayer = null;
        try {
            generatorPlayer = this.generatorPlayerDao.queryForId(player.getUniqueId().toString());
            multiplier += generatorPlayer.getMultiplier() -1;
            double sumMoney = 0;
            int sumCount = 0;

            ArrayList<String> soldStrings = new ArrayList<>();

            NamespacedKey key = new NamespacedKey(plugin, "sellable");

            for (ItemStack item : inventory) {
                if (item != null && item.hasItemMeta()){
                    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
                    if (container.has(key, PersistentDataType.DOUBLE)){
                        double price = container.get(key, PersistentDataType.DOUBLE);
                        ItemStack searchItem = new ItemBuilder(item.getType());
                        ItemUtils.addPersistentTag(searchItem, key, PersistentDataType.DOUBLE, price);
                        ItemUtils.addLore(searchItem,"Price: " + TextHelper.formatCurrency(price, player));

                        int count = ItemUtils.countAndRemove(inventory, searchItem);
                        if (count > 0){
                            double total = count * price * multiplier;
                            sumMoney += total;
                            sumCount += count;
                            economy.add(player, FancyResource.COINS, total);

//                            this.econ.depositPlayer(player, total);
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
                            soldStrings.add(ChatColor.GREEN + "Sold " + ChatColor.YELLOW + count + ChatColor.GREEN + " of " + item.getType() +
                                    " for " + TextHelper.formatCurrency(total, player) + ChatColor.LIGHT_PURPLE + " (x" + TextHelper.formatMultiplier(multiplier, false, player) +")");
                        }
                    }

                }
            }

            if (sumCount > 0){
                if (!auto) {
                    player.sendMessage(ChatColor.STRIKETHROUGH + "-------------------------------------------");
                    for (String soldString : soldStrings) {
                        player.sendMessage(soldString);
                    }
                    player.sendMessage(ChatColor.STRIKETHROUGH + "-------------------------------------------");
                    player.sendMessage(ChatColor.GREEN + "Total Items sold: " + ChatColor.YELLOW + sumCount + ChatColor.GREEN + " for " + TextHelper.formatCurrency(sumMoney, player));
                    generatorPlayer.addSell(sumMoney);
//                this.generatorPlayerDao.update(generatorPlayer);
                    return sumMoney;
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextHelper.parseFancyComponents("&green&Sold: &yellow&" + sumCount + " Items &green&for " + TextHelper.formatCurrency(sumMoney, player)));
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return 0;
        }
        return 0;

    }

    public void buySellWand(Player player, double multiplier, double gems){
        String multiIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_multi%");
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (generatorPlayer.getGems() >= gems ){
                generatorPlayer.withdrawGems(gems);
                generatorPlayerDao.update(generatorPlayer);
                this.adminCommands.giveSellWandBackend(player, multiplier);
                player.sendMessage(Messages.msg("genPurchased") + " " + Messages.msg("sellwandName") + " " + ChatColor.LIGHT_PURPLE + multiIcon +"x" + multiplier);
            } else {
                player.sendMessage(Messages.msg("notEnoughGems") + " " + TextHelper.formatGems(generatorPlayer.getGems(), player) + " / " + TextHelper.formatGems(gems, player));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void buyUpgradeWand(Player player, double gems){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (generatorPlayer.getGems() >= gems ){
                generatorPlayer.withdrawGems(gems);
                generatorPlayerDao.update(generatorPlayer);
                this.adminCommands.giveUpgradeWandBackend(player);
                player.sendMessage(Messages.msg("upgradeWandPurchased"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void buyAnubisHead(Player player, double gems){
        if (economy.getBalance(player, FancyResource.GEMS) >= gems ){
            economy.remove(player, FancyResource.GEMS, gems);
            this.adminCommands.giveAnubisHeadBackend(player);
            player.spigot().sendMessage(TextHelper.parseFancyComponents("&green&You have purchased a Anubis Head"));
        }
    }

    public void buyAutosellChest(Player player, int multiplier, double gems){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (generatorPlayer.getGems() >= gems ){
                generatorPlayer.withdrawGems(gems);
                generatorPlayerDao.update(generatorPlayer);
                this.adminCommands.giveAutosellChestBackend(player, multiplier);
                player.sendMessage(Messages.msg("autosellChestPurchased"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void buyUpgradableChest(Player player, int level, double gemsAmount){
        if (economy.getBalance(player,FancyResource.GEMS) >= gemsAmount ){
            economy.remove(player,FancyResource.GEMS,gemsAmount);
            this.adminCommands.giveUpgradableChestBackend(player,level);
            player.sendMessage(TextHelper.parseFancyString("&green&Purchased a &#2196F3-#FFEB3B&Upgradable Chest"));
        }

    }

    public double calculateGenPrice(GenConfig genConfig, int boost){
        double price = 0;
        for (int i = 1; i <= genConfig.id; i++) {
            price += GensConfig.gens.get(i).getCost(boost);
        }
        return price;
    }

    public double calculateSlotPrice(GeneratorPlayer generatorPlayer){
        return Math.pow(6,generatorPlayer.getTimesPurchasedTokens()) * 120;
    }

    public void buySlots(GeneratorPlayer generatorPlayer, Player player){
        double cost = this.calculateSlotPrice(generatorPlayer);
                        if (economy.getBalance(player, FancyResource.COINS) - cost >= 0){
                            try {
                                generatorPlayer.addMaxGens(10);
                                generatorPlayer.incrementTimesPurchasedTokens();
                                generatorPlayerDao.update(generatorPlayer);
                                economy.remove(player, FancyResource.COINS, cost);
//                                econ.withdrawPlayer(player, cost);
                                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                                player.sendMessage(Messages.msg("genPurchased") + " 10 generator slots" + ChatColor.GREEN + " for " + TextHelper.formatCurrency(cost, player));
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
                        } else {
                            player.sendMessage(Messages.msg("notEnoughMoney") + " " + TextHelper.formatCurrency(economy.getBalance(player, FancyResource.COINS), player) + " / " + TextHelper.formatCurrency(this.calculateSlotPrice(generatorPlayer), player));
                        }
    }



    public void buyGenerator(GenConfig genConfig, Player player, int amount, int boost){

        double total = calculateGenPrice(genConfig, boost) * amount;
        double balance = economy.getBalance(player, FancyResource.COINS);

        if (balance >= total){
            economy.remove(player, FancyResource.COINS, total);
            this.generatorCommands.giveGensBackend(genConfig, player, amount, boost);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            player.sendMessage(Messages.msg("genPurchased") + " " + genConfig.name + ChatColor.GREEN +" for " + TextHelper.formatCurrency(total, player));
        } else {
            player.sendMessage(Messages.msg("notEnoughMoney") + " " + TextHelper.formatCurrency(balance, player) + " / " + TextHelper.formatCurrency(total, player));
        }

    }

    public void buyMiningPickaxe(Player player, int gems){
        double balance = economy.getBalance(player, FancyResource.COINS);
        if (balance >= gems){
            economy.remove(player, FancyResource.GEMS, gems);
            ItemStack pickaxe = OraxenItems.getItemById("obsidian_pickaxe").build();
            ItemUtils.give(player, pickaxe);
            player.sendMessage(Messages.msg("genPurchased") + " " + pickaxe.getItemMeta().getDisplayName());
        } else {
            player.sendMessage(Messages.msg("notEnoughGems") + " " + TextHelper.formatGems(balance, player) + " / " + TextHelper.formatGems(gems, player));
        }

    }

}
