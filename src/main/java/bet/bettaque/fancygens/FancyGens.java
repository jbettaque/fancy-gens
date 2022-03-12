package bet.bettaque.fancygens;

import bet.bettaque.fancygens.commands.*;
import bet.bettaque.fancygens.commands.goblins.GoblinCommands;
import bet.bettaque.fancygens.commands.goblins.GoblinTier;
import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.config.MineConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedAutosellChest;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.helpers.PersistanceHelper;
import bet.bettaque.fancygens.listeners.*;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import me.angeschossen.lands.api.integration.LandsIntegration;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

public final class FancyGens extends JavaPlugin {
    private static final Logger log = Logger.getLogger("FancyGens");
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    Dao<PlacedAutosellChest, Integer> placedAutosellChestDao;
    GeneratorHandler generatorHandler;
    AutosellChestHandler autosellChestHandler;
    ScoreBoardHandler scoreBoardHandler;
    ConfigManager gensConfig;
    Economy econ = null;
    LandsIntegration landsIntegration;
    FancyEconomy economy;

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

    public MineConfig findMine(String mine){

        for (MineConfig mineConfig : GensConfig.mines) {
            if (mine.equals(mineConfig.getName())) return mineConfig;
        }

        return new MineConfig();
    }

    public ArrayList<String> generatorNames(){
        ArrayList<String> rval = new ArrayList<>();

        for (Map.Entry<Integer, GenConfig> set: GensConfig.gens.entrySet()) {
            rval.add(set.getValue().name);
        }
        return rval;
    }

    public ArrayList<String> mineNames(){
        ArrayList<String> rval = new ArrayList<>();

        for (MineConfig mineConfig: GensConfig.mines) {
            rval.add(mineConfig.getName());
        }
        return rval;
    }

