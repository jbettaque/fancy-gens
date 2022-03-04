package bet.bettaque.fancygens;

import bet.bettaque.fancygens.commands.GeneratorCommands;
import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.listeners.BreakGeneratorEvent;
import bet.bettaque.fancygens.listeners.FirstJoinListener;
import bet.bettaque.fancygens.listeners.PlaceGeneratorListener;
import bet.bettaque.fancygens.listeners.UpgradeGeneratorListener;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.config.ConfigManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class FancyGens extends JavaPlugin {
    private static final Logger log = Logger.getLogger("FancyGens");
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    GeneratorHandler generatorHandler;
    ConfigManager gensConfig;
    Economy econ = null;

    public GenConfig findGenerator(String generator){

        for (Map.Entry<Integer, GenConfig> set: GensConfig.gens.entrySet()) {
            if (generator.equals(set.getValue().name)) return set.getValue();
        }
//        for (int i = 1; i < GensConfig.gens.size() -1; i++) {
//            GensConfig.gens.get(i);
//            if (generator.equals(GensConfig.gens.get(i).name)){
//                return GensConfig.gens.get(i);
//            }
//        }
        return new GenConfig();
    }

    public ArrayList<String> generatorNames(){
        ArrayList<String> rval = new ArrayList<>();

        for (Map.Entry<Integer, GenConfig> set: GensConfig.gens.entrySet()) {
            rval.add(set.getValue().name);
        }
        return rval;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Staring FancyGens!");

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        gensConfig = ConfigManager.create(this).target(GensConfig.class).saveDefaults().load();
        gensConfig.save();
        System.out.println(GensConfig.gens);
        GensConfig.gens.put(0, new GenConfig());
        gensConfig.save();

        String databaseUrl = "jdbc:sqlite:" + this.getDataFolder().toPath().resolve("users.db");
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);
            generatorPlayerDao = DaoManager.createDao(connectionSource, GeneratorPlayer.class);
            TableUtils.createTableIfNotExists(connectionSource, GeneratorPlayer.class);
            placedGeneratorDao = DaoManager.createDao(connectionSource, PlacedGenerator.class);
            TableUtils.createTableIfNotExists(connectionSource, PlacedGenerator.class);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        ArgType<GenConfig> generatorType = new ArgType<>("generator", this::findGenerator).tabStream(c -> generatorNames().stream());

        new CommandParser(this.getResource("commands.rdcml")).setArgTypes(generatorType).parse().register("fancygens", new GeneratorCommands(this));

        getServer().getPluginManager().registerEvents(new FirstJoinListener(generatorPlayerDao), this);
        getServer().getPluginManager().registerEvents(new PlaceGeneratorListener(this, generatorPlayerDao, placedGeneratorDao), this);
        getServer().getPluginManager().registerEvents(new BreakGeneratorEvent(this, this.placedGeneratorDao, this.generatorPlayerDao), this);
        getServer().getPluginManager().registerEvents(new UpgradeGeneratorListener(this, this.placedGeneratorDao, this.econ), this);

        generatorHandler = new GeneratorHandler(placedGeneratorDao);
        new BukkitRunnable() {
            public void run() {
                generatorHandler.generateResources();
            }
        }.runTaskTimer(this, 0L, 20L * 10);
    }


    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }





    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
