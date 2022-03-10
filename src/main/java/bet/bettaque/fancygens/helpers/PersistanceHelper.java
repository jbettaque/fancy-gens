package bet.bettaque.fancygens.helpers;


import bet.bettaque.fancygens.FancyGens;
import bet.bettaque.fancygens.config.MineConfig;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class PersistanceHelper {
    static final Plugin plugin = FancyGens.getPlugin(FancyGens.class);

    public static final NamespacedKey oreKey = new NamespacedKey(plugin, "ore");

    private static HashMap<MineConfig, BukkitTask> mineTasks = new HashMap<>();

    private static HashMap<Player, MineGain> mineGains = new HashMap<>();

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void setMineRunnable(MineConfig mine, BukkitTask task){
        mineTasks.put(mine, task);
    }

    public static void removeMineRunnable(MineConfig mine){
        mineTasks.get(mine).cancel();
        mineTasks.remove(mine);
    }

    public static double addMineGain(Player player, double gain){
        if (mineGains.containsKey(player)){
            return mineGains.get(player).addGain(gain);
        }

        MineGain mineGain = new MineGain();
        mineGains.put(player, mineGain);
        return mineGain.addGain(gain);
    }

    public static void cleanupMineGains(){
//        mineGains.forEach((Player player, MineGain mineGain) -> {
//            if (mineGain.timestamp.isBefore(Instant.now().minusSeconds(3))){
//                mineGains.remove(player);
//            }
//        });

        for (Map.Entry<Player, MineGain> set : mineGains.entrySet()) {
            if (set.getValue().timestamp.isBefore(Instant.now().minusSeconds(1))){
                mineGains.remove(set.getKey());
            }
        }
    }


}

class MineGain {
    Instant timestamp;
    double gain;

    public MineGain() {
        this.timestamp = Instant.now();
        this.gain = 0;
    }

    public double addGain(double gain){
        this.gain += gain;
        this.timestamp = Instant.now();

        return this.gain;
    }
}
