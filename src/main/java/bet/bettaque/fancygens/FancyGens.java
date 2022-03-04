package bet.bettaque.fancygens;

import bet.bettaque.fancygens.commands.GeneratorCommands;
import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.listeners.FirstJoinListener;
import bet.bettaque.fancygens.listeners.PlaceGeneratorListener;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.config.ConfigManager;

import java.sql.SQLException;
import java.util.Arrays;

public final class FancyGens extends JavaPlugin {
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    GeneratorHandler generatorHandler;
    ConfigManager gensConfig;

    public GenConfig findGenerator(String generator){
        for (int i = 0; i < GensConfig.gens.size(); i++) {
            if (generator.equals(GensConfig.gens.get(i).name)){
                return GensConfig.gens.get(i);
            }
        }
        return new GenConfig();
    }

    public String[] generatorNames(){
        String[] rval = new String[GensConfig.gens.size()];

        for (int i = 0; i < GensConfig.gens.size(); i++) {
            rval[i] = GensConfig.gens.get(i).name;
        }
        return rval;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Staring FancyGens!");

        gensConfig = ConfigManager.create(this).target(GensConfig.class).saveDefaults().load();
        gensConfig.save();
        System.out.println(GensConfig.gens);

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


        ArgType<GenConfig> generatorType = new ArgType<>("generator", this::findGenerator).tabStream(c -> Arrays.stream(generatorNames()));

        new CommandParser(this.getResource("commands.rdcml")).setArgTypes(generatorType).parse().register("fancygens", new GeneratorCommands(this));

        getServer().getPluginManager().registerEvents(new FirstJoinListener(generatorPlayerDao), this);
        getServer().getPluginManager().registerEvents(new PlaceGeneratorListener(this, generatorPlayerDao, placedGeneratorDao), this);

        generatorHandler = new GeneratorHandler(placedGeneratorDao);
        new BukkitRunnable() {
            public void run() {
                generatorHandler.generateResources();
            }
        }.runTaskTimer(this, 0L, 20L * 10);
    }






    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
