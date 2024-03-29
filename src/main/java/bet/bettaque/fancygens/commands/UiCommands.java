package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.services.FancyResource;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.gui.*;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Land;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;

import java.sql.SQLException;

public class UiCommands {
    ShopCommands shopCommands;
    MineCommands mineCommands;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    LandsIntegration landsIntegration;
    FancyEconomy economy;

    public UiCommands(ShopCommands shopCommands, MineCommands mineCommands, Dao<GeneratorPlayer, String> generatorPlayerDao, LandsIntegration landsIntegration, FancyEconomy economy) {
        this.shopCommands = shopCommands;
        this.mineCommands = mineCommands;
        this.generatorPlayerDao = generatorPlayerDao;
        this.landsIntegration = landsIntegration;
        this.economy = economy;
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
        if (economy.getBalance(player, FancyResource.COINS) - cost >= 0){
            if (generatorPlayer.getScore() >= requirement){
                generatorPlayer.incrementPrestige();
                economy.remove(player, FancyResource.COINS, cost);
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
            player.sendMessage(Messages.msg("notEnoughMoney") + " " + TextHelper.formatCurrency(economy.getBalance(player, FancyResource.COINS), player) + " / " + TextHelper.formatCurrency(this.calculatePrestigePrice(generatorPlayer), player));
        }
    }

    public double calculatePrestigePrice(GeneratorPlayer generatorPlayer){
        return calculatePrestigePriceS(generatorPlayer);
    }

    public static double calculatePrestigePriceS(GeneratorPlayer generatorPlayer){
        return Math.pow(13,generatorPlayer.getPrestige()) * 1000000 * (generatorPlayer.getPrestige() + 1);
    }

    public double calculatePrestigeRequirement(GeneratorPlayer generatorPlayer){
        return calculatePrestigeRequirementS(generatorPlayer);
    }

    public static double calculatePrestigeRequirementS(GeneratorPlayer generatorPlayer){
        if (generatorPlayer.getPrestige() > 200) return Math.pow(28,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige() * generatorPlayer.getPrestige()) +1);
        if (generatorPlayer.getPrestige() > 100) return Math.pow(24,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige() * generatorPlayer.getPrestige()) +1);
        if (generatorPlayer.getPrestige() > 29) return Math.pow(18,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige() * generatorPlayer.getPrestige()) +1);
        if (generatorPlayer.getPrestige() > 9) return Math.pow(14,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige() * generatorPlayer.getPrestige()) +1);
        return Math.pow(14,generatorPlayer.getPrestige()) * 2500000 * ((generatorPlayer.getPrestige()) +1);
    }

    public static double calculatePrestigeRequirementS(int prestige){
        if (prestige > 200) return Math.pow(28,prestige) * 2500000 * ((prestige * prestige) +1);
        if (prestige > 100) return Math.pow(24,prestige) * 2500000 * ((prestige * prestige) +1);
        if (prestige > 29) return Math.pow(18,prestige) * 2500000 * ((prestige * prestige) +1);
        if (prestige > 9) return Math.pow(14,prestige) * 2500000 * ((prestige * prestige) +1);
        return Math.pow(14,prestige) * 2500000 * ((prestige) +1);
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
