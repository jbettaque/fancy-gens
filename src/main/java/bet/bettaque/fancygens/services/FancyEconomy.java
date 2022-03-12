package bet.bettaque.fancygens.services;

import bet.bettaque.fancygens.FancyResource;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import com.j256.ormlite.dao.Dao;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

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
            case COINS: return getCoinBalance(player);
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

    // ============== COINS ==============
    private double getCoinBalance(Player player){
        return econ.getBalance(player);
    }

    private void setCoins(Player player, double amount){
        double currentBal = econ.getBalance(player);
        econ.withdrawPlayer(player, currentBal);
        econ.depositPlayer(player, amount);
    }

    private void addCoins(Player player, double amount){
        econ.depositPlayer(player, amount);
        addPoints(player, amount);
    }

    private void removeCoins(Player player, double amount){
        econ.depositPlayer(player, amount);
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
            generatorPlayer.incrementScore(amount);
            generatorPlayerDao.update(generatorPlayer);
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



}
