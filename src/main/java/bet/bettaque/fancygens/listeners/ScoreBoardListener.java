package bet.bettaque.fancygens.listeners;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedAutosellChest;
import bet.bettaque.fancygens.helpers.ScoreHelper;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreBoardListener implements Listener {
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    static Dao<PlacedAutosellChest, Integer> placedAutosellChestDao;
    Economy econ;

    public ScoreBoardListener(Dao<GeneratorPlayer, String> generatorPlayerDao, Dao<PlacedAutosellChest, Integer> placedAutosellChestDao, Economy econ) {
        this.generatorPlayerDao = generatorPlayerDao;
        ScoreBoardListener.placedAutosellChestDao = placedAutosellChestDao;
        this.econ = econ;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        ScoreHelper scoreHelper = ScoreHelper.createScore(player);
        try {
            GeneratorPlayer generatorPlayer = generatorPlayerDao.queryForId(player.getUniqueId().toString());
            scoreHelper.setTitle("&7&m---- &eFancy&aGens &7&m----");
            setScoreboard(player, scoreHelper, generatorPlayer, econ);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void setScoreboard(Player player, ScoreHelper scoreHelper, GeneratorPlayer generatorPlayer, Economy econ) {

//        StringBuilder graph = new StringBuilder();
//        ArrayList<Double> lastSells = generatorPlayer.getLastSells();
//        if (lastSells == null) return;
//
//        double max = Collections.max(lastSells);
//        double min = Collections.min(lastSells);
//        double divident = (max-min) / 8;
//        for (double sell: generatorPlayer.getLastSells().subList(0,16)){
//            int index = (int) ((sell - min) / divident);
//            if (index > 8) index = 8;
//            if (index < 1) index = 1;
//            String graphIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_graph_" + index + "%");
//            graph.append(graphIcon);
//        }

//        scoreHelper.setSlot(8 , ChatColor.YELLOW + graph.toString());
//        scoreHelper.setSlot(7 , " ");

//        String statsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_stats%");
//        scoreHelper.setSlot(7 , statsIcon + " " + TextHelper.formatCurrency(generatorPlayer.getLastSellsAvrg(), player));

        String prestigeIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_prestige%");
        scoreHelper.setSlot(6, ChatColor.GOLD + prestigeIcon + " " + generatorPlayer.getPrestige());

        String scoreIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_points%");
        scoreHelper.setSlot(5, ChatColor.AQUA + scoreIcon + " "  + TextHelper.formatScore(generatorPlayer.getScore()));

        String coinsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_coins_scoreb%");
        scoreHelper.setSlot(4, ChatColor.YELLOW + coinsIcon + " " + TextHelper.withSuffix(econ.getBalance(player)));

        String gemsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_gems%");
        scoreHelper.setSlot(3, ChatColor.GREEN + gemsIcon + " "+ (int) generatorPlayer.getGems());

        String slotsIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_slots%");
        scoreHelper.setSlot(2, ChatColor.WHITE + slotsIcon + " "+ generatorPlayer.getUsedGens() + " / " + generatorPlayer.getMaxGens());

        String multiIcon = PlaceholderAPI.setPlaceholders(player, "%oraxen_multi%");
        scoreHelper.setSlot(1, ChatColor.LIGHT_PURPLE + multiIcon + " x" + TextHelper.formatMultiplier(generatorPlayer.getMultiplier(), false, player));

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if (ScoreHelper.hasScore(player)){
            ScoreHelper.removeScore(player);
        }
    }
}
