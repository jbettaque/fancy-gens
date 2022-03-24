package bet.bettaque.fancygens.services;

import bet.bettaque.fancygens.commands.UiCommands;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import net.md_5.bungee.api.ChatMessageType;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.math.BigDecimal;
import java.sql.SQLException;

public class FancyEconomy {
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    Plugin plugin;
    Economy econ;

    public FancyEconomy(Dao<GeneratorPlayer, String> generatorPlayerDao, Plugin plugin, Economy econ) {
        this.generatorPlayerDao = generatorPlayerDao;
        this.plugin = plugin;
        this.econ = econ;
    }

    public double getBalance(Player player, FancyResource resource){
        switch (resource){
            case COINS: return getCoinBalance(player).doubleValue();
            case POINTS: return getPointsBalance(player);
            case PRESTIGE: return getPrestigeBalance(player);
            case GEMS: return getGemBalance(player);
            case SLOTS: return getSlotsBalance(player);
            case MULTIPLIER: return getMultiplierBalance(player);
        }
        return 0;
    }

    public void add(Player player, FancyResource resource, double amount){
        switch (resource){
            case COINS: addCoins(player, amount);
                return;
            case POINTS: addPoints(player, amount);
                return;
            case PRESTIGE: addPrestige(player, amount);
                return;
            case GEMS: addGems(player, amount);
                return;
            case SLOTS: addSlots(player, amount);
                return;
            case MULTIPLIER: addMultiplier(player, amount);
        }
    }

    public void set(Player player, FancyResource resource, double amount){
        switch (resource){
            case COINS: setCoins(player, amount);
                return;
            case POINTS: setPoints(player, amount);
                return;
            case PRESTIGE: setPrestige(player, (int) amount);
                return;
            case GEMS: setGems(player, amount);
                return;
            case SLOTS: setSlots(player, (int) amount);
                return;
            case MULTIPLIER: setMultiplier(player, amount);
        }
    }

    public void remove(Player player, FancyResource resource, double amount){
        switch (resource){
            case COINS: removeCoins(player, amount);
                return;
            case POINTS: removePoints(player, amount);
                return;
            case PRESTIGE: removePrestige(player, (int) amount);
                return;
            case GEMS: removeGems(player, amount);
                return;
            case SLOTS: removeSlots(player, (int) amount);
                return;
            case MULTIPLIER: removeMultiplier(player, amount);
        }
    }

    // ============== COINS ==============
    private BigDecimal getCoinBalance(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            BigDecimal dbBalance = generatorPlayer.getCoins();

            double vaultBalance = econ.getBalance(player);
            if (dbBalance == null){
                return BigDecimal.valueOf(vaultBalance);
            }
            return dbBalance.add(BigDecimal.valueOf(vaultBalance));

        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
    }

