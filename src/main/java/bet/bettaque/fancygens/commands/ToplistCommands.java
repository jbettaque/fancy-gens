package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ToplistCommands {
    FancyEconomy economy;
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public ToplistCommands(FancyEconomy economy, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.economy = economy;
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @CommandHook("leaderboard")
    public void leaderBoard(Player player){
        try {
            ArrayList<GeneratorPlayer> generatorPlayers  = (ArrayList<GeneratorPlayer>) generatorPlayerDao.queryForAll();
            generatorPlayers = (ArrayList<GeneratorPlayer>) generatorPlayers.stream().sorted(Comparator.comparingInt(GeneratorPlayer::getPrestige)).collect(Collectors.toList());
            Collections.reverse(generatorPlayers);
            player.spigot().sendMessage(TextHelper.parseFancyComponents("&white&~~-----------------------------------------~~"));
            player.spigot().sendMessage(TextHelper.parseFancyComponents("&#4CAF50&Leaderboard"));
            player.spigot().sendMessage(TextHelper.parseFancyComponents("&white&~~-----------------------------------------~~"));

            int c = 1;
            for (GeneratorPlayer generatorPlayer : generatorPlayers) {
                if (c > 15) return;

                OfflinePlayer lbOfflinePlayer = Bukkit.getOfflinePlayer(generatorPlayer.getUUID());

                String prestige = TextHelper.formatPrestige(generatorPlayer.getPrestige(), player);
                String points = TextHelper.formatPoints(generatorPlayer.getScore(), player);
                player.spigot().sendMessage(TextHelper.parseFancyComponents("&aqua& [ " +  c + ". ] " + lbOfflinePlayer.getName() + " &yellow&Prestige: " + prestige + " &yellow&Points: " + points));
                player.spigot().sendMessage(TextHelper.parseFancyComponents("&white&~~-----------------------------------------~~"));
                c+=1;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
