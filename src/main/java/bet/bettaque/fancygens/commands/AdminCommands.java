package bet.bettaque.fancygens.commands;

import bet.bettaque.fancygens.FancyResource;
import bet.bettaque.fancygens.db.GeneratorPlayer;
import bet.bettaque.fancygens.db.PlacedAutosellChest;
import bet.bettaque.fancygens.db.PlacedGenerator;
import bet.bettaque.fancygens.helpers.TextHelper;
import bet.bettaque.fancygens.services.FancyEconomy;
import com.j256.ormlite.dao.Dao;
import com.jeff_media.customblockdata.CustomBlockData;
import io.th0rgal.oraxen.items.OraxenItems;
import me.angeschossen.lands.api.flags.Flags;
import me.angeschossen.lands.api.integration.LandsIntegration;
import me.angeschossen.lands.api.land.Area;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.itemutils.ItemBuilder;
import redempt.redlib.itemutils.ItemUtils;

import java.sql.SQLException;

public class AdminCommands {
    Plugin plugin;
    Dao<GeneratorPlayer, String> generatorPlayerDao;
    FancyEconomy economy;
    Dao<PlacedGenerator, Integer> placedGeneratorDao;
    LandsIntegration landsIntegration;

    public AdminCommands(Plugin plugin, Dao<GeneratorPlayer, String> generatorPlayerDao, FancyEconomy economy, Dao<PlacedGenerator, Integer> placedGeneratorDao, LandsIntegration landsIntegration) {
        this.plugin = plugin;
        this.generatorPlayerDao = generatorPlayerDao;
        this.economy = economy;
        this.placedGeneratorDao = placedGeneratorDao;
        this.landsIntegration = landsIntegration;
    }

    @CommandHook("givesellwand")
    public void giveSellWand(CommandSender sender, Player player, int multiplier){
        giveSellWandBackend(player, multiplier);
    }

    @CommandHook("setresource")
    public void setResource(CommandSender sender, Player player, FancyResource resource, double amount){
        economy.set(player, resource, amount);
        sender.spigot().sendMessage(TextHelper.parseFancyComponents("&green&Set " + resource.name() + " of " + player.getDisplayName() + " to " + resource.formatValue(amount, player)));
    }

    public void giveSellWandBackend(Player player, double multiplier) {
        NamespacedKey key = new NamespacedKey(plugin, "sellwand");

        ItemStack sellWand = OraxenItems.getItemById("sell_wand").build();
        ItemUtils.addLore(sellWand,"Multiplier x" + multiplier);
        ItemUtils.addPersistentTag(sellWand, key, PersistentDataType.DOUBLE, multiplier);
        ItemUtils.give(player, sellWand);
    }

    @CommandHook("giveupgradewand")
    public void giveUpgradeWand(CommandSender sender, Player player){
        giveUpgradeWandBackend(player);
    }

    public void giveUpgradeWandBackend(Player player) {
        NamespacedKey key = new NamespacedKey(plugin, "upgradewand");
        ItemStack upgradeWand = OraxenItems.getItemById("upgrade_wand").build();
        ItemUtils.addPersistentTag(upgradeWand, key, PersistentDataType.INTEGER, 50);
        ItemUtils.give(player, upgradeWand);
    }

    @CommandHook("giveautosellchest")
    public void giveAutosellChest(CommandSender sender, Player player, int multiplier){
        giveAutosellChestBackend(player, multiplier);
    }

    public void giveAutosellChestBackend(Player player, int multiplier){
        NamespacedKey key = new NamespacedKey(plugin, "autosellchest");
        ItemStack chestItem = new ItemBuilder(Material.CHEST)
                .setName(Messages.msg("autosellChestName"))
                .setLore("Multiplier x" + multiplier)
                .addPersistentTag(key, PersistentDataType.INTEGER, multiplier);

        ItemUtils.addEnchant(chestItem, Enchantment.DURABILITY, 1);
        ItemUtils.addItemFlags(chestItem, ItemFlag.HIDE_ENCHANTS);
        ItemUtils.give(player, chestItem);
    }


    @CommandHook("fixgens")
    public void fixGens(CommandSender sender){
        try {
            for (PlacedGenerator gen : placedGeneratorDao.queryForAll()) {

                NamespacedKey key = new NamespacedKey(plugin, "generator");
                Block block = gen.getLocation().getBlock();
                PersistentDataContainer blockContainer = new CustomBlockData(block, plugin);
                blockContainer.set(key, PersistentDataType.INTEGER, gen.getId());
                block.setType(gen.getMaterial());

                Player player = Bukkit.getPlayer(gen.getOwner());
                if (player == null) return;
                Area area = landsIntegration.getArea(gen.getLocation());
                if (area != null || !area.hasFlag(player.getUniqueId(), Flags.BLOCK_BREAK)) {
                    placedGeneratorDao.delete(gen);
                    GeneratorPlayer genPlayer = generatorPlayerDao.queryForId(gen.getOwner().toString());
                    genPlayer.decrementUsedGens();
                    generatorPlayerDao.update(genPlayer);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