    private Material getMaterial(String string){
        return Material.getMaterial(string);
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getLogger().info("Starting FancyGens!");
        Messages.load(this);

        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.landsIntegration = new LandsIntegration(this);
        gensConfig = ConfigManager.create(this).target(GensConfig.class).saveDefaults().load();
        gensConfig.save();
//        GensConfig.gens.put(0, new GenConfig());
//        gensConfig.save();

//        GensConfig.shopItems.put(Material.WHEAT, 1.0);
        gensConfig.save();

        String databaseUrl = "jdbc:sqlite:" + this.getDataFolder().toPath().resolve("users.db");
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(databaseUrl);

            generatorPlayerDao = DaoManager.createDao(connectionSource, GeneratorPlayer.class);
            TableUtils.createTableIfNotExists(connectionSource, GeneratorPlayer.class);

            placedGeneratorDao = DaoManager.createDao(connectionSource, PlacedGenerator.class);
            TableUtils.createTableIfNotExists(connectionSource, PlacedGenerator.class);

            placedAutosellChestDao = DaoManager.createDao(connectionSource, PlacedAutosellChest.class);
            TableUtils.createTableIfNotExists(connectionSource, PlacedAutosellChest.class);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.economy = new FancyEconomy(generatorPlayerDao, this, econ);



        ArgType<GenConfig> generatorType = new ArgType<>("generator", this::findGenerator).tabStream(c -> generatorNames().stream());
        ArgType<MineConfig> mineType = new ArgType<>("mine", this::findMine).tabStream(c -> mineNames().stream());
        ArgType<GoblinTier> scrollTier = new ArgType<>("stier", GoblinTier::get).tabStream(c -> Arrays.stream(GoblinTier.values()).map(GoblinTier::getItemId));
        ArgType<FancyResource> resourceType = new ArgType<>("resource", FancyResource::get).tabStream(c -> Arrays.stream(FancyResource.values()).map(FancyResource::toString));
        ArgType<Material> materialType = new ArgType<>("material", this::getMaterial).tabStream(c -> Arrays.stream(Material.values()).map(Material::toString));
        ArgType<Material> blockType = new ArgType<>("block", this::getMaterial).tabStream(c -> Arrays.stream(Material.values()).filter(Material::isBlock).filter(Material::isSolid).filter(Material::isOccluding).map(Material::toString));

        GeneratorCommands generatorCommands = new GeneratorCommands(this, generatorPlayerDao, gensConfig);
        AdminCommands adminCommands = new AdminCommands(this, generatorPlayerDao, economy);
        ShopCommands shopCommands = new ShopCommands(this, econ, generatorCommands, this.generatorPlayerDao, adminCommands);
        MineCommands mineCommands = new MineCommands(this, gensConfig, generatorPlayerDao);
        UiCommands uiCommands = new UiCommands(shopCommands, generatorPlayerDao, econ, mineCommands, landsIntegration);
        GoblinCommands goblinCommands = new GoblinCommands();

        new CommandParser(this.getResource("commands.rdcml")).setArgTypes(
                generatorType,
                mineType,
                scrollTier,
                resourceType,
                materialType,
                blockType
        ).parse().register("fancygens",
                generatorCommands,
                shopCommands,
                adminCommands,
                uiCommands,
                mineCommands,
                goblinCommands

        );

        UpgradeGeneratorListener upgradeGeneratorListener = new UpgradeGeneratorListener(this, this.placedGeneratorDao, this.econ);
        getServer().getPluginManager().registerEvents(new FirstJoinListener(generatorPlayerDao), this);
        getServer().getPluginManager().registerEvents(new PlaceGeneratorListener(this, generatorPlayerDao, placedGeneratorDao), this);
        getServer().getPluginManager().registerEvents(new BreakGeneratorListener(this, this.placedGeneratorDao, this.generatorPlayerDao), this);
        getServer().getPluginManager().registerEvents(upgradeGeneratorListener, this);
        getServer().getPluginManager().registerEvents(new ScoreBoardListener(this.generatorPlayerDao, placedAutosellChestDao, this.econ), this);
        getServer().getPluginManager().registerEvents(new SellWandListener(this, econ, shopCommands), this);
        getServer().getPluginManager().registerEvents(new PistonMoveListener(this), this);
        getServer().getPluginManager().registerEvents(new PlaceAutosellChestListener(this, placedAutosellChestDao), this);
        getServer().getPluginManager().registerEvents(new BreakAutosellChestListener(this, adminCommands, placedAutosellChestDao), this);
        getServer().getPluginManager().registerEvents(new UpgradeWandListener(this, placedGeneratorDao, upgradeGeneratorListener), this);
        getServer().getPluginManager().registerEvents(new MainMenuListener(this, uiCommands), this);
        getServer().getPluginManager().registerEvents(new MineListener(this, econ, generatorPlayerDao, goblinCommands), this);
        getServer().getPluginManager().registerEvents(new ScrollListener(this, economy), this);


        generatorHandler = new GeneratorHandler(placedGeneratorDao, this);
        new BukkitRunnable() {
            public void run() {
                generatorHandler.generateResources();
            }
        }.runTaskTimer(this, 0L, 20L * 17);

        autosellChestHandler = new AutosellChestHandler(placedAutosellChestDao, shopCommands);
        new BukkitRunnable() {
            public void run() {
                autosellChestHandler.handleAutosellChests();
            }
        }.runTaskTimer(this, 0L, 20L * 60);

        scoreBoardHandler = new ScoreBoardHandler(generatorPlayerDao, econ);
        new BukkitRunnable() {
            public void run() {
                for(Player player: Bukkit.getOnlinePlayers()){
                    scoreBoardHandler.updateScoreBoard(player);
                }

            }
        }.runTaskTimer(this, 10L, 20L);

        scoreBoardHandler = new ScoreBoardHandler(generatorPlayerDao, econ);
        new BukkitRunnable() {
            public void run() {
                PersistanceHelper.cleanupMineGains();

            }
        }.runTaskTimer(this, 10L, 20L);

        for(MineConfig mine : GensConfig.mines){
            mine.start(this);
        }
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
