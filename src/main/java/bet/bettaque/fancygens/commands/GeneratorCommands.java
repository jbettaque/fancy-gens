package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.config.GenConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.tags.ItemTagType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

public class GeneratorCommands {
    Plugin plugin;

    public GeneratorCommands(Plugin plugin) {
        this.plugin = plugin;
    }

    @CommandHook("givegens")
    public void giveGens(CommandSender sender, GenConfig generator, Player player){
        NamespacedKey key = new NamespacedKey(plugin, "generator");
        ItemStack gen = new ItemBuilder(generator.block).setName(generator.name).setLore("Tier I").addPersistentTag(key, PersistentDataType.STRING, generator.product.toString());
        ItemUtils.give(player, gen);
    }
}
