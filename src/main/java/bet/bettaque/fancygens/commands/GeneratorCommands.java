package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.config.GensConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.helpers.TextHelper;
import com.j256.ormlite.dao.Dao;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;

public class GeneratorCommands {
    Plugin plugin;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    ConfigManager gensConfig;

    public GeneratorCommands(Plugin plugin, Dao<GeneratorPlayer, String> generatorPlayerDao, ConfigManager gensConfig) {
        this.plugin = plugin;
        this.generatorPlayerDao = generatorPlayerDao;
        this.gensConfig = gensConfig;
    }

    @CommandHook("givegens")
    public void giveGens(CommandSender sender, GenConfig generator, Player player, int amount, int boost){
        this.giveGensBackend(generator, player, amount, boost);
    }

    @CommandHook("setmaxgens")
    public void setMaxGens(Player sender, Player player, int maxGens){
        try {
            GeneratorPlayer generatorPlayer = this.generatorPlayerDao.queryForId(player.getUniqueId().toString());
            generatorPlayer.setMaxGens(maxGens);
            this.generatorPlayerDao.update(generatorPlayer);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void giveGensBackend(GenConfig generator, Player player, int amount, int boost){
        NamespacedKey key = new NamespacedKey(plugin, "generator");
        NamespacedKey boostKey = new NamespacedKey(plugin, "generator_boost");
        ItemStack gen = new ItemBuilder(generator.block)
                .setName(TextHelper.parseFancyString("&#4DB6AC&" + generator.name))
                .addLore(TextHelper.parseFancyString("&gray&Tier: &yellow&" + generator.id))
                .addPersistentTag(key, PersistentDataType.INTEGER, generator.id)
                .addPersistentTag(boostKey, PersistentDataType.INTEGER, boost);
        ItemUtils.addEnchant(gen, Enchantment.DURABILITY, 1);
        ItemUtils.addItemFlags(gen, ItemFlag.HIDE_ENCHANTS);
        ItemUtils.give(player, gen, amount);
    }

    @CommandHook("addgen")
    public void addGen(CommandSender sender, Material block, Material product, String name){
        int index = GensConfig.gens.size() + 1;
        GenConfig newGen = new GenConfig();
        newGen.id = index;
        newGen.block = block;
        newGen.product = product;
        newGen.name = name;
        GensConfig.gens.put(index, newGen);
        gensConfig.save();
        sender.spigot().sendMessage(TextHelper.parseFancyComponents(
                "&green&You have added " + name + "(Block: " + block + ", Produces: " + product + ")"
        ));
    }

}
