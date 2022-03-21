package bet.bettaque.fancygens.helpers;


import bet.bettaque.fancygens.FancyGens;
import bet.bettaque.fancygens.FancyResource;
import bet.bettaque.fancygens.config.MineConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.listeners.UpgradeGeneratorListener;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.A;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class PersistanceHelper {
    static final Plugin plugin = FancyGens.getPlugin(FancyGens.class);
    static Dao<GeneratorPlayer, String> generatorPlayerDao;
    static FancyEconomy economy;

    public static final NamespacedKey oreKey = new NamespacedKey(plugin, "ore");

    private static HashMap<MineConfig, BukkitTask> mineTasks = new HashMap<>();

    private static HashMap<Player, MineGain> mineGains = new HashMap<>();

    private static ArrayList<PlacedGenerator> generatorUpgradeQueue = new ArrayList<>();
    private static HashMap<UUID, ArrayList<PlacedGenerator>> generatorUpgradeQueueMap = new HashMap<>();

    public static boolean isPlayerUpgrading(Player player){
        ArrayList<PlacedGenerator> playerQueue = generatorUpgradeQueueMap.get(player.getUniqueId());
        if (playerQueue == null){
            return false;
        } else {
            return playerQueue.size() > 0;
        }
    }

    public static boolean updateGenerator(PlacedGenerator generator){
        generatorUpgradeQueueMap.computeIfAbsent(generator.getOwner(), k -> new ArrayList<>());
        if (generatorUpgradeQueueMap.get(generator.getOwner()).contains(generator)) return false;
        generatorUpgradeQueueMap.get(generator.getOwner()).add(generator);
        return true;


//        if (generatorUpgradeQueue.contains(generator)) return false;
//        generatorUpgradeQueue.add(generator);
//        return true;
    }

    public static void updateOneGeneratorFromQueue(UpgradeGeneratorListener upgradeGeneratorListener, UUID owner){
        ArrayList<PlacedGenerator> list  = generatorUpgradeQueueMap.get(owner);
        if (list == null) return;
        if (generatorUpgradeQueueMap.get(owner).size() > 0){
            upgradeGeneratorListener.updateGenerator(generatorUpgradeQueueMap.get(owner).remove(0));
        }

//        if (generatorUpgradeQueue.size() > 0){
//            upgradeGeneratorListener.updateGenerator(generatorUpgradeQueue.remove(0));
//        }
    }

    public static Plugin getPlugin() {
        return plugin;
    }


    public static void setMineRunnable(MineConfig mine, BukkitTask task){
        mineTasks.put(mine, task);
    }

    public static void init(Dao<GeneratorPlayer, String> genPlayerDao, FancyEconomy fancyEconomy){
        generatorPlayerDao = genPlayerDao;
        economy = fancyEconomy;
    }

    public static void removeMineRunnable(MineConfig mine){
        mineTasks.get(mine).cancel();
        mineTasks.remove(mine);
    }

    private static boolean mineGainsLocked = false;
    private static boolean mineGainsLocked2 = false;

    public static double addMineGain(Player player, double gain, MineConfig mine){
        if (!mineGainsLocked){
            mineGainsLocked2 = true;
            if (mineGains.containsKey(player)){
                mineGainsLocked2 = false;
                return mineGains.get(player).addGain(gain);
            }

            MineGain mineGain = new MineGain(mine.getPointLimit());
            mineGains.put(player, mineGain);
            mineGainsLocked2 = false;
            return mineGain.addGain(gain);

        }
        return 0;
    }

    public static void cleanupMineGains(){
//        mineGains.forEach((Player player, MineGain mineGain) -> {
//            if (mineGain.timestamp.isBefore(Instant.now().minusSeconds(3))){
//                mineGains.remove(player);
//            }
//        });

        if (!mineGainsLocked2){
            mineGainsLocked = true;
            ArrayList<Map.Entry<Player, MineGain>> filtered = (ArrayList<Map.Entry<Player, MineGain>>) mineGains.entrySet().stream().filter(playerMineGainEntry -> playerMineGainEntry.getValue().timestamp.isBefore(Instant.now().minusSeconds(1))).collect(Collectors.toList());
            ArrayList<Map.Entry<Player, MineGain>> filteredNotToClear = (ArrayList<Map.Entry<Player, MineGain>>) mineGains.entrySet().stream().filter(playerMineGainEntry -> playerMineGainEntry.getValue().timestamp.isAfter(Instant.now().minusSeconds(1))).collect(Collectors.toList());

            for (Map.Entry<Player, MineGain> set : filtered) {
                GeneratorPlayer generatorPlayer = null;
                if (set.getValue().timestamp.isBefore(Instant.now().minusSeconds(1))){
                    try {
                        double reward = set.getValue().gain;
                        generatorPlayer = generatorPlayerDao.queryForId(set.getKey().getUniqueId().toString());
                        double pointsMulti = (generatorPlayer.getScore() / 100000) + 1;
                        reward = reward * pointsMulti;
                        reward = reward * generatorPlayer.getMultiplier();


//                        if (reward > 10000000000000000000000000d * (generatorPlayer.getPrestige() * 5 + 1)){
//                            reward = 10000000000000000000000000d * (generatorPlayer.getPrestige() * 5 + 1);
//                            set.getKey().spigot().sendMessage(TextHelper.parseFancyComponents("&yellow&Capped mining gain at " + TextHelper.formatScore(reward)));
//                        }

                        economy.addCoinsPointCap(set.getKey(), reward, set.getValue().mineLimit);
//                        economy.add(set.getKey(), FancyResource.COINS, reward);
                        String message = ChatColor.YELLOW + " + " + TextHelper.formatCurrency(reward, set.getKey());
                        set.getKey().spigot().sendMessage( ChatMessageType.ACTION_BAR, new TextComponent(message));
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    mineGains.remove(set.getKey());
                }
            }

            for (Map.Entry<Player, MineGain> set :
                    filteredNotToClear) {
                GeneratorPlayer generatorPlayer = null;
                try {
                    double reward = set.getValue().gain;
                    generatorPlayer = generatorPlayerDao.queryForId(set.getKey().getUniqueId().toString());
                    double pointsMulti = (generatorPlayer.getScore() / 100000) + 1;
                    reward = reward * pointsMulti;
                    reward = reward * generatorPlayer.getMultiplier();

                    String message = ChatColor.YELLOW + " + " + TextHelper.formatCurrency(reward, set.getKey());
                    set.getKey().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            mineGainsLocked = false;
        }

    }


}

class MineGain {
    Instant timestamp;
    double gain;
    double mineLimit;

    public MineGain(double mineLimit) {
        this.timestamp = Instant.now();
        this.gain = 0;
        this.mineLimit = mineLimit;
    }

    public double addGain(double gain){
        this.gain += gain;
        this.timestamp = Instant.now();

        return this.gain;
    }
}
