package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.config.MineConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.gui.*;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import de.themoep.minedown.MineDown;
import io.th0rgal.oraxen.items.OraxenItems;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;
import java.util.Map;

public class UiCommands {
    ShopCommands shopCommands;
    MineCommands mineCommands;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    LandsIntegration landsIntegration;
    Economy econ;

    public UiCommands(ShopCommands shopCommands, Dao<GeneratorPlayer, String> generatorPlayerDao, Economy econ, MineCommands mineCommands, LandsIntegration landsIntegration) {
        this.shopCommands = shopCommands;
        this.generatorPlayerDao = generatorPlayerDao;
        this.econ = econ;
        this.mineCommands = mineCommands;
        this.landsIntegration = landsIntegration;
    }

    @CommandHook("home")
    public void home(Player player){
        Land ownedLand = landsIntegration.getLandPlayer(player.getUniqueId()).getOwningLand();
        if (ownedLand != null) {
            player.teleport(ownedLand.getSpawn());
        } else {
            player.spigot().sendMessage(TextHelper.parseFancyComponents("&red&You don't have a land you could teleport to! Teleporting you to the spawn instead!"));
            player.spigot().sendMessage(TextHelper.parseFancyComponents("[Click HERE to create a land claim (Make sure you stand where you want your land to be!)](suggest_command=/l claim color=yellow)"));
            player.performCommand("spawn");
        }
    }

    @CommandHook("gemshop")
    public void gemShop(Player player){
        MainMenuGui mainMenuGui = new MainMenuGui(
                player,
                this,
                shopCommands
        );
        GemShopGui slotShopGui = new GemShopGui(
                player,
                mainMenuGui,
                shopCommands
        );
        slotShopGui.populate();
    }

    @CommandHook("prestige")
    public void prestige(Player player){
        MainMenuGui mainMenuGui = new MainMenuGui(
                player,
                this,
                shopCommands
        );
        PrestigeGui prestigeGui = new PrestigeGui(
                player,
                mainMenuGui,
                this,
                shopCommands,
                generatorPlayerDao
        );
        prestigeGui.populate();
    }

    public void buyPrestige(GeneratorPlayer generatorPlayer, Player player){
        double cost = this.calculatePrestigePrice(generatorPlayer);
        double requirement  = this.calculatePrestigeRequirement(generatorPlayer);
        if (econ.getBalance(player) - cost >= 0){
            if (generatorPlayer.getScore() >= requirement){
                generatorPlayer.incrementPrestige();
                econ.withdrawPlayer(player, cost);
                generatorPlayer.addGems(2000);
                generatorPlayer.incrementMultiplier(0.04 * generatorPlayer.getPrestige() / 2);
                generatorPlayer.resetScore();
                try {
                    generatorPlayerDao.update(generatorPlayer);
                    player.sendMessage(ChatColor.GREEN + "Reset Points & Gained 1 Prestige for " + TextHelper.formatCurrency(cost, player));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                player.sendMessage(Messages.msg("scoreNotHighEnough") + " " + ChatColor.AQUA + TextHelper.formatScore(generatorPlayer.getScore()) + " / " + TextHelper.formatScore(requirement));
            }
        } else {
            player.sendMessage(Messages.msg("notEnoughMoney") + " " + TextHelper.formatCurrency(econ.getBalance(player), player) + " / " + TextHelper.formatCurrency(this.calculatePrestigePrice(generatorPlayer), player));
        }
    }

    public double calculatePrestigePrice(GeneratorPlayer generatorPlayer){
        return Math.pow(12,generatorPlayer.getPrestige()) * 1000000 * (generatorPlayer.getPrestige() + 1);
    }

    public double calculatePrestigeRequirement(GeneratorPlayer generatorPlayer){
        if (generatorPlayer.getPrestige() > 9) return Math.pow(14,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige() * generatorPlayer.getPrestige()) +1);
        return Math.pow(14,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige()) +1);
    }

    @CommandHook("genshop")
    public void genShop(Player player){
        MainMenuGui mainMenuGui = new MainMenuGui(
                player,
                this,
                shopCommands
        );
        GeneratorShopGui generatorShopGui = new GeneratorShopGui(
                player,
                mainMenuGui,
                shopCommands
        );
        generatorShopGui.populate();

    }

    @CommandHook("buyslots")
    public void slotShop(Player player){
        MainMenuGui mainMenuGui = new MainMenuGui(
                player,
                this,
                shopCommands
        );
        SlotShopGui slotShopGui = new SlotShopGui(
                player,
                mainMenuGui,
                this,
                shopCommands,
                generatorPlayerDao
        );
        slotShopGui.populate();

    }

    @CommandHook("menu")
    public void openMenu(Player player){
        MainMenuGui mainMenuGui = new MainMenuGui(
                player,
                this,
                shopCommands
        );
        mainMenuGui.populate();
    }

    @CommandHook("mines")
    public void mines(Player player){
        MainMenuGui mainMenuGui = new MainMenuGui(
                player,
                this,
                shopCommands
        );
        MinesGui minesGui = new MinesGui(
                player,
                mainMenuGui,
                mineCommands

        );
        minesGui.populate();
    }


}
