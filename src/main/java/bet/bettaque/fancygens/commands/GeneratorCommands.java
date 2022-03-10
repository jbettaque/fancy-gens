package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.config.GenConfig;
import bet.bettaque.fancygens.db.GeneratorPlayer;
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
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;

public class GeneratorCommands {
    Plugin plugin;
    Dao<GeneratorPlayer, String> generatorPlayerDao;

    public GeneratorCommands(Plugin plugin, Dao<GeneratorPlayer, String> generatorPlayerDao) {
        this.plugin = plugin;
        this.generatorPlayerDao = generatorPlayerDao;
    }

    @CommandHook("givegens")
    public void giveGens(CommandSender sender, GenConfig generator, Player player, int amount){
        this.giveGensBackend(generator, player, amount);
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

    public void giveGensBackend(GenConfig generator, Player player, int amount){
        NamespacedKey key = new NamespacedKey(plugin, "generator");
        ItemStack gen = new ItemBuilder(generator.block)
                .setName(generator.name)
                .setLore("Tier " + generator.id)
                .addPersistentTag(key, PersistentDataType.INTEGER, generator.id);
        ItemUtils.addEnchant(gen, Enchantment.DURABILITY, 1);
        ItemUtils.addItemFlags(gen, ItemFlag.HIDE_ENCHANTS);
        ItemUtils.give(player, gen, amount);
    }
}