    private void setCoins(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (amount > Integer.MAX_VALUE){
                generatorPlayer.setCoins(BigDecimal.valueOf(amount));

            } else {
                double currentBal = econ.getBalance(player);
                econ.withdrawPlayer(player, currentBal);
                econ.depositPlayer(player, amount);
                generatorPlayer.setCoins(BigDecimal.valueOf(0));
            }
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    private void addCoins(Player player, double amount){
//        GeneratorPlayer generatorPlayer = null;
//        try {
//            generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
//            this.addCoinsPointCap(player, amount, UiCommands.calculatePrestigeRequirementS(generatorPlayer) * 1.5);
//
//        } catch (SQLException throwables) {
//            throwables.printStackTrace();
//        }
        if (amount > Integer.MAX_VALUE || getCoinBalance(player).compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0){
            try {
                GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
                generatorPlayer.addCoins(BigDecimal.valueOf(amount));
//                generatorPlayer.incrementScore(amount);
                if (UiCommands.calculatePrestigeRequirementS(generatorPlayer) * 1.5 >= generatorPlayer.getScore() + amount){
                    generatorPlayer.incrementScore(amount);
                } else {
                    generatorPlayer.setScore(UiCommands.calculatePrestigeRequirementS(generatorPlayer) * 1.5);
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextHelper.parseFancyComponents("&yellow&You have reached the point limit for your prestige level!"));

                }
                generatorPlayerDao.update(generatorPlayer);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            econ.depositPlayer(player, amount);
            addPoints(player, amount);
        }
    }

    public void addCoinsPointCap(Player player, double amount, double cap){
        if (amount > Integer.MAX_VALUE || getCoinBalance(player).compareTo(BigDecimal.valueOf(Integer.MAX_VALUE)) > 0){
            try {
                GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());

//                generatorPlayer.incrementScore(amount);
                if (cap >= generatorPlayer.getScore() + amount){
                    generatorPlayer.incrementScore(amount);
                } else {
                    if (cap > generatorPlayer.getScore()){
                        generatorPlayer.setScore(cap);
                    }
                    player.spigot().sendMessage(TextHelper.parseFancyComponents("&yellow&You have reached the point limit for this mine!"));
                }

                if (cap >= getBalance(player, FancyResource.COINS) + amount) {
                    generatorPlayer.addCoins(BigDecimal.valueOf(amount));
                } else {
                    player.spigot().sendMessage(TextHelper.parseFancyComponents("&yellow&You have reached the coin limit for this mine!"));
                }
                generatorPlayerDao.update(generatorPlayer);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        } else {
            econ.depositPlayer(player, amount);
            addPoints(player, amount);
        }
    }

    private void removeCoins(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());

            if (generatorPlayer.getCoins() != null && generatorPlayer.getCoins().doubleValue() > amount){
                generatorPlayer.setCoins(generatorPlayer.getCoins().subtract(BigDecimal.valueOf(amount)));
            } else {
                if (generatorPlayer.getCoins() != null) {
                    amount -= generatorPlayer.getCoins().doubleValue();
                }
                generatorPlayer.setCoins(BigDecimal.valueOf(0));
                econ.withdrawPlayer(player, amount);
            }
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // ============== GEMS ==============
    private double getGemBalance(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            return generatorPlayer.getGems();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    private void addGems(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.depositGems(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setGems(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setGems(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removeGems(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.withdrawGems(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // ============== POINTS ==============

    private double getPointsBalance(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            return generatorPlayer.getScore();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    private void addPoints(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            if (UiCommands.calculatePrestigeRequirementS(generatorPlayer) * 1.5 >= generatorPlayer.getScore() + amount){
                generatorPlayer.incrementScore(amount);
                generatorPlayerDao.update(generatorPlayer);
            } else {
                generatorPlayer.setScore(UiCommands.calculatePrestigeRequirementS(generatorPlayer) * 1.5);
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextHelper.parseFancyComponents("&yellow&You have reached the point limit for your prestige level!"));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setPoints(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setScore(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removePoints(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setScore(generatorPlayer.getScore() - amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void resetPoints(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.resetScore();
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // ============== SLOTS ==============

    private double getSlotsBalance(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            return generatorPlayer.getMaxGens();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    private void addSlots(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.addMaxGens((int) amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setSlots(Player player, int amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setMaxGens(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removeSlots(Player player, int amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setMaxGens(generatorPlayer.getMaxGens() - amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    // ============== MULTIPLIER ==============

    private double getMultiplierBalance(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            return generatorPlayer.getMultiplier();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    private void addMultiplier(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.incrementMultiplier(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setMultiplier(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setMultiplier(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removeMultiplier(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setMultiplier(generatorPlayer.getMultiplier() - amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // ============== PRESTIGE ==============

    private double getPrestigeBalance(Player player){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            return generatorPlayer.getMultiplier();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0;
    }

    private void addPrestige(Player player, double amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.incrementMultiplier(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void setPrestige(Player player, int amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setPrestige(amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void removePrestige(Player player, int amount){
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setPrestige(generatorPlayer.getPrestige() - amount);
            generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



}
